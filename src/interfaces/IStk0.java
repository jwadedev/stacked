package interfaces;

public interface IStk0 {

    /**
     * @param top New item to be on top of stack
     */
    void push(int top);

    /**
     * @return Top item on stack with out removing it.
     */
    int top();

    /**
     * Remove the top item from the stack
     */
    void drop();

    /**
     * @return The contents of the stack
     */
    int[] getData();

    // static boolean equiv(final IStk0 a,final IStk0 b) {
    // if(a==b) {
    // return true;
    // }
    // if(a==null || b==null) {
    // return false;
    // }
    // final int[] dA = a.getData();
    // final int[] dB = b.getData();
    // return Arrays.equals(dA, dB);
    // }

}