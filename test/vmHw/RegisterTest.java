package vmHw;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class RegisterTest {

    private static final int[] arr = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

    @Test
    public void testGetSetValue() {
        final Register cut = new Register(null, null);
        Arrays.stream(arr).forEach(i -> {
            cut.setValue(i);
            assertEquals(i, cut.getValue());
        });
    }

    public static class TestBus implements ISrc, ISink {
        public int val = 0;
        @Override
        public void setValue(final int val) {
            this.val = val;
        }
        @Override
        public int getValue() {
            return val;
        }
    };

    @Test
    public void testDoWrite() {
        final TestBus bus = new TestBus();
        final IRegister cut = new Register(null, bus);
        Arrays.stream(arr).forEach(i -> {
            cut.setValue(i);
            cut.doWrite();
            assertEquals(i, bus.val);
        });
    }

    @Test
    public void testDoRead() {
        final TestBus bus = new TestBus();
        final IRegister cut = new Register(bus, null);
        Arrays.stream(arr).forEach(i -> {
            bus.val = i;
            cut.doRead();
            assertEquals(i, cut.getValue());
        });
    }

    @Test
    public void testGetSet() {
        final IRegister cut = new Register(null, null);
        final ISrc cSrc = cut;
        final ISink cSink = cut;

        Arrays.stream(new int[] { 1, 3, 5, 6, 8, 2 }).forEach(i -> {
            cSink.setValue(i);
            assertEquals(i, cSrc.getValue());
        });
    }

}
