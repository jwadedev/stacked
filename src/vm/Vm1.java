package vm;


import static instructions.IS1Vals.add;
import static instructions.IS1Vals.and;
import static instructions.IS1Vals.comp;
import static instructions.IS1Vals.debug;
import static instructions.IS1Vals.drp;
import static instructions.IS1Vals.dup;
import static instructions.IS1Vals.emit;
import static instructions.IS1Vals.eq;
import static instructions.IS1Vals.gsb;
import static instructions.IS1Vals.gt;
import static instructions.IS1Vals.halt;
import static instructions.IS1Vals.jmp;
import static instructions.IS1Vals.jmpz;
import static instructions.IS1Vals.ld;
import static instructions.IS1Vals.lshft;
import static instructions.IS1Vals.lt;
import static instructions.IS1Vals.noop;
import static instructions.IS1Vals.not;
import static instructions.IS1Vals.or;
import static instructions.IS1Vals.over;
import static instructions.IS1Vals.prnt;
import static instructions.IS1Vals.push;
import static instructions.IS1Vals.ret;
import static instructions.IS1Vals.rot;
import static instructions.IS1Vals.rshft;
import static instructions.IS1Vals.sto;
import static instructions.IS1Vals.sub;
import static instructions.IS1Vals.swp;

import ex.InvalidInstructionException;
import interfaces.IMem;
import interfaces.IStk0;
import interfaces.IStk1;
import interfaces.Ivm;
import interfaces.VmDebugable;

public class Vm1 implements Ivm, VmDebugable  {

    final private IStk1 stk;
    final private IStk1 rstk;
    final private IMem mem;
    private int pc;
    private int count = 0;
    private static final boolean debugOn = false;

    public Vm1(final IMem newMem, final IStk1 stk, final IStk1 rstk) {
        this.mem = newMem;
        this.stk = stk;
        this.rstk = rstk;
    }

    @Override
    public void reset() {
        count = 0;
        pc = 0;
        mem.clear();
        stk.clear();
        rstk.clear();
    }

    @Override
    public void addProg(final int[] prog) {
        for(int i = 0; i<prog.length; i++) {
            mem.setMem(i,prog[i]);
        }
    }

    @Override
    public IStk1 getStk() {
        return stk;
    }

    @Override
    public IStk0 getRstk() {
        return rstk;
    }

    @Override
    public IMem getMem() {
        return mem;
    }

    @Override
    public int getPc() {
        return pc;
    }
    @Override
    public void setPc(final int val) {
        pc = val;
    }

    @Override
    public void run() {
        while(getInstruction()!=halt) {
            oneCycle();
        }
//        System.out.println("Halting Machine");
    }

    void oneCycle(){
        ++count;
        // get instruction
        final int inst = getInstruction();
        // increment pc
        incrementPc();
        // execute instruction
        execInstruction(inst);

    }


    void execInstruction(final int inst) {
        switch(inst) {
            case noop: ; break;
            case dup: execDup();break;
            case swp: execSwp();break;
            case over: execOver();break;
            case rot: execRot();break;
            case jmp: execJmp(); break;
            case jmpz: execJmpz(); break;
            case gsb: execGsb();break;
            case ret: execRet(); break;
            case ld: execLoad(); break;
            case sto: execStore(); break;
            case and: execAnd(); break;
            case or: execOr(); break;
            case not: execNot(); break;
            case comp: execBNot(); break;
            case eq: execEq();break;
            case lt: execLt();break;
            case gt: execGt(); break;
            case add: execAdd(); break;
            case sub: execSub(); break;
            case push: execPush(); break;
            case drp: execDrop(); break;
            case lshft: execLshft(); break;
            case rshft: execRshft(); break;
            case prnt: execPrint(); break;
            case emit:
                execEmit();
                break;
            case debug:
                execDebug();
                break;
            default:
                throw new InvalidInstructionException(inst, pc);
        }
    }

    void execDebug() {
        System.out.println();
        System.out.print(stk.toString());
    }

    void execRshft() {
        if(debugOn) prn("Rshft");
        final int f = stk.pop();
        final int s = stk.pop();
        final int a = s >> f;
        stk.push(a);
    }

    void execLshft() {
        if(debugOn) prn("Lshft");
        final int f = stk.pop();
        final int s = stk.pop();
        final int a = s << f;
        stk.push(a);
    }

    void prn(final String str) {
        System.out.print(",{"+(pc-1)+":"+str+"}");
    }

