package vmHw;

public interface IRegister extends ISrc, ISink {

    void doWrite();

    void doRead();

}
