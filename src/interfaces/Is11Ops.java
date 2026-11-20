package interfaces;

/**
 * Interface for classes which will handle stack operations for VM.
 */
public interface Is11Ops {

    /** Shift bits one to the right */
    void execRshft();

    /** Shift bits one to the left */
    void execLshft();

    /** Print TOS */
    void execPrint();

    /** Print TOS as a charecter */
    void execEmit();

    /** Drop top item off stack */
    void execDrop();

    /** Subtract TOS from next */
    void execSub();

    /** Add TOS to next */
    void execAdd();

    /** True if uTop > TOS */
    void execGt();

    /** True if uTop < TOS */
    void execLt();

    /** True if uTop == TOS */
    void execEq();

    /** Boolean opposite TOS. Answers 0 or 1 */
    void execNot();

    /** Invert all bits of TOS */
    void execBNot();

    /** Binary OR of stack values */
    void execOr();

    /** Binary AND of stack values */
    void execAnd();

    /** Store to Utop value to address at TOS */
    void execStore();

    /** Read value from memory location specified by TOS */
    void execLoad();

    /** Push from mem(pc) to top of stack */
    void execPush();

    /** Return from method to calling context. pc=ret.pop(); */
    void execRet();

    /** Push current pc to return stack and jump to address on TOS */
    void execGsb();

    /** If utop is 0, jump to address on TOS */
    void execJmpz();

    /** Jump to address on TOS */
    void execJmp();

    /** Push value from Utop on top of stack */
    void execOver();

    /** Rotate top 3 items on stack. 1,2,3,4 --> 2,3,1,4 */
    void execRot();

    /** Swap the top 2 items on the stack. 1,2,3 --> 2,1,3 */
    void execSwp();

    /** Push another copy of TOS onto stack */
    void execDup();

    /** Just returns the whole memory */
    IMem getMem();

    /** Returns the value of the program counter */
    int getPc();

    /** Sets the value of the program counter */
    void setPc(int newPc);

    // Should these be removed????
    /** Advance the program counter */
    void incrementPc();

    /** Return the execution stack */
    IStk1 getStk();

    /** Return the return stack */
    IStk0 getRstk();

    /** Wipe state of VM */
    void reset();

    /** Add program to PC. Note doesn't seem to belong here */
    void addProg(int[] prog);

    /** Return value at mem(pc) */
    int getInstruction();
}