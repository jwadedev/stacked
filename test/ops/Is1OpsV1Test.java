package ops;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import interfaces.IMem;
import interfaces.IStk0;
import interfaces.IStk1;
import memory.Mem;
import stack.Stk1;

public class Is1OpsV1Test {

    public static final int MAX_MEM = 100;
    public static final int MAX_STK = 20;

    public static final int TRUE = 1;
    public static final int FALSE = 0;
    
    
    Is1OpsV1 ops;
    IMem newMem;
    IStk1 stk;
    IStk1 rstk;
    
    @Before
    public void setup() {
        newMem = new Mem(new int[MAX_MEM]);
        stk = new Stk1("execStk",MAX_STK);
        rstk = new Stk1("rtnStk",MAX_STK);
        ops = new Is1OpsV1(newMem,stk,rstk);
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
        ops.execSub();
        assertEquals(2,stk.pop());
    }

    @Test
    public void testExecAdd() {
        loadToStack(stk,5,7);
        ops.execAdd();
        assertEquals(12,stk.pop());
    }

    @Test
    public void testExecGt() {
        loadToStack(stk,7,5);
        ops.execGt();
        assertEquals(TRUE,stk.pop());

        loadToStack(stk,5,7);
        ops.execGt();
        assertEquals(FALSE,stk.pop());

        loadToStack(stk,5,5);
        ops.execLt();
        assertEquals(FALSE,stk.pop());
    }

    @Test
    public void testExecLt() {
        loadToStack(stk,7,5);
        ops.execLt();
        assertEquals(FALSE,stk.pop());

        loadToStack(stk,5,7);
        ops.execLt();
        assertEquals(TRUE,stk.pop());

        loadToStack(stk,5,5);
        ops.execLt();
        assertEquals(FALSE,stk.pop());
    }

    @Test
    public void testExecEq() {
        loadToStack(stk,5,7);
        ops.execEq();
        assertEquals(FALSE,stk.pop());

        loadToStack(stk,7,5);
        ops.execEq();
        assertEquals(FALSE,stk.pop());

        loadToStack(stk,5,5);
        ops.execEq();
        assertEquals(TRUE,stk.pop());
    }

    @Test
    public void testExecBNot() {
        loadToStack(stk,999);
        assertEquals(999,stk.top());
        ops.execBNot();
        assertEquals(-1000,stk.top());
        ops.execBNot();
        assertEquals(999,stk.top());
    }
    
    @Test
    public void testExecNot() {
        loadToStack(stk,0);
        assertEquals(0,stk.top());
        ops.execNot();
        assertEquals(1,stk.top());
        ops.execNot();
        assertEquals(0,stk.top());
        
        // start with non-bool value
        loadToStack(stk,999);
        assertEquals(999,stk.top());
        ops.execNot();
        assertEquals(0,stk.top());
        ops.execNot();
        assertEquals(1,stk.top());
    }


    @Test
    public void testIntToBool() {
        assertThat(ops.intToBool(7),equalTo(true));
        assertThat(ops.intToBool(-12),equalTo(true));
        assertThat(ops.intToBool(0),equalTo(false));
    }

    @Test
    public void testBoolToInt() {
        assertThat(ops.boolToInt(true),equalTo(1));
        assertThat(ops.boolToInt(false),equalTo(0));
    }

    @Test
    public void testExecOr() {
        loadToStack(stk,4,8);
        ops.execOr();
        assertThat(stk.pop(),equalTo(12));

    }

    @Test
    public void testExecAnd() {
        loadToStack(stk,7,14);
        ops.execAnd();
        assertThat(stk.pop(),equalTo(6));
    }

    @Test
    public void testExecStore() {
        loadToStack(stk,23,99);
        assertThat(getMem(99),equalTo(0));
        ops.execStore();
        assertThat(getMem(99),equalTo(23));
    }

    @Test
    public void testExecLoad() {
        setMem(6,-99);
        loadToStack(stk,6);
        assertThat(stk.top(),equalTo(6));
        ops.execLoad();
        loadToStack(stk,6);
    }

