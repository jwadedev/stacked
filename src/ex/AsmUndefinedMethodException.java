package ex;

public class AsmUndefinedMethodException extends RuntimeException implements IAsmException {

    private static final long serialVersionUID = -1241111313056306282L;

    public AsmUndefinedMethodException(final String name) {
        super("The Method ("+name+") has not been defined.");
    }
}
