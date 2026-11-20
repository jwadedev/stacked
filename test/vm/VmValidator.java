package vm;

import static instructions.IS1Vals.add;
import static instructions.IS1Vals.and;
import static instructions.IS1Vals.comp;
import static instructions.IS1Vals.drp;
import static instructions.IS1Vals.dup;
import static instructions.IS1Vals.emit;
import static instructions.IS1Vals.eq;
import static instructions.IS1Vals.gsb;
import static instructions.IS1Vals.gt;
import static instructions.IS1Vals.halt;
import static instructions.IS1Vals.jmp;
import static instructions.IS1Vals.jmpz;
import static instructions.IS1Vals.ld;
import static instructions.IS1Vals.lshft;
import static instructions.IS1Vals.lt;
import static instructions.IS1Vals.noop;
import static instructions.IS1Vals.not;
import static instructions.IS1Vals.or;
import static instructions.IS1Vals.over;
import static instructions.IS1Vals.push;
import static instructions.IS1Vals.ret;
import static instructions.IS1Vals.rot;
import static instructions.IS1Vals.rshft;
import static instructions.IS1Vals.sto;
import static instructions.IS1Vals.sub;
import static instructions.IS1Vals.swp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import assembler.Asm;
import interfaces.IMem;
import interfaces.IStk0;
import interfaces.IStk1;
import interfaces.IStk2;
import interfaces.Ivm;
import memory.Mem;
import stack.MicroStk;
import stack.Stk1;
import stack.Stk2;

public class VmValidator {

    public static int MAX_MEM = 100;
    public static int MAX_STK = 40;

    Ivm vmi;

    @Before
    public void setup() {
        loadModVm1();
    }
    private void loadVm1() {
        final IMem newMem = new Mem(new int[MAX_MEM]);
        final IStk1 stkp = new Stk1("execStk",MAX_STK);
        final IStk1 rstk = new Stk1("rtnStk",MAX_STK);
        vmi = new Vm1(newMem,stkp,rstk);
    }

    public void loadModVm1() {
        final IMem newMem = new Mem(new int[MAX_MEM]);
        final IStk1 stkp = new Stk1("execStk",MAX_STK);
        final IStk1 rstk = new Stk1("rtnStk",MAX_STK);
        vmi = new ModVm1(newMem,stkp,rstk);
    }

    public void loadModVm2() {
        final IMem newMem = new Mem(new int[MAX_MEM]);
        final IStk2 stkp = new Stk2("execStk",MAX_STK);
        final IStk2 rstk = new Stk2("rtnStk",MAX_STK);
        vmi = new ModVm2(newMem,stkp,rstk);
    }

    public void loadMicroVm() {
        final IMem newMem = new Mem(new int[MAX_MEM]);
        final MicroStk stkp = new MicroStk("execStk", MAX_STK);
        final MicroStk rstk = new MicroStk("rtnStk", MAX_STK);
        vmi = new MicroVm(newMem, stkp, rstk);
    }


    @Test
    public void test() {
        testAll(vmi);
    }

    public void testAll(final Ivm vm) {
        validateNoop(vm);
        validateDup(vm);
        validateSwp(vm);
        validateOver(vm);
        validateRot(vm);
        validateJmp(vm);
        validateJmpz(vm);
        validateGsb(vm);
        validateRet(vm);
        validateAnd(vm);
        validateOr(vm);
        validateNot(vm);
        validateBNot(vm);
        validateEq(vm);
        validateLt(vm);
        validateGt(vm);
        validateAdd(vm);
        validateSub(vm);
        validatePush(vm);
        validateDrp(vm);
        validateLd(vm);
        validateStr(vm);
        validateLshft(vm);
        validateRshft(vm);
        testEmit(vm);
        testFib(vm);
        testMult(vm);
    }


    public void validateNoop(final Ivm vm) {
        ldStk(vm.getStk(),9,9);
        run(vm,mkPrg(noop));
        equivStacks(mkStk(9,9), vm.getStk());
        assertEquals(1,vm.getPc());
        vm.reset();
    }

    public void equivStacks(final IStk0 a, final IStk0 b) {
        if(!Arrays.equals(a.getData(), b.getData())) {
            fail("Expected: "+a+" but was: "+b);
        }
    }


    public void validateDup(final Ivm vm) {
        ldStk(vm.getStk(),6);
        runChk(vm,dup,6,6);
    }

//    public void blah(Ivm vm, IStack orig, int[] prog, I )

    public void validateSwp(final Ivm vm) {
        ldStk(vm.getStk(),8,9);
        runChk(vm,swp,9,8);
    }

    public void validateOver(final Ivm vm) {
        ldStk(vm.getStk(),5,9);
        runChk(vm,over,5,9,5);
    }

    public void validateRot(final Ivm vm) {
        ldStk(vm.getStk(),7,8,9);
        runChk(vm,rot,9,7,8);
    }

