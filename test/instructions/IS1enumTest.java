package instructions;

import static org.junit.Assert.*;

import org.junit.Test;

public class IS1enumTest {

    @Test
    public void testLookupInt() {
        IS1enum result = IS1enum.lookup(IS1Vals.add);
        assertNotNull(result);
        assertEquals(IS1Vals.add,result.getValue());
    }

    @Test
    public void testLookupString() {
        IS1enum result = IS1enum.lookup("dup");
        assertNotNull(result);
        assertEquals(IS1Vals.dup,result.getValue());assertEquals("dup",result.toString());
        
    }

    @Test
    public void testGetValue() {
        assertEquals(6,IS1enum.Jmpz.getValue());
    }

    @Test
    public void testGetName() {
        assertEquals("Halt",IS1enum.Halt.getName());
    }

    @Test
    public void testToString() {
        assertEquals("gsb",IS1enum.Gsb.toString());
    }
}
