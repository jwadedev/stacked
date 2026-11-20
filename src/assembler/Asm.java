package assembler;

import static assembler.TokeType.conditional;
import static assembler.TokeType.definition;
import static assembler.TokeType.reference;
import static assembler.Token.ELSE;
import static assembler.Token.ENDIF;
import static assembler.Token.IF;
import static assembler.Token.WEND;
import static assembler.Token.WHILE;
import static instructions.IS1Vals.halt;
import static instructions.IS1Vals.ret;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import ex.AsmConditionalException;
import ex.AsmDuplicateDeclarationException;
import ex.AsmEmptyTokenNameException;
import ex.AsmUndefinedMethodException;
import instructions.IS1enum;



public class Asm {

    public static final Set<String> conditionals = new HashSet<>(Arrays.asList(IF, ELSE, ENDIF, WHILE, WEND));
    public static final Set<String> conditionalsToPush = new HashSet<>(Arrays.asList(IF, ELSE, WHILE));
    public static final Set<String> conditionalsToEnd = new HashSet<>(Arrays.asList(ENDIF, WEND));

    /**
     * Simply takes in 1 long string and returns list of individual Words by
     * breaking on whitespace
     */
    public static List<String> parseSymbols(final String string) {
        return Arrays.asList(string.isEmpty() //
                        ? new String[0] //
                        : string.replaceAll("\\s+", " ").split("\\s+"));
    }

    /**
     * Divides the string into lines based on newline characters
     * @param string
     * @return
     */
    public static List<String> parseLines(final String string) {
        return Arrays.asList(string.split("\n"));
    }

    public static List<List<String>> parseLineSymbols(final String prog) {
        return parseLines(prog)
                .stream() //
                .map(s -> parseSymbols(s.trim())) //
                .collect(Collectors.toList());
    }

    /**
     * Takes a list of string/Words and converts List of Tokens TODO Remove this
     * method, its replaced by above lined version
     */
    public static List<IToken> tokenizeLines(final List<List<String>> tokStrings) {
        final List<IToken> tokens = createLineTokens(tokStrings);
        completeTokens(tokens);
        return tokens;
    }

    /**
     * Validate tokens, and update global info in them
     * @param tokens
     */
    private static void completeTokens(final List<IToken> tokens) {
        // Update tokens with start location
        updateTokenStarts(tokens);

        // TODO: Check for cmd taking address of next item grab next or wrap in []
        // wrap seems better

        // Just check if the same lable ID is used more then once
        checkForDuplicateDeclarations(tokens);

        // Tell jumping tokens where they are jumping to
        handleJumpJump(tokens);
    }

    /**
     * Initial creation of tokens, while ensuring the tok num, line num and line are
     * in place
     * @param tokStrings
     * @return
     */
    static List<IToken> createLineTokens(final List<List<String>> tokStrings) {
        int count = 0;
        final List<IToken> tokens = new ArrayList<>();
        for (int lineNum = 0; lineNum < tokStrings.size(); lineNum++) {
            final List<String> line = tokStrings.get(lineNum);
            for (int tokNum = 0; tokNum < line.size(); tokNum++) {
                tokens.add(tokenFactory(line.get(tokNum), count++, lineNum, tokNum));
            }
        }
        return tokens;
    }

    //
    //
    // private static List<IToken> createTokens(final List<String> tokStrings) {
    // final int[] index = { 0 }; // Kludge. Value hidden in array. Stream requires
    // final value.
    // return tokStrings
    // .stream()//
    // .map(s -> tokenFactory(s, index[0]++))//
    // .collect(Collectors.toList());
    // }

    /**
     * Find duplicate methods declarations
     * @param tokens
     */
    private static void checkForDuplicateDeclarations(final List<IToken> tokens) {
        // Difficult to check for duplicates in the stream... Possible with GroupBy.
        final Set<String> methodNames = new HashSet<>();
        for (final IToken token : tokens) {
            if (token.getType() == TokeType.definition) {
                if (methodNames.contains(token.getName())) {
                    throw new AsmDuplicateDeclarationException(token);
                }
                methodNames.add(token.getName());
            }
        }
    }

    /**
     * Update method and conditional tokens to jump to the correct location
     * @param tokens
     */
    private static void handleJumpJump(final List<IToken> tokens) {
        final Map<String, Integer> locationTable = buildDefinitionLocationTable(tokens);
        setJumpDestinations(tokens, locationTable);
    }


    /**
     * Build map giving the location in code where each method starts.
     * @param tokens The list of tokens.
     * @return map from method name to location it starts in compiled code
     */
    private static Map<String, Integer> buildDefinitionLocationTable(final List<IToken> tokens) {
        return tokens
                .stream()
                .filter(t -> t.getType() == definition) // blah blah
                .collect(Collectors.toMap(t -> t.getName(), t -> t.getCodeIndex())); // blah
    }

