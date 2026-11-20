package assembler;

import static instructions.IS1Vals.halt;
import static instructions.IS1Vals.jmp;
import static instructions.IS1Vals.jmpz;
import static instructions.IS1Vals.noop;
import static instructions.IS1Vals.push;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import ex.AsmConditionalException;
import ex.AsmDuplicateDeclarationException;
import ex.AsmUndefinedMethodException;
import instructions.IS1Vals;
import instructions.IS1enum;

public class AsmTest {

    @Test
    public void testCtor() {
        final Asm a = new Asm();
        assertNotNull(a);
    }

    @Test
    public void testParseSymbols() {
        final List<String> empty = Asm.parseSymbols("");
        assertEquals(0, empty.size());

        final List<String> symbols = Asm.parseSymbols("Hi Barkley how are you");
        assertEquals(5, symbols.size());
//        System.out.println(symbols);
    }

    @Test
    public void testParseLineSymbols() {
        final List<List<String>> empty = Asm.parseLineSymbols("");
        assertEquals(1, empty.size());
        assertEquals(0, empty.get(0).size());

        final List<List<String>> blankLines = Asm.parseLineSymbols("\n hi \n \n bye bye \n");
        // System.out.println(blankLines);
        assertEquals(4, blankLines.size());
        assertEquals(0, blankLines.get(0).size());
        assertEquals(1, blankLines.get(1).size());
        assertEquals(0, blankLines.get(2).size());
        assertEquals(2, blankLines.get(3).size());

        final List<List<String>> lines = Asm.parseLineSymbols("1 2 \n 3 4 5 \n hi \n");
        // System.out.println(lines);
        assertEquals(3, lines.size());
        assertEquals(2, lines.get(0).size());
        assertEquals(3, lines.get(1).size());
        assertEquals(1, lines.get(2).size());
    }

    @Test
    public void testTokenizeEmptyList() {
        final List<List<String>> toks = new ArrayList<>();
        assertEquals(0, toks.size());
        final List<IToken> blah = Asm.tokenizeLines(toks);
        assertEquals(0, blah.size());
    }

    @Test
    public void testTokenizeCmd() {
        final String[] strings = new String[] {"dup","swp","add"};
        final List<IToken> tokens = Asm.tokenizeLines(Arrays.asList(Arrays.asList(strings)));
        assertNotNull(tokens);
        assertEquals(strings.length,tokens.size());
        for(int i=0; i<strings.length; i++) {
            assertEquals(strings[i],tokens.get(i).getName());
        }
    }

    @Test
    public void testTokenizeNum() {
        final String[] strings = new String[] {"1","10","100","1000"};
        final Integer[] expected = new Integer[] {1,10,100,1000 };
        final List<IToken> tokens = Asm.tokenizeLines(Arrays.asList(Arrays.asList(strings)));
        assertNotNull(tokens);
        assertEquals(strings.length,tokens.size());
        for(int i=0; i<strings.length; i++) {
            assertEquals("number",tokens.get(i).getName());
            assertEquals(2,tokens.get(i).getValues().size());
            assertEquals(IS1Vals.push,tokens.get(i).getValues().get(0).intValue());
            final Integer blah = tokens.get(i).getValues().get(1).intValue();
            assertTrue(Arrays.asList(expected).contains(blah));
//            assertThat(expected,hasItem(blah));
        }
    }

    @Test
    public void testMakeCode() {
        final String[] strings = new String[] {"dup","swp","add"};
        final List<IToken> tokens = Asm.tokenizeLines(Arrays.asList(Arrays.asList(strings)));
        assertNotNull(tokens);
        assertEquals(strings.length,tokens.size());
        for(int i=0; i<strings.length; i++) {
            assertEquals(strings[i],tokens.get(i).getName());
        }

        final List<Integer> nums = Asm.makeCode(tokens);
        assertEquals(IS1Vals.dup,nums.get(0).intValue());
        assertEquals(IS1Vals.swp,nums.get(1).intValue());
        assertEquals(IS1Vals.add,nums.get(2).intValue());
    }

