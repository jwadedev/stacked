package stack;

import java.util.Arrays;

import ex.OverflowException;
import ex.UnderflowException;
import interfaces.IStk2;

public class Stk2 implements IStk2 {

    final int[] array;
    int index = -1;
    private final String name;

    public Stk2(final String name,final int i) {
        this.name = name;
        array = new int[i];
    }

    @Override
    public void push(final int val) {
        try {
            array[++index]=val;
        }catch(final IndexOutOfBoundsException e) {
            throw new OverflowException(name, index, array.length, val, slice());
        }
    }

    @Override
    public int top() {
        try {
            return array[index];
        }catch(final ArrayIndexOutOfBoundsException ex) {
            throw new UnderflowException(name,index);
        }
    }
    @Override
    public void drop() {
        index--;
    }

    @Override
    public int utop() {
        try {
            return array[index-1];
        }catch(final ArrayIndexOutOfBoundsException ex) {
            throw new UnderflowException(name,index-1);
        }
    }

    @Override
    public void replace1(final int top) {
        array[index] = top;
    }

    @Override
    public void replace1(final int top, final int nxt) {
        array[index] = nxt;
        array[++index]=top;
    }

    @Override
    public void replace2(final int top) {
        array[--index] = top;
    }

    @Override
    public void replace2(final int nxt, final int top) {
        array[index-1] = nxt;
        array[index]=top;
    }

    @Override
    public int size() {
        return index+1;
    }

    @Override
    public void clear() {
        index = -1;
    }

    @Override
    public String toString() {
        return name+":"+index+": "+Arrays.toString(slice());
    }

    private int[] slice() {
        return Arrays.copyOfRange(array, 0, index+1);
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj == null || !(obj instanceof Stk2)) {
            return false;
        }
        final Stk2 si = (Stk2)obj;
        return Arrays.equals(slice(), si.slice());
    }

    @Override
    public int[] getData() {
        return slice().clone();
    }
}
