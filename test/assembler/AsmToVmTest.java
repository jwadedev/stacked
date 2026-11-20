package assembler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import instructions.IS1Vals;
import instructions.IS1enum;
import interfaces.IMem;
import interfaces.IStk1;
import interfaces.IStk2;
import interfaces.Ivm;
import interfaces.VmDebugable;
import memory.Mem;
import stack.MicroStk;
import vm.MicroVm2;

public class AsmToVmTest {

    public static int MAX_MEM = 200;
    public static int MAX_STK = 40;

    MicroVm2 vmImpl;
    Ivm vm;
    IMem newMem;
    IStk2 stkp;
    IStk2 rstk;

    @Before
    public void setup() {
        newMem = new Mem(new int[MAX_MEM]);
        stkp = new MicroStk("execStk", MAX_STK);
        rstk = new MicroStk("rtnStk", MAX_STK);
        vm = vmImpl = new MicroVm2(newMem, stkp, rstk);
    }

    @Test
    public void testMath() {
        final int[] code = Asm.textToCode("6 7 + 3 -"+" prnt halt");
        vm.addProg(code);
        vm.run();
        assertEquals(10,vm.getStk().top());
    }

    @Test
    public void testCompare() {
        final int[] code = Asm.textToCode("1 2 <    4 3 <   5 6 >    8 7 >    9 9 =    10 11 ="+" prnt halt");
        // Check lt and gt, remember lifo.  F,T,T, F, F, T
        vm.addProg(code);
        vm.run();
//        stk.print();
        final IStk1 stk = vm.getStk();
        assertEquals(0,stk.pop());
        assertEquals(1,stk.pop());
        assertEquals(1,stk.pop());
        assertEquals(0,stk.pop());
        assertEquals(0,stk.pop());
        assertEquals(1,stk.pop());
    }

    @Test
    public void testBoolOps() {
//        final int[] code = Asm.textToCode("1 not (failing, needs bit wise and logical bool ops) "+" prnt halt");

        final IStk1 stk = vm.getStk();

        vm.addProg(Asm.textToCode("1 not "));
        vm.run();
        assertEquals(0, stk.pop());


        vmImpl.setPc(0);
        vm.addProg(Asm.textToCode("0 not "));
        vm.run();
        assertEquals(1, stk.pop());

        vmImpl.setPc(0);
        vm.addProg(Asm.textToCode("1 1 and "));
        vm.run();
        assertEquals(1, stk.pop());

        vmImpl.setPc(0);
        vm.addProg(Asm.textToCode("1 0 and "));
        vm.run();
        assertEquals(0, stk.pop());

        vmImpl.setPc(0);
        vm.addProg(Asm.textToCode("0 0 and "));
        vm.run();
        assertEquals(0, stk.pop());


        vmImpl.setPc(0);
        vm.addProg(Asm.textToCode("1 1 or "));
        vm.run();
        assertEquals(1, stk.pop());

        vmImpl.setPc(0);
        vm.addProg(Asm.textToCode("1 0 or "));
        vm.run();
        assertEquals(1, stk.pop());

        vmImpl.setPc(0);
        vm.addProg(Asm.textToCode("0 0 or "));
        vm.run();
        assertEquals(0, stk.pop());
    }

    @Test
    public void testBNot() {
        vm.addProg(Asm.textToCode("1 comp "));
      vm.run();
      assertEquals(-2, vm.getStk().pop());

      vmImpl.setPc(0);
        vm.addProg(Asm.textToCode("-2 comp "));
      vm.run();
      assertEquals(1, vm.getStk().pop());
    }



    @Test
    public void testCondition() {
        final String prog = "0 if 77 else 99 endif prnt halt";
        final int[] code = Asm.textToCode(prog);
//        System.out.println(codeToString((code)));;
        vm.addProg(code);
        vm.run();
        assertEquals(99,vm.getStk().top());
    }

    @Test
    public void testEmpty() {
        final IStk1 stk = vm.getStk();
        final String prog = " ";
        final int[] code = Asm.textToCode(prog);
//        System.out.println(codeToString((code)));;
        vm.addProg(code);
        vm.run();
        assertEquals(0,stk.size());
    }

    @Test
    public void testFib() {

        final String prog = "20 fib1 prnt halt      (push 4 then execute method, print an halt)"
                + ":fib1 dup                  (define jump label fib1)"
                + "dup                        (dup for later use)"
                + "0 = swp 1 = or             (boolean does stack top equal 0 or 1)"
                + "if                         (if not true skip to else)"
                + "    drp 1 ret                  (happy case get rid of extra and return number 1)"
                + "else                       (begin secondary case)"
                + "    dup                        (make another copy of number so there is one for each)"
                + "    1 sub fib1                 (call method with n-1)"
                + "    swp                        (bring orig number in front of answer from method call)"
                + "    2 sub fib1                 (call method with n-2)"
                + "    add                        (add results together)"
                + "    ret                        (return from method)"
                + "endif                      (end of else block)"
                ;
        final int[] code = Asm.textToCode(prog);
        System.out.println(codeToString((code)));;
        vm.addProg(code);
        vm.run();
        assertEquals(10946,vm.getStk().top());
    }

