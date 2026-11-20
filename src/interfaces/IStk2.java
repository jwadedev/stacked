package interfaces;

public interface IStk2 extends IStk1 {

    /**
     * @return The item UnderTop. Hence the name utop.
     */
    int utop();

    /**
     * Replace current item at top of stack with this one.
     * @param top The value which will now be at the top of the stack.
     */
    void replace1(int top);

    /**
     * Replace the top item on the stack with these 2 items.
     * @param top New top of stack.
     * @param nxt New thing under top of stack.
     */
    void replace1(int top,int nxt);

    /**
     * Replace the top 2 items on stack with this single item
     * @param top The new item on top of stack.
     */
    void replace2(int top);

    /**
     * Replace the top 2 items on the stack with these 2 items
     * @param top The new item which will be on top of the stack.
     * @param nxt The new item under the top of the stack
     */
    void replace2(int top,int nxt);
}
