package vm;

import static instructions.IS1Vals.add;
import static instructions.IS1Vals.drp;
import static instructions.IS1Vals.dup;
import static instructions.IS1Vals.eq;
import static instructions.IS1Vals.gsb;
import static instructions.IS1Vals.halt;
import static instructions.IS1Vals.jmpz;
import static instructions.IS1Vals.or;
import static instructions.IS1Vals.prnt;
import static instructions.IS1Vals.push;
import static instructions.IS1Vals.ret;
import static instructions.IS1Vals.rot;
import static instructions.IS1Vals.sub;
import static instructions.IS1Vals.swp;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import interfaces.IMem;
import interfaces.IStk1;
import interfaces.Ivm;
import interfaces.VmDebugable;
import memory.Mem;
import stack.MicroStk;
import stack.Stk1;
import stack.Stk2;

public class TestFibinochi {

    public static final int FIB_FOR = 25;

    public static final int MAX_MEM = 100;
    public static final int MAX_STK = 50;

    IMem mem;
    IStk1 execStack;
    IStk1 retStack;
    Ivm vm;


    @Before
    public void setup() {
        // loadVm1();
        // loadModVm1();
        // loadModVm2();
        // loadMicroVm();
        loadMicroVm2();
    }

    private void loadVm1() {
        mem = new Mem(new int[MAX_MEM]);
        execStack = new Stk1("execStk",MAX_STK);
        retStack = new Stk1("rtnStk",MAX_STK);
        vm =  new Vm1(mem,execStack,retStack);
    }
    private void loadModVm1() {
        mem = new Mem(new int[MAX_MEM]);
        execStack = new Stk1("execStk",MAX_STK);
        retStack = new Stk1("rtnStk",MAX_STK);
        vm = new ModVm1(mem,execStack,retStack);
    }
    private void loadModVm2() {
        mem = new Mem(new int[MAX_MEM]);
        final Stk2 estk = new Stk2("execStk",MAX_STK);
        execStack = estk;
        final Stk2 rstk = new Stk2("rtnStk",MAX_STK);
        retStack = rstk;
        vm = new ModVm2(mem,estk,rstk);
    }

    private void loadMicroVm() {
        mem = new Mem(new int[MAX_MEM]);
        final MicroStk estk = new MicroStk("execStk", MAX_STK);
        execStack = estk;
        final MicroStk rstk = new MicroStk("rtnStk", MAX_STK);
        retStack = rstk;
        vm = new MicroVm(mem, estk, rstk);
    }

    private void loadMicroVm2() {
        mem = new Mem(new int[MAX_MEM]);
        final MicroStk estk = new MicroStk("execStk", MAX_STK);
        execStack = estk;
        final MicroStk rstk = new MicroStk("rtnStk", MAX_STK);
        retStack = rstk;
        vm = new MicroVm2(mem, estk, rstk);
    }

    @Test
    public void testJava() {
        assertEquals(121393, fib(FIB_FOR));
    }

    int[] fibProg = {push, FIB_FOR, push, 7, gsb, prnt, halt, // MAIN: load 40 for evaluation, then gosub to function : 0-5
            // function...
            dup,                                    // reserve value 2nd half of func                       6
            dup,push,0,eq,swp,push,1,eq,or,         // bool, is top equal to 0 or one                       7-15
            push, 24, jmpz,                    // If false jump over following statement              16-18
            drp, push, 1, ret,                      // drop param and return 1 to caller                    19-23
            dup,                                    // dup param for second call                            24
            push, 1, sub, push ,7, gsb,        // subtract one from param, and then make recursive call 25-31
            swp,                                    // bring orig param back to top                         32
            push, 2, sub, push, 7, gsb,        // subtract 2 from param, and make recursive call       33-39
            add, ret,                               // add 2 results together and return                    40-42
    };

    // @Test
    // This is commented out, because it is all covered by the PERF test below
    public void testVm() {
        vm.addProg(fibProg);
        vm.run();
        System.out.println(((VmDebugable)vm).getCount());
        assertEquals(fib(FIB_FOR),vm.getStk().top());
    }

    int[] fibProgPerf = {push, FIB_FOR, push, 6, gsb, halt, // MAIN: load 40 for evaluation, then gosub to function : 0-5
            // function...
            dup,                                    // reserve value 2nd half of func                       6
            dup,push,0,eq,swp,push,1,eq,or,         // bool, is top equal to 0 or one                       7-15
            push, 23, jmpz,                    // If false jump over following statement              16-18
            drp, push, 1, ret,                      // drop param and return 1 to caller                    19-23
            dup,                                    // dup param for second call                            24
            push, 1, sub, push ,6, gsb,        // subtract one from param, and then make recursive call 25-31
            swp,                                    // bring orig param back to top                         32
            push, 2, sub, push, 6, gsb,        // subtract 2 from param, and make recursive call       33-39
            add, ret,                               // add 2 results together and return                    40-42
    };

    @Test
    public void testVmPerf() {
        final int[] perfFibProg1 = fibProgPerf.clone();
        perfFibProg1[1] = 22;

        vm.addProg(perfFibProg1);
        vm.run();
        vm.reset();

        final int[] perfFibProg2 = fibProgPerf.clone();
        perfFibProg2[1] = 27;
        vm.addProg(perfFibProg2);
        final long t0 = System.currentTimeMillis();
        vm.run();
        final long t1 = System.currentTimeMillis();

        final long totTime = t1-t0;
        System.out.println("Time="+totTime);
        System.out.println("Ops="+((VmDebugable)vm).getCount());

        final double mips = ((0.0+((VmDebugable)vm).getCount())/totTime)/1000;
        System.out.println("MIPS=" + mips);

//      14930352
//      assertEquals(fib(FIB_FOR),vm.getStk().top());
    }


    int[] fibProg2 = {push, FIB_FOR, push, 7, gsb, prnt, halt, // MAIN: load 40 for evaluation, then gosub to function : 0-6
                                                    // function...
            dup,                                    // reserve value 2nd half of func                       7
            push, 0, eq,                            // Is it equal to zero
            push, 20, jmpz,                    // If not zero skip quick return
            drp, push, 0, push, 1, ret,             // Drop n and Return values for fib (-1) & fib(0)
            push, 1, sub, push, 7, gsb,             // Subtract 1 from n and call recursively
            dup, rot, add, ret,
    };

    @Test
    public void testVm2() {
        vm.addProg(fibProg2);
        vm.run();
        System.out.println(((VmDebugable)vm).getCount());
        assertEquals(fib(FIB_FOR),execStack.top());

    }

    @Test
    public void testJava2() {
        assertEquals(121393, fib2(FIB_FOR));
    }


    private Pair fib2impl(final int n) {
        if(n==0) {
            return new Pair(0,1);
        }
        final Pair p = fib2impl(n-1);
        return new Pair(p.top,p.next+p.top);
    }

    private static class Pair{
        public Pair(final int n,final int t) {this.next=n;this.top=t;}
        int next;
        int top;
    }
    int fib2(final int n) {
        return fib2impl(n).top;
    }

    int fib(final int in) {
        return (in==0 || in==1) ? 1 : fib(in-1)+fib(in-2);
    }

}
