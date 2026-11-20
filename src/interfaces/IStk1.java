package interfaces;

public interface IStk1 extends IStk0 {

    /**
     * @return The size of the stack
     */
    int size();  // Maybe remove

    /**
     * @return The top item from the stack. This removes it from stack.
     */
    default int pop() {
        final int toRet = top();
        drop();
        return toRet;
    }

    /**
     * This clears everything off the stack.
     */
    void clear();

}