package vmHw;

public class Register implements IRegister {

    int val;
    final ISrc src;
    final ISink dst;

    public Register(final ISrc src, final ISink dst) {
        this.src = src;
        this.dst = dst;
    }

    @Override
    public int getValue() {
        return val;
    }

    @Override
    public void setValue(final int val) {
        this.val = val;
    }

    @Override
    public void doWrite() {
        dst.setValue(val);
    }

    @Override
    public void doRead() {
        val = src.getValue();
    }

}
