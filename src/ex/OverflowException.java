package ex;

import java.util.Arrays;

public class OverflowException extends RuntimeException {
    private static final long serialVersionUID = 6738276876175913758L;

//
  public OverflowException(final String name, final int index,final int limit,final int value,final int[] stk) {
        super(
                "Overflow in (" + name + "): " + "Can't push value " + value + " because index>=limit. index=" + index
                        + " limit=" + limit + "\nstk=" + Arrays.toString(stk));
  }
}