    @Test
    public void testMakeCodeIfErrors() {

        checkConditionalFailure(
                Arrays.asList("noop", "noop", "else", "noop"),
                "ENDIF after IF or ELSE at token index=2");
        checkConditionalFailure(
                Arrays.asList("noop", "noop", "if", "else", "noop"),
                "ENDIF after IF or ELSE.*index=3");
        checkConditionalFailure(Arrays.asList("noop", "noop", "if", "if", "noop"), "ENDIF after IF or ELSE.*index=3");

        checkConditionalFailure(Arrays.asList("noop", "noop", "else", "endif"), "IF before the ENDIF at index=3");
        checkConditionalFailure(Arrays.asList("noop", "noop", "endif"), "IF before the ENDIF at index=2");
        checkConditionalFailure(Arrays.asList("noop", "noop", "endif", "endif"), "IF before the ENDIF at index=2");

        checkConditionalFailure(Arrays.asList("noop", "noop", "if", "else", "else", "else", "endif"),
                "IF before the ENDIF.*Instead found ELSE");
    }

    @Test
    public void testMakeWhile() {

        checkConditionalFailure(Arrays.asList("noop", "noop", "while", "noop"), "WEND after WHILE at token index=2");
        checkConditionalFailure(Arrays.asList("noop", "noop", "wend", "noop"), "WHILE before the WEND at index=2");

        checkConditionalFailure(
                Arrays.asList("noop", "while", "if", "wend", "noop"),
                "WHILE before the WEND .* Instead found IF");
        checkConditionalFailure(
                Arrays.asList("noop", "while", "else", "wend", "noop"),
                "WHILE before the WEND .* Instead found ELSE");
        checkConditionalFailure(
                Arrays.asList("noop", "while", "endif", "wend", "noop"),
                "ELSE before the ENDIF.*Instead found WHILE");

        checkConditionalFailure(
                Arrays.asList("noop", "if", "while", "endif", "noop"),
                "ELSE before the ENDIF.*Instead found WHILE");
        checkConditionalFailure(
                Arrays.asList("noop", "if", "wend", "endif", "noop"),
                "WHILE before the WEND.*Instead found IF");
        checkConditionalFailure(
                Arrays.asList("noop", "if", "else", "while", "endif", "noop"),
                "ELSE before the ENDIF.*Instead found WHILE");
        checkConditionalFailure(
                Arrays.asList("noop", "if", "else", "wend", "endif", "noop"),
                "WHILE before the WEND.*Instead found ELSE");

    }

    private void checkConditionalFailure(final List<String> words, final String expected) {
        try {

            final List<IToken> tokens = Asm.tokenizeLines(Arrays.asList(words));
            assertEquals(words.size(), tokens.size());
            fail("The conditionals (" + words + ") should have failed assembly.");
        } catch (final IndexOutOfBoundsException e) {
            throw e; // WHY IS THIS HERE?? Just to allow try to remain?
        }
        catch (final AsmConditionalException e) {
            final String message = e.getMessage();
            // System.out.println(message);
            assertTrue(
                    "Couldn't find match for (" + expected + ") in: " + message,
                    message.matches(".*" + expected + ".*"));
        }
    }

    @Test
    public void testToIntArray() {
        final int[] origArr = {1,2,3,4,5,6,7,8,9};
        final List<Integer> intList  = Arrays.stream(origArr).boxed().collect(Collectors.toList());

        final int[] result = Asm.toIntArray(intList);
        assertEquals(origArr.length,result.length);
        for(int i = 0;i< origArr.length; i++) {
            assertEquals(origArr[i],result[i]);
        }
    }

    @Test
    public void testTextToCode() {
        final String source = "dup swp add gsb   ret halt 999";
        final int[] out = Asm.textToCode(source);
        assertEquals("pos 0",IS1Vals.dup,out[0]);
        assertEquals("pos 1",IS1Vals.swp,out[1]);
        assertEquals("pos 2",IS1Vals.add,out[2]);
        assertEquals("pos 3",IS1Vals.gsb,out[3]);
        assertEquals("pos 4",IS1Vals.ret,out[4]);
        assertEquals("pos 5",IS1Vals.halt,out[5]);
        assertEquals("pos 6",IS1Vals.push,out[6]);
        assertEquals("pos 7",999,out[7]);
        assertEquals("pos 8",IS1Vals.halt,out[8]);
        assertEquals(9,out.length);
    }

