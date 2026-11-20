package stack;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ex.OverflowException;
import ex.UnderflowException;
import interfaces.IStk1;
import stack.Stk1;
import interfaces.IStk0;

public class StackImplTest {

    IStk1 st = null;
    
    private void loadToStack(IStk0 stack, int... vals) {
        for(int val : vals) {
            stack.push(val);
        }
    }

    @Before
    public void setup() {
        st = new Stk1("testStack",12);
    }

    @Test
    public void testCtor() {
        assertEquals(0, st.size());
    }

    @Test
    public void testPushPull() {
        st.push(1);
        assertEquals(1, st.size());
        assertEquals(1, st.top()); st.drop();
        assertEquals(0, st.size());

        st.push(1);
        assertEquals(1, st.size());
        int peek = st.top();
        assertEquals(1, peek);
        st.push(2);
        assertEquals(2, st.size());
        assertEquals(2, st.top());st.drop();
        assertEquals(1, st.top());st.drop();

        st.push(1);
        st.push(2);
        assertEquals(2, st.top());
        st.drop();
        assertEquals(1, st.top());st.drop();
        assertEquals(0, st.size());
    }
    
    @Test
    public void testUnderflow() {
        try {
            st.pop();
            fail("Should have thrown an underflow exception");
        }catch(UnderflowException ex) {
            //Expected
        }
    }
    
    @Test
    public void testOverflow() {
        try {
            loadToStack(st,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9);
            fail("Should have thrown an underflow exception");
        }catch(OverflowException ex) {
            // Expected
        }
    }

}
