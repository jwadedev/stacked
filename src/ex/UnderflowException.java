package ex;

public class UnderflowException extends RuntimeException {

    private static final long serialVersionUID = -768703938306198322L;

    //    
    public UnderflowException(String name, int index) {
        super("Underflow in ("+name+"): "+"Can't pop when index<0. index="+index);
    }
}