    @Test
    public void testDefineToken() {
        // :BOB 5;  --> BOB constant value is address of (push 5 return)
        final String source = ":BOB";
        final int index = 9;
        final Token t = Asm.tokenFactory(source, index);
        assertEquals(TokeType.definition,t.getType());
        assertEquals("BOB",t.getName());
        assertEquals(index, t.getTokenIndex());
    }

    @Test
    public void testSemToken() {
        // :BOB 5;  --> BOB constant value is address of (push 5 return)
        final String source = ";";
        final int index = 5;
        final Token t = Asm.tokenFactory(source, index);
        assertEquals(TokeType.cmd,t.getType());
        assertEquals("ret",t.getName());
        assertEquals(index, t.getTokenIndex());
    }

    @Test
    public void testDefineMethod() {
        // :BOB 5;  --> BOB constant value is address of (push 5 return)
        final String source = ":BOB 5; BOB halt";
        final List<IToken> toks = Asm.tokenizeLines(Asm.parseLineSymbols(Asm.preProcess(source)));

        assertEquals( 5,toks.size());
//        assertThat(toks.get(0),equalTo(""));
        assertEquals(TokeType.definition,toks.get(0).getType());
        assertEquals(TokeType.number,toks.get(1).getType());
        assertEquals(TokeType.cmd,toks.get(2).getType());
        assertEquals(TokeType.reference,toks.get(3).getType());
        assertEquals(TokeType.cmd,toks.get(4).getType());
    }


    @Test
    public void testDefineMethod2() {
        // :BOB 5;  --> BOB constant value is address of (push 5 return)
        final String source = "noop :BOB 5; BOB halt";
        final List<IToken> toks = Asm.tokenizeLines(Asm.parseLineSymbols(Asm.preProcess(source)));

        assertEquals( 6,toks.size());
//        assertThat(toks.get(0),equalTo(""));
        assertEquals(TokeType.cmd,toks.get(0).getType());
        assertEquals(TokeType.definition,toks.get(1).getType());
        assertEquals(TokeType.number,toks.get(2).getType());
        assertEquals(TokeType.cmd,toks.get(3).getType());
        assertEquals(TokeType.reference,toks.get(4).getType());
        assertEquals(TokeType.cmd,toks.get(5).getType());

        final List<Integer> code = Asm.makeCode(toks);
        final List<Integer> expected = Arrays.asList(
                IS1Vals.noop,IS1Vals.push,5,IS1Vals.ret,
                IS1Vals.push, 1, IS1Vals.gsb,
                IS1Vals.halt);
        assertEquals(expected.size(),code.size());
        assertEquals(expected,code);
    }

    @Test
    public void testUnDefinedMethod() {
        // :BOB 5;  --> BOB constant value is address of (push 5 return)
        final String source = ":JIM 5; BOB halt";

        // assertEquals( 5,toks.size());
        //// assertThat(toks.get(0),equalTo(""));
        // assertEquals(TokeType.definition,toks.get(0).getType());
        // assertEquals(TokeType.number,toks.get(1).getType());
        // assertEquals(TokeType.cmd,toks.get(2).getType());
        // assertEquals(TokeType.reference,toks.get(3).getType());
        // assertEquals(TokeType.cmd,toks.get(4).getType());

        try {

            final List<IToken> toks = Asm.tokenizeLines(Asm.parseLineSymbols(Asm.preProcess(source)));
            Asm.makeCode(toks);
        }catch(final AsmUndefinedMethodException e) {
            assertTrue(e.getMessage().contains("BOB") && e.getMessage().contains("not been defined"));
        }
    }