    @Test
    public void testDivide() {
        // assertEquals();
    }

    public int[] javaDiv(final int[] params) {
        int[] returned;
        if (params[0] >= params[1]) {
            returned = javaDiv(new int[] { params[0], params[1] << 1 });
        } else {
            returned = new int[] { params[0], params[1], 0 };
        }
        if (returned[0] >= returned[1]) {
            return new int[] { returned[0] - returned[1], returned[1] >> 1, (returned[2] << 1) + 1 };
        } else {
            return new int[] { returned[0], returned[1] >> 1, returned[2] << 1 };
        }

    }
    @Test
    public void testMultiply() {
        assertEquals(0,javaMult(0,1));
        assertEquals(0,javaMult(1,0));
        assertEquals(1,javaMult(1,1));
        assertEquals(2,javaMult(1,2));
        assertEquals(2,javaMult(2,1));
        assertEquals(3,javaMult(1,3));
        assertEquals(3,javaMult(3,1));
        assertEquals(4,javaMult(2,2));
        assertEquals(81,javaMult(9,9));
//        String prog = "20 fib1 prnt halt       (push 4 then execute method, print an halt)"
//                + ":fib1 ";
//        final int[] code = Asm.textToCode(prog);
//        System.out.println(codeToString((code)));;
//        vm.addProg(code);
//        vm.run();
//        assertEquals(10946,stk.top());
    }


    public int javaMult1(final int a, final int b) {
        return (a == 0) ? 0:(javaMult1(a>>1,b) << 1)+(((a&1)==1)?b:0);
    }

    public int javaMult(final int a, final int b) {
        if(b!=0) {
            final int val = javaMult(a,b>>1) << 1;
            if((b&1)==1) {
                return val+a;
            }else {
                return val;
            }
        }
        return 0;
    }



    @Test
    public void testWhileLoop() {
        final String prog = "0 loop halt \n " // blah
                + ":loop 1 while prnt _save _checkRepeat wend ; \n " // hi
                + ":_checkRepeat 1 + dup 10 < ; \n " //
                + ":_save dup dup 50 + sto ; (this saves to memory at index 50+i) \n "; // hi there

        final int[] code = Asm.textToCode(prog);
        // System.out.println(this.codeToString(code));
        vm.addProg(code);
        vm.run();

        // Validate that the data was written where expected.
        for(int i = 0;i < 10;i++) {
            assertEquals(i, vm.getMem().getMem(50 + i));
        }
    }

    @Test
    public void testPosSwap() {
        final String prog = "90 95 (now swap to increasing order) ifswap halt (load mem locations and run swap method)" // blah
                + ":ifswap _readMem over over gt if " // hi
                + "_leave "// there
                + "else " // blah
                + "_wrtSwpd " // blah
                + "endif ;" // blah
                + ":_leave drp drp drp drp ;" // blah
                + ":_wrtSwpd rot swp sto swp sto ;" // blah
                + ":_readMem over over ld swp ld swp ;"; // blah

        final int[] code = Asm.textToCode(prog);
        System.out.println(this.codeToString(code));

        vm.addProg(code);
        vm.getMem().setMem(90, 12);
        vm.getMem().setMem(95, 42);

        // Validate it swaps the order
        vm.run();
        assertEquals(12, vm.getMem().getMem(95));
        assertEquals(42, vm.getMem().getMem(90));

        // Validate it doesn't re-swap because correct order
        vm.setPc(0);
        vm.run();
        assertEquals(12, vm.getMem().getMem(95));
        assertEquals(42, vm.getMem().getMem(90));
    }

    private static String library = //
            ":ifswap _readMem _dup2 < if " // hi
            + "_leave "// there
            + "else " // blah
            + "_wrtSwpd " // blah
            + "endif ;" // blah
            + ":_leave drp drp drp drp ;" // blah
            + ":_wrtSwpd rot swp sto swp sto ;" // blah
            + ":_readMem _dup2 ld swp ld swp ; " // n
            + ":_dup2 over over ;"; // blah

    @Test
    public void testSort() {
        final String prog = "150 155 sort halt " // blah
                + ":sort 1 while dup2 makePass 1 - dup2 = not wend ;" // m
                + ":makePass 1 while makePair ifswap inc chkRpt wend drp drp;" // mn
                + ":chkRpt dup2 (prt2nd) lt ;" //
                + ":dup2 over over ;" //
                + ":inc swp 1 + swp ; " //
                + ":makePair over dup 1 + ;" //
                + library;

        final int[] code = Asm.textToCode(prog);
        // System.out.println(this.codeToString(code));
        vm.addProg(code);

        // Store the random numbers into the VMs memory
        final IMem mem = vm.getMem();
        final int[] rndVals = { 32, 45, 98, 7, 454, 6, 6, 7, 8, 4, 3, 978, 256, 435, 3, 3, 5, 45, 7, 3, 8, 0, 15, 1001,
                4242, 123, 456, 876 };
        for (int i = 0; i < rndVals.length; i++) {
            mem.setMem(150 + i, rndVals[i]);
        }
        // Modify program with array end marker
        mem.setMem(3, 150 + rndVals.length);

        // run the program
        vm.run();

        // System.out.println();
        for (int i = 150; i < 150 + rndVals.length + 1; i++) {
            // System.out.print(mem.getMem(i) + ",");
            if (i > 150) {
                assertTrue(mem.getMem(i - 1) <= mem.getMem(i));
            }
        }

        System.out.println(
                "Sorting array length=" + rndVals.length + " took this many cycles " + ((VmDebugable) vm).getCount());
    }

