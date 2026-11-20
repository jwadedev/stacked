package vm;

import static instructions.IS1Vals.add;
import static instructions.IS1Vals.eq;
import static instructions.IS1Vals.halt;
import static instructions.IS1Vals.push;
import static instructions.IS1Vals.sub;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import ex.InvalidInstructionException;
import ex.MemoryBoundsException;
import instructions.IS1Vals;
import interfaces.IMem;
import interfaces.IStk0;
import interfaces.IStk1;
import memory.Mem;
import stack.Stk1;

public class Vm1Test {

    public static final int MAX_MEM = 100;
    public static final int MAX_STK = 20;
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    Vm1 vm;
    IMem newMem;
    IStk1 stk;
    IStk1 rstk;


    int getMem(final int addr){
        return newMem.getMem(addr);
    }

    void setMem(final int addr, final int val) {
        newMem.setMem(addr, val);
    }

    @Before
    public void setup() {
        newMem = new Mem(new int[MAX_MEM]);
        stk = new Stk1("execStk",MAX_STK);
        rstk = new Stk1("rtnStk",MAX_STK);
        vm = new Vm1(newMem,stk,rstk);
    }

    @Test
    public void testOneCycle() {
        vm.setPc(33);
        setMem(vm.getPc(),IS1Vals.dup);

        loadToStack(stk,23,45);
        assertThat(stk.size(),equalTo(2));
        vm.oneCycle();
        assertThat(stk.size(),equalTo(3));
        assertThat(vm.getPc(),equalTo(34));
        assertThat(stk.pop(),equalTo(45));
        assertThat(stk.pop(),equalTo(45));
        assertThat(stk.pop(),equalTo(23));
    }

    @Test
    public void testRun() {
        vm.setPc(0);
        // 60 + 60 - 20 == 100
        final int[] prog = {push , 60, push, 60, add , push, 20, sub, push, 100, eq, halt};
        vm.addProg(prog);

        vm.run();
        assertThat(stk.top(), equalTo(TRUE));
    }

    @Test
    public void testExecInstruction() {
        vm.execInstruction(0); // No-op
        try {
            vm.execInstruction(1000); // Invalid
            fail("Should throw invalid instruction");
        }catch(final InvalidInstructionException inst) {
            // Expected
        }
    }

    private void loadToStack(final IStk0 stack, final int... vals) {
        for(final int val : vals) {
            stack.push(val);
        }
    }

    @Test
    public void testExecSub() {
        loadToStack(stk,7,5);
        vm.execSub();
        assertEquals(2,stk.pop());
    }

    @Test
    public void testExecAdd() {
        loadToStack(stk,5,7);
        vm.execAdd();
        assertEquals(12,stk.pop());
    }

    @Test
    public void testExecGt() {
        loadToStack(stk,7,5);
        vm.execGt();
        assertEquals(TRUE,stk.pop());

        loadToStack(stk,5,7);
        vm.execGt();
        assertEquals(FALSE,stk.pop());

        loadToStack(stk,5,5);
        vm.execLt();
        assertEquals(FALSE,stk.pop());
    }

    @Test
    public void testExecLt() {
        loadToStack(stk,7,5);
        vm.execLt();
        assertEquals(FALSE,stk.pop());

        loadToStack(stk,5,7);
        vm.execLt();
        assertEquals(TRUE,stk.pop());

        loadToStack(stk,5,5);
        vm.execLt();
        assertEquals(FALSE,stk.pop());
    }

    @Test
    public void testExecEq() {
        loadToStack(stk,5,7);
        vm.execEq();
        assertEquals(FALSE,stk.pop());

        loadToStack(stk,7,5);
        vm.execEq();
        assertEquals(FALSE,stk.pop());

        loadToStack(stk,5,5);
        vm.execEq();
        assertEquals(TRUE,stk.pop());
    }

    @Test
    public void testExecBNot() {
        loadToStack(stk,999);
        assertEquals(999,stk.top());
        vm.execBNot();
        assertEquals(-1000,stk.top());
        vm.execBNot();
        assertEquals(999,stk.top());
    }

    @Test
    public void testExecNot() {
        loadToStack(stk,0);
        assertEquals(0,stk.top());
        vm.execNot();
        assertEquals(1,stk.top());
        vm.execNot();
        assertEquals(0,stk.top());

        // start with non-bool value
        loadToStack(stk,999);
        assertEquals(999,stk.top());
        vm.execNot();
        assertEquals(0,stk.top());
        vm.execNot();
        assertEquals(1,stk.top());
    }


    @Test
    public void testIntToBool() {
        assertThat(vm.intToBool(7),equalTo(true));
        assertThat(vm.intToBool(-12),equalTo(true));
        assertThat(vm.intToBool(0),equalTo(false));
    }

    @Test
    public void testBoolToInt() {
        assertThat(vm.boolToInt(true),equalTo(1));
        assertThat(vm.boolToInt(false),equalTo(0));
    }

