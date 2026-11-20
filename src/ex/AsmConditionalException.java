package ex;

public class AsmConditionalException extends RuntimeException implements IAsmException {

    private static final long serialVersionUID = 539163612755356178L;

    public AsmConditionalException(final String msg) {
        super(msg);
    }
}
