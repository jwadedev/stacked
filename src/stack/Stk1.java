package stack;

import java.util.Arrays;

import ex.OverflowException;
import ex.UnderflowException;
import interfaces.IStk1;

public class Stk1 implements IStk1 {

    final int[] array;
    int index = -1;
    private final String name;

    public Stk1(final String name,final int i) {
        this.name = name;
        array = new int[i];
    }

    @Override
    public void clear() {
        index = -1;
    }

    @Override
    public int size() {
        return index+1;
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
    public int pop() {
        try {
            return array[index--];
        }catch(final ArrayIndexOutOfBoundsException ex) {
            throw new UnderflowException(name,index);
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
    public String toString() {
        return name+":"+index+": "+Arrays.toString(slice());
    }

    private int[] slice() {
        return Arrays.copyOfRange(array, 0, index+1);
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj == null || !(obj instanceof Stk1)) {
            return false;
        }
        final Stk1 si = (Stk1)obj;
        return Arrays.equals(slice(), si.slice());
    }

    @Override
    public int[] getData() {
        return slice().clone();
    }

}
