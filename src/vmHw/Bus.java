package vmHw;

/**
 * Assume cmd will tell senders to send and receivers to receive
 * @author Parents
 *
 */
public class Bus implements IBus {

    int val;

    @Override
    public int getValue() {
        return val;
    }

    @Override
    public void setValue(final int val) {
        this.val = val;
    }

    @Override
    public void tick() {
        // Dont remember what I intended this to do????

    }

}