    @Test
    public void testMult() {

        final String prog = "0 0 MULT prnt halt                   (push 4 then execute method, print an halt)"
                + ":dupTwo over over ;         (a,b --> a,b,a,b)"
                + ":MULT dup if _nonZero else drp drp 0 endif ;    (if cpy of b is true exec nonZero, else drop params and put zero on)"
                + ":_nonZero dupTwo 1 rshft MULT 1 lshft _posAdd ;       (multiply[a,b>>1]<<1)"
                + ":_posAdd swp 1 and if add else swp drp endif ;  (if first bit set, add A to number)"
                ;
        final int[] code = Asm.textToCode(prog);
//      System.out.println(Arrays.toString(code));
//        System.out.println(codeToString((code)));;
        vm.addProg(code);
        vm.run();
//        System.out.println(((Vm1)vm).count);
        assertEquals(javaMult(0,0),vm.getStk().top());

        evalOneMult(0,0,prog);
        evalOneMult(1,1,prog);
        evalOneMult(1,10,prog);
        evalOneMult(10,1,prog);
        evalOneMult(10,10,prog);
        evalOneMult(10,100,prog);
        evalOneMult(100,10,prog);
        evalOneMult(100,100,prog);
        evalOneMult(100,1000,prog);
        evalOneMult(1000,100,prog);
        evalOneMult(1000,1000,prog);

    }
    public void evalOneMult(final int a, final int b, final String prog) {
        vm.reset();
        final int[] ml = Asm.textToCode(prog);

        ml[1] = a;
        ml[3] = b;
        vm.addProg(ml);
        vm.run();
        final int ans = vm.getStk().pop();

        assertEquals(a*b,ans);
    }

    public void evalOneMultOld(final int a, final int b, final String prog) {
        final String newProg = Integer.toString(a)+" "+Integer.toString(b)+" "+prog;
        vm.reset();
        vm.addProg(Asm.textToCode(newProg));
        vm.run();
        final int ans = vm.getStk().pop();

        assertEquals(a*b,ans);
    }

    @Test
    public void testDupTo() {

        final String prog = "1 6 dupTwo prnt drp prnt drp prnt drp prnt halt       (push 4 then execute method, print an halt)"
                + ":dupTwo over over;         (a,b --> a,b,a,b)"
                ;
        final int[] code = Asm.textToCode(prog);
        System.out.println(codeToString((code)));;
        vm.addProg(code);
        vm.run();
    }

    @Test
    public void testLabel() {
        // jumps from main -> BOB -> SUSIE which returns 12345 up the stack.
        final String prog = "noop BOB prnt halt :BOB SUSIE; :SUSIE 12345;";
        final int[] code = Asm.textToCode(prog);
//        System.out.println(codeToString((code)));;
        vm.addProg(code);
        vm.run();
        assertEquals(12345,vm.getStk().top());
    }

    private String codeToString(final int[] code) {
        final StringBuffer buf = new StringBuffer();
        buf.append("Code Length=" + code.length + " \n");
        for (int i = 0; i < code.length; i++) {
            handleOne(code, buf, i);
        }
        return buf.toString();
    }

    private void handleOne(final int[] code, final StringBuffer buf, final int index) {
        final int val = code[index];
        final IS1enum inst = lookupInstruction(val);

        final boolean isAfterPush = (index > 0 && code[index - 1] == IS1Vals.push);

        final String update = (inst == null || isAfterPush) ? "(" + val + ")"
                : (val == IS1Vals.gsb) ? "GOSUB" : inst.getName();

        buf.append(index + ":" + update + "|");

        // Newline after return statement, assuming next line is start of new method
        if (code[index] == IS1Vals.ret && !isAfterPush) buf.append("\n");
    }

    static IS1enum lookupInstruction(final int val) {
        return Arrays
                .stream(IS1enum.values())//
                .filter(z -> z.getValue() == val)//
                .findFirst()
                .orElse(null);
    }

    // private String codeToString(final int[] code) {
    // final StringBuffer buf = new StringBuffer();
    // buf.append("Code Length=" + code.length + " \n");
    // for (int i = 0; i < code.length; i++) {
    // buf.append(i + "|" + printInt(code[i]) + ",");
    // if (code[i] == IS1Vals.ret) {
    // buf.append("\n");
    // }
    // }
    //
    // buf.append('\n');
    // return buf.toString();
    // }
    // static String printInt(final int val) {
    // final IS1enum inst = lookupInstruction2(val);
    // return ((inst != null) ? inst.getName() : "Unk") + "{" + val + "}";
    // }

}
