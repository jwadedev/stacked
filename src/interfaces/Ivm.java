package interfaces;

public interface Ivm {

    /**
     * Load the program into the VM.
     * @param prog The program to load into the VM.
     */
    void addProg(int[] prog);

    /**
     * Run the program which is currently loaded in the VM
     */
    void run();

    /**
     * @return The execution stack of the VM.
     */
    IStk1 getStk();

    /**
     * @return The return stack of the VM.
     */
    IStk0 getRstk();

    /**
     * @return The memory of the VM.
     */
    IMem getMem();

    /**
     * @return The program counter of the VM.
     */
    int getPc();

    /**
     * Set the program counter
     */
    void setPc(int newPc);

    /**
     * Reset the VM. This means clearing out everything in it.
     */
    void reset();


}