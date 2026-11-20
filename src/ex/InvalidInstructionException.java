package ex;

public class InvalidInstructionException extends RuntimeException {

    private static final long serialVersionUID = -8343119435907676709L;

    public InvalidInstructionException(final int instruction, final int pc) {
        super("Invalid Instruction:" + instruction + " at index:" + pc);
    }
}
