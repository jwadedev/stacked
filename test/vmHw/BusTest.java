package vmHw;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class BusTest {


    private static final int[] arr = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

    @Test
    public void testGetSetValue() {
        final IBus cut = new Bus();
        Arrays.stream(arr).forEach(i -> {
            cut.setValue(i);
            assertEquals(i, cut.getValue());
        });
    }

}
