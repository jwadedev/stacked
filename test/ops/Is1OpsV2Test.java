package ops;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import interfaces.IMem;
import interfaces.IStk0;
import interfaces.IStk2;
import memory.Mem;
import stack.Stk2;

public class Is1OpsV2Test {

    public static final int MAX_MEM = 100;
    public static final int MAX_STK = 20;

    public static final int TRUE = 1;
    public static final int FALSE = 0;
    
    
    Is1OpsV2 vm;
    IMem newMem;
    IStk2 stk;
    IStk2 rstk;
    
    @Before
    public void setup() {
        newMem = new Mem(new int[MAX_MEM]);
        stk = new Stk2("execStk",MAX_STK);
        rstk = new Stk2("rtnStk",MAX_STK);
        vm = new Is1OpsV2(newMem,stk,rstk);
    }
    
    int getMem(int addr){
        return newMem.getMem(addr);
    }
    
    void setMem(int addr, int val) {
        newMem.setMem(addr, val);
    }
    

    private void loadToStack(IStk0 stack, int... vals) {
        for(final int val : vals) {
            stack.push(val);
        }
    }
    
    @Test
    public void testExecSub() {
        loadToStack(stk,7,5);
        assertEquals(stk.size(),2);
        vm.execSub();
        assertEquals(stk.size(),1);
        assertEquals(2,stk.pop());
    }

    @Test
    public void testExecAdd() {
        loadToStack(stk,5,7);
        assertEquals(stk.size(),2);
        vm.execAdd();
        assertEquals(stk.size(),1);
        assertEquals(12,stk.pop());
    }

    @Test
    public void testExecGt() {
        loadToStack(stk,7,5);
        assertEquals(stk.size(),2);
        vm.execGt();
        assertEquals(stk.size(),1);
        assertEquals(TRUE,stk.pop());

        loadToStack(stk,5,7);
        assertEquals(stk.size(),2);
        vm.execGt();
        assertEquals(stk.size(),1);
        assertEquals(FALSE,stk.pop());

        loadToStack(stk,5,5);
        assertEquals(stk.size(),2);
        vm.execLt();
        assertEquals(stk.size(),1);
        assertEquals(FALSE,stk.pop());
    }

    @Test
    public void testExecLt() {
        loadToStack(stk,7,5);
        assertThat(stk.size(),equalTo(2));
        vm.execLt();
        assertThat(stk.size(),equalTo(1));
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
        assertThat(stk.size(),equalTo(2));
        vm.execEq();
        assertThat(stk.size(),equalTo(1));
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
        assertThat(stk.size(),equalTo(1));
        vm.execBNot();
        assertThat(stk.size(),equalTo(1));
        assertEquals(-1000,stk.top());
        vm.execBNot();
        assertEquals(999,stk.top());
    }
    
    @Test
    public void testExecNot() {
        loadToStack(stk,0);
        assertEquals(0,stk.top());
        assertThat(stk.size(),equalTo(1));
        vm.execNot();
        assertThat(stk.size(),equalTo(1));
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
        assertThat(stk.size(),equalTo(2));
        vm.execOr();
        assertThat(stk.size(),equalTo(1));
        assertThat(stk.pop(),equalTo(12));

    }

    @Test
    public void testExecAnd() {
        loadToStack(stk,7,14);
        assertThat(stk.size(),equalTo(2));
        vm.execAnd();
        assertThat(stk.size(),equalTo(1));
        assertThat(stk.pop(),equalTo(6));
    }

    @Test
    public void testExecStore() {
        loadToStack(stk,23,99);
        assertThat(getMem(99),equalTo(0));
        assertThat(stk.size(),equalTo(2));
        vm.execStore();
        assertThat(stk.size(),equalTo(0));
        assertThat(getMem(99),equalTo(23));
    }

    @Test
    public void testExecLoad() {
        setMem(6,-99);
        loadToStack(stk,6);
        assertThat(stk.top(),equalTo(6));
        assertThat(stk.size(),equalTo(1));
        vm.execLoad();
        assertThat(stk.size(),equalTo(1));
        loadToStack(stk,6);
    }

    @Test
    public void testExecRet() {
        vm.setPc(123);
        loadToStack(rstk,99);
        assertThat(vm.getPc(),equalTo(123));
        assertThat(rstk.size(),equalTo(1));
        assertThat(stk.size(),equalTo(0));
        vm.execRet();
        assertThat(stk.size(),equalTo(0));
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
        assertThat(stk.size(),equalTo(1));
        vm.execGsb();
        assertThat(stk.size(),equalTo(0));
        assertThat(vm.getPc(),equalTo(1000));
        assertThat(rstk.size(),equalTo(2));
    }

    @Test
    public void testExecJmpz() {
        vm.setPc(123);
        loadToStack(stk,FALSE,1000);
        assertThat(stk.size(),equalTo(2));
        vm.execJmpz();
        assertThat(stk.size(),equalTo(0));
        assertThat(vm.getPc(),equalTo(1000));
        
        vm.setPc(123);
        loadToStack(stk,1000,TRUE);
        assertThat(stk.size(),equalTo(2));
        vm.execJmpz();
        assertThat(stk.size(),equalTo(0));
        assertThat(vm.getPc(),equalTo(123));
    }

    @Test
    public void testExecJmp() {
        vm.setPc(123);
        loadToStack(stk,1000);
        assertThat(stk.size(),equalTo(1));
        vm.execJmp();
        assertThat(stk.size(),equalTo(0));
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
    
    @Test
    public void testIncrementPc() {
        vm.setPc(100);
        vm.incrementPc();
        assertThat(vm.getPc(),equalTo(101));
    }

    @Test
    public void testExecDrop() {
        loadToStack(stk,9,8);
        assertThat(stk.size(),equalTo(2));
        assertThat(stk.top(),equalTo(8));
        vm.execDrop();

        assertThat(stk.size(),equalTo(1));
        assertThat(stk.top(),equalTo(9));
    }

    @Test
    public void testExecPush() {
        assertThat(stk.size(),equalTo(0));
        vm.setPc(5);
        vm.getMem().setMem(5, 99);
        vm.getMem().setMem(6, 1089);
        
        vm.execPush();
        assertThat(stk.size(),equalTo(1));
        assertThat(stk.top(),equalTo(99));
        assertThat(vm.getPc(),equalTo(6));

        vm.execPush();
        assertThat(stk.size(),equalTo(2));
        assertThat(stk.top(),equalTo(1089));
        assertThat(vm.getPc(),equalTo(7));
    }


}
