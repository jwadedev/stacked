package interfaces;

public interface IMem {

    /**
     * @param addr Address to return.
     * @return the value stored at specified address.
     */
    int getMem(int addr);

    /**
     * Set the value at the specified address
     * @param addr Adddress to store at.
     * @param value Value to store at that address.
     */
    void setMem(int addr,int value);

    /**
     * Clear the values out of the memory
     */
    void clear();
}
