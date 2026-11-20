package ex;

public class AsmEmptyTokenNameException extends RuntimeException implements IAsmException {

    private static final long serialVersionUID = -6813716524841819074L;

    public AsmEmptyTokenNameException(final String tokenName) {
        super("Empty named token :( :(" + tokenName + ")");
    }
}
