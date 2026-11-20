package ops;

import interfaces.IMem;
import interfaces.IStk0;
import interfaces.IStk1;
import interfaces.Is11Ops;

public class Is1OpsV1 implements Is11Ops {

    final private IStk1 stk;
    final private IStk1 rstk;
    final private IMem mem;
    private int pc;
    private static final boolean debugOn = false;


    @Override
    public IMem getMem() {
        return mem;
    }
    @Override
    public int getPc() {
        return pc;
    }
    public void setPc(final int val) {
        pc = val;
    }

    public Is1OpsV1(final IMem mem, final IStk1 stk, final IStk1 rstk) {
        this.stk = stk;
        this.rstk=rstk;
        this.mem = mem;
    }

    @Override
    public void execRshft() {
        if(debugOn) prn("Rshft");
        final int f = stk.pop();
        final int s = stk.pop();
        final int a = s >> f;
        stk.push(a);
    }

    @Override
    public void execLshft() {
        if(debugOn) prn("Lshft");
        final int f = stk.pop();
        final int s = stk.pop();
        final int a = s << f;
        stk.push(a);
    }

    private void prn(final String str) {
        System.out.print(",{"+(pc-1)+":"+str+"}");
    }

    @Override
    public void execPrint() {
        if(debugOn) prn("prnt");
        System.out.println("("+stk.top()+")");
    }

    @Override
    public void execEmit() {
        if (debugOn) prn("prnt");
        final char top = (char) stk.top();
        System.out.println("(" + top + ")");
    }

    @Override
    public void execDrop() {
        if(debugOn) prn("drop");
        stk.drop();
    }

    @Override
    public void execSub() {
        if(debugOn) prn("Sub");
        final int f = stk.pop();
        final int s = stk.pop();
        final int a = s - f;
        stk.push(a);
    }

    @Override
    public void execAdd() {
        if(debugOn) prn("Add");
        final int f = stk.pop();
        final int s = stk.pop();
        final int a = f + s;
        stk.push(a);
    }

    @Override
    public void execGt() {
        if(debugOn) prn("Gt");
        final int f = stk.pop();
        final int s = stk.pop();
        final boolean a = s > f;
        stk.push(boolToInt(a));
    }

    @Override
    public void execLt() {
        if(debugOn) prn("Lt");
        final int f = stk.pop();
        final int s = stk.pop();
        final boolean a = s < f;
        stk.push(boolToInt(a));
    }

    @Override
    public void execEq() {
        if(debugOn) prn("Eq");
        final int f = stk.pop();
        final int s = stk.pop();
        final boolean a = f == s;
        stk.push(boolToInt(a));
    }


    @Override
    public void execNot() {
        if(debugOn) prn("Not");
        final int f = (stk.pop());
        stk.push(boolToInt(!intToBool(f)));
    }

    @Override
    public void execBNot() {
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

    @Override
    public void execOr() {
        if(debugOn) prn("Or");
        final int f = stk.pop();
        final int s = stk.pop();
        final int a = f | s;
        stk.push(a);
    }

    @Override
    public void execAnd() {
        if(debugOn) prn("And");
        final int f = (stk.pop());
        final int s = (stk.pop());
        final int a = f & s;
        stk.push(a);
    }

    @Override
    public void execStore() {
        if(debugOn) prn("Store");
        final int addr = stk.pop();
        final int val = stk.pop();
        mem.setMem(addr, val);
    }

    @Override
    public void execLoad() {
        if(debugOn) prn("Load");
        final int addr = stk.pop();
        stk.push(mem.getMem(addr));
    }

    @Override
    public void execPush() {
        final int val = mem.getMem(pc);
        if(debugOn) prn("Push("+val+")");
        stk.push(val);
        incrementPc();
    }

    @Override
    public void execRet() {
        if(debugOn) prn("Ret");
        pc = rstk.pop();
    }

    @Override
    public void execGsb() {
        if(debugOn) prn("Gsb("+stk.top()+")");
        rstk.push(pc);
        pc = stk.pop();
    }

    @Override
    public void execJmpz() {
        final int top = stk.pop();
        final int sec = stk.pop();
        if(sec==0) {
            if(debugOn) prn("jmpz("+top+")");
            pc = top;
        }else {
            if(debugOn) prn("Jmpz not("+top+")");
        }
    }

    @Override
    public void execJmp() {
        if(debugOn) prn("jmp("+stk.top()+")");
        pc = stk.pop();
    }

    @Override
    public void execOver() {
        if(debugOn) prn("Ovr");
        final int top = stk.pop();
        final int next = stk.pop();
        stk.push(next);
        stk.push(top);
        stk.push(next);
    }

    @Override
    public void execRot() {
        if(debugOn) prn("Rot");
        final int f = stk.pop();
        final int s = stk.pop();
        final int t = stk.pop();
        stk.push(f);
        stk.push(t);
        stk.push(s);
    }

    @Override
    public void execSwp() {
        if(debugOn) prn("Swp");
        final int f = stk.pop();
        final int s = stk.pop();
        stk.push(f);
        stk.push(s);
    }

    @Override
    public void execDup() {
        if(debugOn) prn("Dup");
        stk.push(stk.top());
    }

    @Override
    public void incrementPc() {
        ++pc;
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
    public void reset() {
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
    public int getInstruction() {
        return mem.getMem(pc);
    }
}
