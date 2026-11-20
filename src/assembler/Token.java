package assembler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ex.TokenException;
import instructions.IS1Vals;
import instructions.IS1enum;

public class Token implements IToken {

    public static final String ENDIF = "endif";
    public static final String ELSE = "else";
    public static final String IF = "if";
    public static final String WHILE = "while";
    public static final String WEND = "wend";
    public static final Integer JMP_OFSET = 1;

    private List<Integer> vals = new ArrayList<>();
    private final String name;
    private final TokeType type;
    private int codeIndex;
    private final int tokenIndex;
    private int line;
    private int indexOnLine;

    public Token setLoc(final int line, final int tokNum) {
        this.line = line;
        indexOnLine = tokNum;
        return this;
    }

    @Override
    public int getTokenIndex() {
        return tokenIndex;
    }

    /**
     * Token constructor for use with defined instructions
     * @param inst
     * @param index in the source file
     */
    public Token(final IS1enum inst, final int index) {
        vals.add(inst.getValue());
        name = inst.toString();
        type = TokeType.cmd;
        tokenIndex = index;
    }

    /**
     * Token constructor to represent a number
     * @param numVal numeric value
     * @param index index in source files
     */
    public Token(final int numVal, final int index) {
        vals.add(IS1Vals.push);
        vals.add(numVal);
        name = "number";
        type = TokeType.number;
        tokenIndex = index;
    }

    /**
     * Token constructor for strings (not num or inst). This is method or
     * conditionals.
     * @param type Type of token. Assume only reference or conditional here.
     * @param name String value of symbol. Either method name of conditional type.
     * @param index Index of this token in the list of tokens
     */
    public Token(final TokeType type, final String name, final int index) {
        this.name = name;
        this.type = type;
        this.tokenIndex = index;
        if(TokeType.reference==type) {
            vals.addAll(Arrays.asList(IS1Vals.push,-1,IS1Vals.gsb));
        }else if(TokeType.conditional==type) {
            if(IF.equals(name)) {
                vals.addAll(Arrays.asList(IS1Vals.push,-1,IS1Vals.jmpz));
            }else if(ELSE.equals(name)) {
                vals.addAll(Arrays.asList(IS1Vals.push,-1,IS1Vals.jmp));
            } else if (ENDIF.equals(name)) {
                vals.addAll(Arrays.asList());
            } else if (WHILE.equals(name)) {
                vals.addAll(Arrays.asList(IS1Vals.push, -1, IS1Vals.jmpz));
            } else if (WEND.equals(name)) {
                vals.addAll(Arrays.asList(IS1Vals.push, -1, IS1Vals.jmp));
            }else {
                throw new TokenException("Unsupported conditional name: " + name);
            }
        } else if (TokeType.definition == type) {
            // This is a method reference. Do nothing Pass through.
        } else {
            throw new TokenException("Unrecognized identifier string:" + name + " with type: " + type);
        }
    }

    // public Token(TokeType type, String name, List<Integer> initVals) {
    // this.name = name;
    // this.type = type;
    // vals.addAll(initVals);
    // }

    @Override
    public void setJumpLoc(final int loc) {
        // Currently all tokens which jump have the address in the same location.
        vals.set(JMP_OFSET, loc);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TokeType getType() {
        return type;
    }
    @Override
    public List<Integer> getValues() {
        return vals;
    }

    @Override
    public void setValues(final List<Integer> vals) {
        this.vals = vals;
    }

    @Override
    public int size() {
        return vals.size();
    }

    // private int codeIndex;
    // private final int tokenIndex;
    // private int line;
    // private int indexOnLine;
    @Override
    public String toString() {
        return "{" + name + "," + type + "," + vals.toString() + "(" + codeIndex + "," + tokenIndex + "," + line + ","
                + indexOnLine + ")}";
    }

    @Override
    public int getCodeIndex() {
        return codeIndex;
    }

    @Override
    public int getCodeEndIndex() {
        return getCodeIndex() + size();
    }

    @Override
    public void setCodeIndex(final int index) {
        codeIndex = index;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getIndexOnLine() {
        return indexOnLine;
    }

}