    /**
     * For the tokens which jump tell them where they jump to. NOTE: Each
     * if/else/endif has to be mapped to it's paired one. This is done by pushing on
     * a stack.
     *
     * @param tokens List of all tokens
     * @param definitionLocations Cache of named locations
     */
    private static void setJumpDestinations(final List<IToken> tokens, final Map<String, Integer> definitionLocations) {

        final Stack<IToken> conditionalStack = new Stack<>();
        // For each jumping tokens telling where they go
        tokens.forEach(t -> setJumpDestination(t, definitionLocations, conditionalStack));

        requireConditionalsComplete(conditionalStack);
    }

    /**
     * For tokens which jump, specify where they jump to
     * @param tok The token to update
     * @param declarationLocations Table of where methods start
     * @param conditionalStack Stack to keep track of conditionals matches
     */
    private static void setJumpDestination(final IToken tok, final Map<String, Integer> declarationLocations,
            final Stack<IToken> conditionalStack) {
        // Reference tokens updated with location of definition
        if (reference == tok.getType()) setReferenceJump(tok, declarationLocations);
        // Conditional tokens if and else updated with location in: if/else/endif
        else if (conditional == tok.getType()) setConditionalJump(tok, conditionalStack);
    }


    /**
     * If all the conditionals aren't removed from the stack, the an if or else
     * wasn't ended.
     * @param tokStack
     */
    private static void requireConditionalsComplete(final Stack<IToken> tokStack) {
        if (!tokStack.empty()) {
            throw new AsmConditionalException(tokStack.stream().map(t -> msgForCond(t)).collect(Collectors.joining()));
        }
    }

    /**
     * @return message for this conditional token left on stack.
     */
    private static String msgForCond(final IToken tok) {
        final int index = tok.getTokenIndex();
        return (WHILE.equals(tok.getName()))
                ? "Could not find WEND after WHILE at token index=" + index + ". "
                : "Could not find ENDIF after IF or ELSE at token index=" + index + ". ";
    }

    /**
     * For token which jumps to method reference, tell location in code
     * @param tok
     * @param declarationLocations
     */
    private static void setReferenceJump(final IToken tok, final Map<String, Integer> declarationLocations) {
        tok.setJumpLoc(findStart(tok.getName(), declarationLocations));
    }


    /**
     * Converts a single Word into a single Token, or throws exception on error.
     *
     * Note unknown words are assumed to be a reference to a method. If it isn't a
     * method that will be caught in makeCode()
     *
     * @param word The word for which this token is being made
     * @param index How far into the source file this is.
     * @return a Token representing the passed in word
     */

    static Token tokenFactory(final String word, final int index) {
        return tokenFactory(word, index, 0, 0);
    }

    static Token tokenFactory(final String word, final int index, final int line, final int tokNum) {
//        System.out.println(name);
        if (conditionals.contains(word)) {
            // Make token for conditional
            return new Token(conditional, word, index).setLoc(line, tokNum);
        }
        else if (word.startsWith(":")) {
            // This is a function definition such as :multiply, save the name w/o the colon
            return new Token(definition, word.substring(1), index).setLoc(line, tokNum);
        }
        else if (";".equals(word)) {
            // return at end of subroutine
            return new Token(IS1enum.lookup(ret), index).setLoc(line, tokNum);
        }
        else if (IS1enum.lookup(word) != null) {
            // Maps to an instruction from instruction set
            return new Token(IS1enum.lookup(word), index).setLoc(line, tokNum);
        }
        else if (word.matches("-?\\d+")) {
            // This is simply a number
            return new Token(Integer.parseInt(word), index).setLoc(line, tokNum);
        }
        else if (word.matches("\\S+")) {
            // Matches 1 or more non-whitespace chars. Must be a reference to something
            return new Token(reference, word, index).setLoc(line, tokNum);
        } else {
            // throw new RuntimeException("Empty named token :( :(" + word + ")");
            throw new AsmEmptyTokenNameException(word);
        }
    }


    /**
     * Converts a list of Tokens into a list of OpCodes for VM execution
     *
     * @param tokens Tokens which should be converted to op codes,
     * @return List of opcodes from IS1.
     */
    public static List<Integer> makeCode(final List<IToken> tokens) {
        // tokens.forEach(t -> System.out.println(t.toString()));
        // Compile machine code for tokens end to end
        final List<Integer> collected = tokens.stream() //
                .map(t -> t.getValues()) //
                .flatMap(Collection::stream) //
                .collect(Collectors.toList());

        // Ensure the last symbol is HALT to avoid runaway VM.
        return ensureCodeHalts(collected);
    }


    /**
     * Update the code index where each token starts
     * @param tokens The tokens which will become the program
     */
    private static void updateTokenStarts(final List<IToken> tokens) {
        int index = 0;
        for (final IToken tok : tokens) {
            tok.setCodeIndex(index);
            index += tok.size();
        }
    }

    /**
     * Append a halt symbol to the code if it isn't already there
     */
    private static List<Integer> ensureCodeHalts(final List<Integer> toRet) {
        if(toRet.isEmpty() || toRet.get(toRet.size()-1)!=halt) {
            toRet.add(halt);
        }
        return toRet;
    }