    @Test
    public void testExecOr() {
        loadToStack(stk,4,8);
        vm.execOr();
        assertThat(stk.pop(),equalTo(12));

    }

    @Test
    public void testExecAnd() {
        loadToStack(stk,7,14);
        vm.execAnd();
        assertThat(stk.pop(),equalTo(6));
    }

    @Test
    public void testExecStore() {
        loadToStack(stk,23,99);
        assertThat(getMem(99),equalTo(0));
        vm.execStore();
        assertThat(getMem(99),equalTo(23));
    }

    @Test
    public void testExecLoad() {
        setMem(6,-99);
        loadToStack(stk,6);
        assertThat(stk.top(),equalTo(6));
        vm.execLoad();
        loadToStack(stk,6);
    }

    @Test
    public void testExecRet() {
        vm.setPc(123);
        loadToStack(rstk,99);
        assertThat(vm.getPc(),equalTo(123));
        assertThat(rstk.size(),equalTo(1));
        vm.execRet();
        assertThat(vm.getPc(),equalTo(99));
        assertThat(rstk.size(),equalTo(0));
    }

    @Test
    public void testExecGsb() {
        vm.setPc(123);
        loadToStack(rstk,99);
        loadToStack(stk,1000);
        assertThat(vm.getPc(),equalTo(123));
        assertThat(rstk.size(),equalTo(1));
        vm.execGsb();
        assertThat(vm.getPc(),equalTo(1000));
        assertThat(rstk.size(),equalTo(2));
    }

    @Test
    public void testExecJmpz() {
        vm.setPc(123);
        loadToStack(stk,FALSE,1000);
        vm.execJmpz();
        assertThat(vm.getPc(),equalTo(1000));

        vm.setPc(123);
        loadToStack(stk,1000,TRUE);
        vm.execJmpz();
        assertThat(vm.getPc(),equalTo(123));
    }

    @Test
    public void testExecJmp() {
        vm.setPc(123);
        loadToStack(stk,1000);
        vm.execJmp();
        assertThat(vm.getPc(),equalTo(1000));
    }

    @Test
    public void testExecOver() {
        loadToStack(stk,1,2);
        assertThat(stk.size(),equalTo(2));
        vm.execOver();
        assertThat(stk.size(),equalTo(3));
        assertEquals(stk.pop(),1);
        assertEquals(stk.pop(),2);
        assertEquals(stk.pop(),1);

    }

    @Test
    public void testExecRot() {
        loadToStack(stk,1,2,3);
        assertThat(stk.size(),equalTo(3));
        vm.execRot();
        assertThat(stk.size(),equalTo(3));
        assertThat(stk.pop(),equalTo(2));
        assertThat(stk.pop(),equalTo(1));
        assertThat(stk.pop(),equalTo(3));
    }

    @Test
    public void testExecSwp() {
        loadToStack(stk,1,2);
        assertThat(stk.size(),equalTo(2));
        vm.execSwp();
        assertThat(stk.size(),equalTo(2));
        assertThat(stk.pop(),equalTo(1));
        assertThat(stk.pop(),equalTo(2));
    }

    @Test
    public void testExecDup() {
        loadToStack(stk,99);
        assertThat(stk.size(),equalTo(1));
        vm.execDup();
        assertThat(stk.size(),equalTo(2));
        assertThat(stk.pop(),equalTo(99));
        assertThat(stk.pop(),equalTo(99));
    }


    @Test
    public void testExecLshft() {
        loadToStack(stk,10,1);
        assertThat(stk.size(),equalTo(2));
        vm.execLshft();
        assertThat(stk.size(),equalTo(1));
        assertThat(stk.pop(),equalTo(20));
    }

    @Test
    public void testExecRshft() {
        loadToStack(stk,10,1);
        assertThat(stk.size(),equalTo(2));
        vm.execRshft();
        assertThat(stk.size(),equalTo(1));
        assertThat(stk.pop(),equalTo(5));
    }

    // @Test
    public void testExecEmit() {
        loadToStack(stk, 'Q');
        vm.execEmit();
    }

    @Test
    public void testIncrementPc() {
        vm.setPc(100);
        vm.incrementPc();
        assertThat(vm.getPc(),equalTo(101));
    }

    @Test
    public void testGetInstruction() {
        setMem(1,123);
        setMem(2,234);
        setMem(3,345);

        for(int i=0;i<4;i++) {
            vm.setPc(i);
            assertThat(vm.getInstruction(),equalTo(getMem(i)));
        }
    }

    @Test
    public void testMemBounds() {
        try {
            loadToStack(stk,23,-5);
            vm.execStore();
            fail("Should have been out of bounds too low");
        }catch(final MemoryBoundsException ex) {
            // Expected
        }
        try {
            loadToStack(stk,23,MAX_MEM);
            vm.execStore();
            fail("Should have been out of bounds too high");
        }catch(final MemoryBoundsException ex) {
            // Expected
        }
    }


}