    @Test
    public void testIfElse() {
        final String source = "noop 0 if 222 else 444 endif";
        final List<IToken> toks = Asm.tokenizeLines(Asm.parseLineSymbols(Asm.preProcess(source)));
        final List<Integer> code = Asm.makeCode(toks);

//        System.out.println( codeToString(code));

        assertEquals(TokeType.cmd,toks.get(0).getType());
        assertEquals(TokeType.number,toks.get(1).getType());
        assertEquals(TokeType.conditional,toks.get(2).getType());
        assertEquals(TokeType.number,toks.get(3).getType());
        assertEquals(TokeType.conditional,toks.get(4).getType());
        assertEquals(TokeType.number,toks.get(5).getType());
        assertEquals(TokeType.conditional,toks.get(6).getType());
        assertEquals(7,toks.size());

//        java.lang.AssertionError: expected:<[]> but was:<[0, 17, 0, 17, -1, 5, 17, 222, 17, -1, 5, 17, 444]>

        final List<Integer> expectedCode = Arrays.asList(noop, push, 0, push, 11, jmpz, push, 222, push, 13, jmp, push, 444,halt);
        assertEquals(expectedCode,code);
    }

    @Test
    public void testIfElse2() {
        final String source =
                "0 if 111 if 222 endif 333 else 444 if 555 else 666  endif endif";
        final List<IToken> toks = Asm.tokenizeLines(Asm.parseLineSymbols(Asm.preProcess(source)));
        final List<Integer> code = Asm.makeCode(toks);

//        System.out.println( codeToString(code));

        final List<Integer> expectedCode = Arrays
                .asList(push, 0, push, 17, jmpz, push, 111, push, 12, jmpz, push, 222, push, 333, push, 29, jmp, push, 444, push, 27, jmpz, push, 555, push, 29, jmp, push, 666, halt);

        evalEquality(expectedCode,code);
        assertEquals(expectedCode,code);
        //        assertThat(expectedCode,equalTo());
    }


    private void evalEquality(final List<Integer> expectedCode, final List<Integer> code) {
        final int l1= expectedCode.size();
        final int l2= code.size();
        final int sz = Math.min(l1, l2);
        for(int i=0; i<sz; i++) {
            assertEquals("At index="+i,expectedCode.get(i),code.get(i));
        }
    }

    // private String codeToString(List<Integer> code) {
    // final StringBuffer buf = new StringBuffer();
    // for(final Integer i: code) {
    // buf.append(printInt(i)+",");
    // }
    // final String result = buf.toString();
    // return result;
    // }

    static String printInt(final int val) {
        for(final IS1enum eVal: IS1enum.values()) {
            if(eVal.getValue()==val) {
                return eVal.getName().toString() + "{" + val + "}";
            }
        }
        return "Unk"+"("+val+"}";
    }

    @Test
    public void testPreProcess() {
        // :BOB 5;  --> BOB constant value is address of (push 5 return)
        assertEquals("hi ; hi",Asm.preProcess("hi;hi"));
        // assertEquals("push 5 dup gt", Asm.preProcess("push 5 ( walk dog ) dup gt"));
        assertEquals("push 5 dup gt ret rot",Asm.preProcess("push 5 ( walk dog ) dup (feed cat) gt ret () rot"));
    }

    @Test
    public void testDuplicateMethods() {
        try {
        Asm.tokenizeLines(Arrays.asList(Arrays.asList(":hi", "hi", ":hi")));
            fail("Should have thrown duplicate exception, but threw nothing");
        } catch (final /* NullPointerException */ AsmDuplicateDeclarationException a) {
            // Correctly throwing exception
        } catch (final Exception e) {
            fail("Should have thrown duplicate exception, but instead threw: " + e.getMessage());
        }
    }

//    @Test
    public void testBlah() {
        assertTrue("//".matches("//"));
        assertTrue("*".matches("\\*"));
        assertTrue("/**/".matches("/\\*\\*/"));
        assertTrue("/*bud*/".matches("/\\*.*\\*/"));
        assertTrue("/* bud dog */".matches("/\\*.*\\*/"));
//        assertTrue("/*walkdog*/".matches("/\\*.*\\*//"));
        assertEquals("success","/* bud dog */".replaceAll("/\\*.*\\*/","success"));
        final String a = "dup /* bud dog */ 5 /*cats */ swp".replaceAll("/\\*.*\\*/","noop");
        fail(a);
    }
}
