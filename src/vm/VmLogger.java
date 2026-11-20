package vm;

public class VmLogger {

    void prn(final String str, final int pc) {
        System.out.print(",{" + (pc - 1) + ":" + str + "}");
    }


}