    public void validateJmp(final Ivm vm) {
        ldStk(vm.getStk(),8,6);
        assertEquals(0,vm.getPc());
        final int[] prog = {noop,noop,jmp,halt,halt,halt,halt,halt};
        run(vm,prog);

        assertEquals(6,vm.getPc());
        equivStacks(mkStk(8),vm.getStk());
        equivStacks(mkStk(8),vm.getStk());
        vm.reset();
    }

    public void validateJmpz(final Ivm vm) {
        ldStk(vm.getStk(),8,12,0,6);
        assertEquals(0,vm.getPc());
        run(vm,new int[] {noop,noop,jmpz,halt,halt,halt,halt,halt});
        assertEquals(6,vm.getPc());
        equivStacks(mkStk(8,12),vm.getStk()); // 2 should have been harvested
        vm.reset();


        ldStk(vm.getStk(),8,12,1,6);
        assertEquals(0,vm.getPc());
        run(vm,new int[] {noop,noop,jmpz,halt,halt,halt,halt,halt});
        assertEquals(3,vm.getPc());
        equivStacks(mkStk(8,12),vm.getStk()); // 2 should have been harvested
        vm.reset();
    }

    public void validateGsb(final Ivm vm) {
        ldStk(vm.getStk(),6,4);
        ldStk(vm.getRstk(),1,2,3);
        final int[] prog = {gsb,halt,halt,halt,halt,halt};
        run(vm,prog);
        equivStacks(mkStk(1,2,3,1),vm.getRstk());
        assertEquals(4,vm.getPc());
        equivStacks(mkStk(6),vm.getStk());
        vm.reset();
    }

    public void validateRet(final Ivm vm) {
        assertEquals(0,vm.getPc());
        ldStk(vm.getStk(),0);
        ldStk(vm.getRstk(),4);  //return pc to this
        final int[] prog = {ret,halt,halt,halt,halt,halt}; // Endusure PC stops where
        run(vm,prog);
        assertEquals(4,vm.getPc());
        vm.reset();
    }

    public void validateAnd(final Ivm vm) {
        ldStk(vm.getStk(),1,1);
        runChk(vm,and,1);

        ldStk(vm.getStk(),0,1);
        runChk(vm,and,0);

        ldStk(vm.getStk(),1,0);
        runChk(vm,and,0);

        ldStk(vm.getStk(),0,0);
        runChk(vm,and,0);
    }

    public void validateOr(final Ivm vm) {
        ldStk(vm.getStk(),1,1);
        runChk(vm,or,1);

        ldStk(vm.getStk(),0,1);
        runChk(vm,or,1);

        ldStk(vm.getStk(),1,0);
        runChk(vm,or,1);

        ldStk(vm.getStk(),0,0);
        runChk(vm,or,0);
    }

    public void validateNot(final Ivm vm) {
        ldStk(vm.getStk(),1);
        runChk(vm,not,0);

        ldStk(vm.getStk(),0);
        runChk(vm,not,1);
    }

    public void validateBNot(final Ivm vm) {
        ldStk(vm.getStk(),1);
        runChk(vm,comp,-2);

        ldStk(vm.getStk(),-10);
        runChk(vm,comp,9);
    }

    public void validateEq(final Ivm vm) {
        ldStk(vm.getStk(),5,9);
        runChk(vm,eq,0);

        ldStk(vm.getStk(),9,9);
        runChk(vm,eq,1);
    }

    public void validateLt(final Ivm vm) {
        ldStk(vm.getStk(),5,9);
        runChk(vm,lt,1);

        ldStk(vm.getStk(),9,9);
        runChk(vm,lt,0);

        ldStk(vm.getStk(),9,5);
        runChk(vm,lt,0);
    }


    public void validateGt(final Ivm vm) {
        ldStk(vm.getStk(),5,9);
        runChk(vm,gt,0);

        ldStk(vm.getStk(),9,9);
        runChk(vm,gt,0);

        ldStk(vm.getStk(),9,5);
        runChk(vm,gt,1);
    }


    public void validateAdd(final Ivm vm) {
        ldStk(vm.getStk(),5,9);
        runChk(vm,add,14);

        ldStk(vm.getStk(),9,9);
        runChk(vm,add,18);

        ldStk(vm.getStk(),9,5);
        runChk(vm,add,14);
    }


    public void validateSub(final Ivm vm) {
        ldStk(vm.getStk(),5,9);
        runChk(vm,sub,-4);

        ldStk(vm.getStk(),9,9);
        runChk(vm,sub,0);

        ldStk(vm.getStk(),9,5);
        runChk(vm,sub,4);
    }

    public void validatePush(final Ivm vm) {
        ldStk(vm.getStk(),6);
        final int[] prog = {push,99,halt};
        run(vm,prog);
        equivStacks(vm.getStk(),mkStk(6,99));
        vm.reset();
    }

    public void validateDrp(final Ivm vm) {
        ldStk(vm.getStk(),1,2,3);
        runChk(vm,drp,1,2);

        ldStk(vm.getStk(),9,9);
        runChk(vm,drp,9);

        ldStk(vm.getStk(),5,9);
        runChk(vm,drp,5);
    }


