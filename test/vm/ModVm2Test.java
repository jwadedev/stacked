package vm;

import org.junit.Test;

public class ModVm2Test {

    @Test
    public void test() {
        final VmValidator vv = new VmValidator();
        vv.loadModVm2();
        vv.test();
    }

}