    /**
     * For conditionals if/else/then, push on stack or pop set off as appropriate.
     * Pop for then (the last symbol) When then is found pop back to IF. These all
     * go together.
     * @param tok
     * @param conditionalStack
     */
    private static void setConditionalJump(final IToken tok, final Stack<IToken> conditionalStack) {
        // Accumulate conditional words on stack until clause complete then process from
        if (conditionalsToPush.contains(tok.getName())) {
            conditionalStack.push(tok);
        } else if (conditionalsToEnd.contains(tok.getName())) {
            completeConditionalClause(conditionalStack, tok);
        } else {
            throw new AsmConditionalException("Unexpected conditional token at(" + tok.getTokenIndex() + "): " + tok);
        }
    }

    /**
     * Found an ENDIF. Pop back to If. This is a set. Give IF and Else correct jump
     * locations
     * @param condStk
     * @param endTok
     */
    private static void completeConditionalClause(final Stack<IToken> condStk, final IToken endTok) {
        // Got an ENDIF, so end the conditional clause, having each jump to next
        // NOTE: ELIF doesn't make sense in an RPN language. WHY
        try {
            final IToken aTok = condStk.pop();
            if (endTok.getName().equals(ENDIF)) {
                if (aTok.getName().equals(IF)) {
                    // Tie IF and ELSE together
                    chkName(aTok, IF, endTok).setJumpLoc(endTok.getCodeIndex());
                } else {
                    // Tie together IF, ELSE & ENDIF
                    final IToken elseTok = chkName(aTok, ELSE, endTok);
                    elseTok.setJumpLoc(endTok.getCodeIndex());
                    chkName(condStk.pop(), IF, endTok).setJumpLoc(elseTok.getCodeEndIndex());
                }
            } else if (endTok.getName().equals(WEND)) {
                // Tie WHILE and WEND together
                final IToken whileTok = chkName(aTok, WHILE, endTok);
                whileTok.setJumpLoc(endTok.getCodeEndIndex());
                endTok.setJumpLoc(whileTok.getCodeIndex());
            } else {
                throw new AsmConditionalException(
                        "CompleteCondClause called without ENDIF or WEND. It was: " + endTok.getName());
            }
        } catch (final EmptyStackException e) {
            final int index = endTok.getTokenIndex();
            throw new AsmConditionalException((WEND.equals(endTok.getName()))
                    ? "Could not find an WHILE before the WEND at index=" + index
                    : "Could not find an IF before the ENDIF at index=" + index);
        }
    }

    /**
     * @param tok Token to evaluate
     * @param required Required token name
     * @param endTok Token which triggered looking for the start. Endif triggers IF
     * @return Passed in tok if it matches required type
     */
    private static IToken chkName(final IToken tok, final String required, final IToken endTok) {
        if (!required.equals(tok.getName())) {
            throw new AsmConditionalException(
                    "Failed to find " + required.toUpperCase() + " before the " + endTok.getName().toUpperCase()
                            + " at index " + endTok.getTokenIndex() + ". Instead found " + tok.getName().toUpperCase()
                            + " at index=" + tok.getTokenIndex());
        }
        return tok;
    }

    /**
     * Find location where method is defined
     * @param methodIndexCache Cache of where each method starts in code
     * @param string
     * @return
     */
    private static Integer findStart(final String name, final Map<String, Integer> methodIndexCache) {
        if (!methodIndexCache.containsKey(name))
            throw new AsmUndefinedMethodException(name);
        return methodIndexCache.get(name);
    }

    /**
     * Simply convert List<Integer> to int[].
     * @param list
     * @return
     */
    public static int[] toIntArray(final List<Integer> list) {
        return list.stream()
                .mapToInt(i->i)
                .toArray();
    }

    /**
     * Do pre-processor textual substitutions
     * @param text
     * @return The modified string with all the substitutions done
     */
    public static String preProcess(final String text) {
        return text
                .replaceAll("\\([^)]*\\)", " ") // Remove everything between parens as comments
                .replaceAll(" \\+ ", " add ") // Accept + as Add symbol
                .replaceAll(" - ", " sub ") // Accept - as subtract symbol
                .replaceAll("<", " lt ") // Acceot < as less then symbol
                .replaceAll(">", " gt ") // Accept > as greater then symbol
                .replaceAll("=", " eq ") // Accept = as equal to symbol
                // .replaceAll("(/\\*.*\\*/)*?","") // Trying to remove comments like /*....*/
                // .replaceAll("(/\\*[^*/]\\*/)","")

                .replaceAll("[\\t|' ']+", " ") // Replace whitespace group with single space
        // .replaceAll("\\s+", " "); // Replace whitespace group with single space
                .replaceAll(";", " ; "); // Ensure ';' has space on both sides separating. End of Method
    }

    /**
     * Do full conversion from text to executable code
     * @param text
     * @return
     */
    public static int[] textToCode(final String text) {
        return toIntArray(makeCode(tokenizeLines(parseLineSymbols(preProcess(text)))));
    }
}