    void execPrint() {
        if(debugOn) prn("prnt");
        System.out.println("("+stk.top()+")");
    }

    void execEmit() {
        if (debugOn) prn("emit");
        System.out.println("(" + toChar(stk.top()) + ")");
    }

    private char toChar(final int top) {
        return (char) top;
    }

    void execDrop() {
        if(debugOn) prn("drop");
        stk.drop();
    }

    void execSub() {
        if(debugOn) prn("Sub");
        final int f = stk.pop();
        final int s = stk.pop();
        final int a = s - f;
        stk.push(a);
    }

    void execAdd() {
        if(debugOn) prn("Add");
        final int f = stk.pop();
        final int s = stk.pop();
        final int a = f + s;
        stk.push(a);
    }

    void execGt() {
        final int f = stk.pop();
        final int s = stk.pop();
        final boolean a = s > f;
        if (debugOn) prn("Gt(" + s + "," + f + "=" + a + ")");
        stk.push(boolToInt(a));
    }

    void execLt() {
        final int f = stk.pop();
        final int s = stk.pop();
        final boolean a = s < f;
        if (debugOn) prn("Lt(" + a + ")");
        stk.push(boolToInt(a));
    }

    void execEq() {
        if(debugOn) prn("Eq");
        final int f = stk.pop();
        final int s = stk.pop();
        final boolean a = f == s;
        if (debugOn) prn("Eq(" + a + ")");
        stk.push(boolToInt(a));
    }


    void execNot() {
        if(debugOn) prn("Not");
        final int f = (stk.pop());
        stk.push(boolToInt(!intToBool(f)));
    }

    void execBNot() {
        if(debugOn) prn("BNot");
        final int f = (stk.pop());
        stk.push(~f);
    }

    boolean intToBool(final int in) {
        return in!=0;
    }

    int boolToInt(final boolean in) {
        return in?1:0;
    }

    void execOr() {
        if(debugOn) prn("Or");
        final int f = stk.pop();
        final int s = stk.pop();
        final int a = f | s;
        stk.push(a);
    }

    void execAnd() {
        if(debugOn) prn("And");
        final int f = (stk.pop());
        final int s = (stk.pop());
        final int a = f & s;
        stk.push(a);
    }

    void execStore() {
        final int addr = stk.pop();
        final int val = stk.pop();
        if (debugOn) prn(val + "->Store(" + addr + ")");
        mem.setMem(addr, val);
    }

    void execLoad() {
        final int addr = stk.pop();
        final int mem2 = mem.getMem(addr);
        if (debugOn) prn("Load(" + addr + ") = " + mem2);
        stk.push(mem2);
    }

    void execPush() {
        final int val = mem.getMem(pc);
        if(debugOn) prn("Push("+val+")");
        stk.push(val);
        incrementPc();
    }

    void execRet() {
        pc = rstk.pop();
        if (debugOn) prn("Ret(" + pc + ")");
    }

    void execGsb() {
        if(debugOn) prn("Gsb("+stk.top()+")");
        rstk.push(pc);
        pc = stk.pop();
    }

    void execJmpz() {
        final int top = stk.pop();
        final int sec = stk.pop();
        if(sec==0) {
            if(debugOn) prn("jmpz("+top+")");
            pc = top;
        }else {
            if(debugOn) prn("Jmpz not("+top+")");
        }
    }

    void execJmp() {
        if(debugOn) prn("jmp("+stk.top()+")");
        pc = stk.pop();
    }

    void execOver() {
        final int top = stk.pop();
        final int next = stk.pop();
        if (debugOn) prn("Ovr(" + next + ")");
        stk.push(next);
        stk.push(top);
        stk.push(next);
    }

    void execRot() {
        if(debugOn) prn("Rot");
        final int f = stk.pop();
        final int s = stk.pop();
        final int t = stk.pop();
        stk.push(f);
        stk.push(t);
        stk.push(s);
    }

    void execSwp() {
        if(debugOn) prn("Swp");
        final int f = stk.pop();
        final int s = stk.pop();
        stk.push(f);
        stk.push(s);
    }

    void execDup() {
        if(debugOn) prn("Dup");
        stk.push(stk.top());
    }

    void incrementPc() {
        ++pc;
    }

    int getInstruction() {
        return mem.getMem(pc);
    }

    @Override
    public int getCount() {
        return count;
    }

}