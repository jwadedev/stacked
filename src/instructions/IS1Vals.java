package instructions;

public interface IS1Vals {


    public static final int noop = 0;
    public static final int dup = 1;
    public static final int swp = 2;

    public static final int over = 3;
    public static final int rot = 4;

    public static final int jmp = 5;
    public static final int jmpz = 6;
    public static final int gsb = 7;
    public static final int ret = 8;

    public static final int and = 9;
    public static final int or = 10;
    public static final int not = 11;

    public static final int eq = 12;
    public static final int lt = 13;
    public static final int gt = 14;

    public static final int add = 15;
    public static final int sub = 16;

    public static final int push = 17;
    public static final int drp = 18;
    public static final int prnt = 19;

    public static final int ld = 20;
    public static final int sto = 21;
    public static final int comp = 22;

    public static final int lshft = 23;
    public static final int rshft = 24;

    public static final int emit = 25;
    public static final int debug = 101;

    public static final int halt = 100;


}