    public void validateLd(final Ivm vm) {
        ldStk(vm.getStk(),5,12);
        vm.getMem().setMem(12, 99);
        runChk(vm,ld,5,99);
    }

    public void validateStr(final Ivm vm) {
        ldStk(vm.getStk(),5,99,12);
        run(vm,mkPrg(sto));
        assertEquals(99,vm.getMem().getMem(12));
        vm.reset();

    }


    public void validateLshft(final Ivm vm) {
        ldStk(vm.getStk(),8,0);
        runChk(vm,lshft,8);

        ldStk(vm.getStk(),8,1);
        runChk(vm,lshft,16);

        ldStk(vm.getStk(),8,2);
        runChk(vm,lshft,32);
    }

    public void testFib(final Ivm vm) {

        final String prog = "20 fib1 prnt halt      (push 4 then execute method, print an halt)"
                + ":fib1 dup                  (define jump label fib1)"
                + "dup                        (dup for later use)"
                + "0 = swp 1 = or             (boolean is it 0 or 1)"
                + "if                         (if not true skip to else)"
                + "drp 1 ret                  (happy case get rid of extra and return number 1)"
                + "else                       (begin secondary case)"
                + "dup                        (make another copy of number so there is one for each)"
                + "1 sub fib1                 (call method with n-1)"
                + "swp                        (bring orig number in front of answer from method call)"
                + "2 sub fib1                 (call method with n-2)"
                + "add                        (add results together)"
                + "ret                        (return from method)"
                + "endif                      (end of else block)"
                ;
        final int[] code = Asm.textToCode(prog);
//        System.out.println(codeToString((code)));;
        vm.addProg(code);
        vm.run();
        assertEquals(10946,vm.getStk().top());
        vm.reset();
    }

    public void validateRshft(final Ivm vm) {
        ldStk(vm.getStk(),8,0);
        runChk(vm,rshft,8);

        ldStk(vm.getStk(),8,1);
        runChk(vm,rshft,4);

        ldStk(vm.getStk(),8,2);
        runChk(vm,rshft,2);
    }

    private void testEmit(final Ivm vm) {
        ldStk(vm.getStk(), 'a', 'c', 'e');
        final int[] prog = { emit, drp, emit, drp, emit, drp, halt };
        run(vm, prog);
        vm.reset();
    }

    public void testMult(final Ivm vm) {

        final String prog = "0 0 MULT halt                   (push 4 then execute method, print an halt)"
                + ":MULT dup if _nonZero else drp drp 0 endif ;    (if cpy of b is true exec nonZero, else drop params and put zero on)"
                + ":_dupTwo over over ;         (a,b --> a,b,a,b)"
                + ":_nonZero _dupTwo 1 rshft MULT 1 lshft _posAdd ;       (multiply[a,b>>1]<<1)"
                + ":_posAdd swp 1 and if add else swp drp endif ;  (if first bit set, add A to number)"
                ;
        final int[] code = Asm.textToCode(prog);
//      System.out.println(Arrays.toString(code));
//        System.out.println(codeToString((code)));;
        vm.addProg(code);
        vm.run();
//        System.out.println(((Vm1)vm).count);

        evalOneMult(0,0,prog, vm);
        evalOneMult(1,1,prog, vm);
        evalOneMult(1,10,prog, vm);
        evalOneMult(10,1,prog, vm);
        evalOneMult(10,10,prog, vm);
        evalOneMult(10,100,prog, vm);
        evalOneMult(100,10,prog, vm);
        evalOneMult(100,100,prog, vm);
        evalOneMult(100,1000,prog, vm);
        evalOneMult(1000,100,prog, vm);
        evalOneMult(1000,1000,prog, vm);
        vm.reset();
    }
    public void evalOneMult(final int a, final int b, final String prog, final Ivm vm) {
        vm.reset();
        final int[] ml = Asm.textToCode(prog);

        ml[1] = a;
        ml[3] = b;
        vm.addProg(ml);
        vm.run();
        final int ans = vm.getStk().pop();

        assertEquals(a*b,ans);
    }




    private void runChk(final Ivm vm, final int inst, final int... vals) {
        run(vm,mkPrg(inst));
        equivStacks(mkStk(vals),vm.getStk());
        vm.reset();
    }
    private void run(final Ivm vm, final int [] prog) {
        vm.addProg(prog);
        vm.run();
    }
    private void ldStk(final IStk0 stack, final int... vals) {
        for(final int val : vals) {
            stack.push(val);
        }
    }

    private IStk0 mkStk(final int... vals) {
        final IStk0 stack = new Stk1("forCompare",vals.length);
        ldStk(stack,vals);
        return stack;
    }

    // private void chkStk(IStk1 stack, int... vals) {
    // for(final int val : vals) {
    // assertEquals(val,stack.pop());
    // }
    // }
    private int[] mkPrg(final int inst) {
        return new int[] {inst,halt};
    }
}
