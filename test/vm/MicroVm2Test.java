package vm;

import org.junit.Test;

public class MicroVm2Test {

    @Test
    public void test() {
        final VmValidator vv = new VmValidator();
        vv.loadMicroVm();
        vv.test();
    }

}