    @Test
    public void testExecRet() {
        ops.setPc(123);
        loadToStack(rstk,99);
        assertThat(ops.getPc(),equalTo(123));
        assertThat(rstk.size(),equalTo(1));
        ops.execRet();
        assertThat(ops.getPc(),equalTo(99));
        assertThat(rstk.size(),equalTo(0));
    }

    @Test
    public void testExecGsb() {
        ops.setPc(123);
        loadToStack(rstk,99);
        loadToStack(stk,1000);
        assertThat(ops.getPc(),equalTo(123));
        assertThat(rstk.size(),equalTo(1));
        ops.execGsb();
        assertThat(ops.getPc(),equalTo(1000));
        assertThat(rstk.size(),equalTo(2));
    }

    @Test
    public void testExecJmpz() {
        ops.setPc(123);
        loadToStack(stk,FALSE,1000);
        ops.execJmpz();
        assertThat(ops.getPc(),equalTo(1000));
        
        ops.setPc(123);
        loadToStack(stk,1000,TRUE);
        ops.execJmpz();
        assertThat(ops.getPc(),equalTo(123));
    }

    @Test
    public void testExecJmp() {
        ops.setPc(123);
        loadToStack(stk,1000);
        ops.execJmp();
        assertThat(ops.getPc(),equalTo(1000));
    }

    @Test
    public void testExecOver() {
        loadToStack(stk,1,2);
        assertThat(stk.size(),equalTo(2));
        ops.execOver();
        assertThat(stk.size(),equalTo(3));
        assertEquals(stk.pop(),1);
        assertEquals(stk.pop(),2);
        assertEquals(stk.pop(),1);
        
    }

    @Test
    public void testExecRot() {
        loadToStack(stk,1,2,3);
        assertThat(stk.size(),equalTo(3));
        ops.execRot();
        assertThat(stk.size(),equalTo(3));
        assertThat(stk.pop(),equalTo(2));
        assertThat(stk.pop(),equalTo(1));
        assertThat(stk.pop(),equalTo(3));
    }

    @Test
    public void testExecSwp() {
        loadToStack(stk,1,2);
        assertThat(stk.size(),equalTo(2));
        ops.execSwp();
        assertThat(stk.size(),equalTo(2));
        assertThat(stk.pop(),equalTo(1));
        assertThat(stk.pop(),equalTo(2));
    }

    @Test
    public void testExecDup() {
        loadToStack(stk,99);
        assertThat(stk.size(),equalTo(1));
        ops.execDup();
        assertThat(stk.size(),equalTo(2));
        assertThat(stk.pop(),equalTo(99));
        assertThat(stk.pop(),equalTo(99));
    }
    

    @Test
    public void testExecLshft() {
        loadToStack(stk,10,1);
        assertThat(stk.size(),equalTo(2));
        ops.execLshft();
        assertThat(stk.size(),equalTo(1));
        assertThat(stk.pop(),equalTo(20));
    }

    @Test
    public void testExecRshft() {
        loadToStack(stk,10,1);
        assertThat(stk.size(),equalTo(2));
        ops.execRshft();
        assertThat(stk.size(),equalTo(1));
        assertThat(stk.pop(),equalTo(5));
    }
    
    @Test
    public void testIncrementPc() {
        ops.setPc(100);
        ops.incrementPc();
        assertThat(ops.getPc(),equalTo(101));
    }

    @Test
    public void testExecDrop() {
        loadToStack(stk,9,8);
        assertThat(stk.size(),equalTo(2));
        assertThat(stk.top(),equalTo(8));
        ops.execDrop();

        assertThat(stk.size(),equalTo(1));
        assertThat(stk.top(),equalTo(9));
    }

    @Test
    public void testExecPush() {
        assertThat(stk.size(),equalTo(0));
        ops.setPc(5);
        ops.getMem().setMem(5, 99);
        ops.getMem().setMem(6, 1089);
        
        ops.execPush();
        assertThat(stk.size(),equalTo(1));
        assertThat(stk.top(),equalTo(99));
        assertThat(ops.getPc(),equalTo(6));

        ops.execPush();
        assertThat(stk.size(),equalTo(2));
        assertThat(stk.top(),equalTo(1089));
        assertThat(ops.getPc(),equalTo(7));
    }


}
