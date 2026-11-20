package ex;

import assembler.IToken;

public class AsmDuplicateDeclarationException extends RuntimeException implements IAsmException {

    private static final long serialVersionUID = -4817538968599188804L;

    public AsmDuplicateDeclarationException(final IToken tok) {
        super("Duplicate Name (" + tok.getName() + ") in code at index:" + tok.getCodeIndex() + tok.tokLocationMsg());
    }

}
