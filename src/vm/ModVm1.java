package vm;

import static instructions.IS1Vals.add;
import static instructions.IS1Vals.and;
import static instructions.IS1Vals.comp;
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
import interfaces.Is11Ops;
import interfaces.Ivm;
import interfaces.VmDebugable;
import ops.Is1OpsV1;

public class ModVm1 implements Ivm, VmDebugable {

    final Is11Ops ops;
    private int count = 0;

    public ModVm1(final IMem newMem, final IStk1 stk, final IStk1 rstk) {
        ops = new Is1OpsV1(newMem,stk,rstk);
    }

    @Override
    public void addProg(final int[] prog) {
        ops.addProg(prog);
    }

    @Override
    public void run() {
        while(ops.getInstruction()!=halt) {
            oneCycle();
        }
//        System.out.println("Halting Machine");
    }

    void oneCycle(){
        ++count;
        // get instruction
        final int inst = ops.getInstruction();
        // increment pc
        ops.incrementPc();
        // execute instruction
        execInstruction(inst);

    }

    void execInstruction(final int inst) {
        switch(inst) {
            case noop: ; break;
            case dup: ops.execDup();break;
            case swp: ops.execSwp();break;
            case over: ops.execOver();break;
            case rot: ops.execRot();break;
            case jmp: ops.execJmp(); break;
            case jmpz: ops.execJmpz(); break;
            case gsb: ops.execGsb();break;
            case ret: ops.execRet(); break;
            case ld: ops.execLoad(); break;
            case sto: ops.execStore(); break;
            case and: ops.execAnd(); break;
            case or: ops.execOr(); break;
            case not: ops.execNot(); break;
            case comp: ops.execBNot(); break;
            case eq: ops.execEq();break;
            case lt: ops.execLt();break;
            case gt: ops.execGt(); break;
            case add: ops.execAdd(); break;
            case sub: ops.execSub(); break;
            case push: ops.execPush(); break;
            case drp: ops.execDrop(); break;
            case lshft: ops.execLshft(); break;
            case rshft: ops.execRshft(); break;
            case prnt: ops.execPrint(); break;
            case emit:
                ops.execEmit();
                break;
            default:
                throw new InvalidInstructionException(inst, ops.getPc());
        }
    }

    @Override
    public IStk1 getStk() {
        return ops.getStk();
    }

    @Override
    public IStk0 getRstk() {
        return ops.getRstk();
    }

    @Override
    public IMem getMem() {
        return ops.getMem();
    }

    @Override
    public int getPc() {
        return ops.getPc();
    }

    @Override
    public void setPc(final int newPc) {
        count = 0;
        ops.setPc(newPc);
    }

    @Override
    public void reset() {
        ops.reset();
    }

    @Override
    public int getCount() {
        return count;
    }


}
