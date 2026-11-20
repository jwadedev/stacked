package memory;

import ex.MemoryBoundsException;
import interfaces.IMem;

public class Mem implements IMem {

    public final int[] baseArray;

    public Mem(int[] array) {
        this.baseArray = array;
    }
    
    public void clear() {
        for(int i=0;i<baseArray.length;i++) {
            baseArray[i] = 0;
        }
    }

    @Override
    public int getMem(int addr) {
        try {
            return baseArray[addr];
        }catch(IndexOutOfBoundsException ex) {
            throw new MemoryBoundsException(addr,baseArray.length);
        }
    }

    @Override
    public void setMem(int addr, int value) {
        try {
            baseArray[addr] = value;
        }catch(IndexOutOfBoundsException ex) {
            throw new MemoryBoundsException(addr,baseArray.length);
        }
    }

}
