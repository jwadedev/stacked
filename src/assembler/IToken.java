package assembler;

import java.util.List;

public interface IToken {
    /**
     * @return the set of numeric values for this token
     */
    List<Integer> getValues();

    /**
     * Set the set of numeric values for this token
     */
    void setValues(List<Integer> vals);

    /**
     * @return Name of this token. Cond {IF/ELSE/ELIF}. Number asString.
     */
    String getName();

    /**
     * @return Type of this token. Conditional, Numeric, Definition
     */
    TokeType getType();

    /**
     * @return The length of the the returned values. getValues().size()
     */
    int size();

    /**
     * @return Index of the start of this token's code in generated program
     */
    int getCodeIndex();

    /**
     * Specify index where this tokens code should start in generated program
     * @param index
     */
    void setCodeIndex(int index);

    /**
     * Specify index just after this in generated program
     * @param index
     */
    int getCodeEndIndex();

    /**
     * For token which jumps specify where it jumps to.
     * @param loc
     */
    void setJumpLoc(int loc);

    /**
     * @return Index of this token in the list of tokens
     */
    int getTokenIndex();

    int getIndexOnLine();

    int getLine();

    default String tokLocationMsg() {
        return "Token on line " + getLine() + " at token number " + getIndexOnLine() + ". ";
    }

}
