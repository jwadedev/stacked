package ex;

public class MemoryBoundsException extends RuntimeException {

    private static final long serialVersionUID = -6043915014927750561L;

    public MemoryBoundsException(int addr, int max) {
        super("Referencing outside allowed memory bounds. addr="+addr+" max="+max);
    }

}
