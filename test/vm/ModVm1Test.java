package vm;

import org.junit.Test;

public class ModVm1Test {

    @Test
    public void test() {
        VmValidator vv = new VmValidator();
        vv.loadModVm1();
        vv.test();
    }

}
