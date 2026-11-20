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
import stack.MicroStk;

/**
 * @author Parents
 *
 */
public class MicroVm implements Ivm, VmDebugable {

    final private MicroStk stk;
    final private MicroStk rstk;
    final private IMem mem;
    private int pc;
    private int count = 0;
    private static final boolean debugOn = false;

    public MicroVm(final IMem newMem, final IStk1 stk, final IStk1 rstk) {
        this.mem = newMem;
        this.stk = (MicroStk) stk; // TOTAL HACK
        this.rstk = (MicroStk) rstk; // TOTAL HACK
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
        for (int i = 0; i < prog.length; i++) {
            mem.setMem(i, prog[i]);
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
        fetchInstruction();
        while (inst != halt) {
            ++count;

            incrementPc();
            // execute instruction
            execInstruction(this.inst);

            fetchInstruction();
        }
        ;
        // System.out.println("Halting Machine");
    }

    void execInstruction(final int inst) {
        switch (inst) {
            case noop:
                execNoop();
                break;
            case dup:
                execDup();
                break;
            case swp:
                execSwp();
                break;
            case over:
                execOver();
                break;
            case rot:
                execRot();
                break;
            case jmp:
                execJmp();
                break;
            case jmpz:
                execJmpz();
                break;
            case gsb:
                execGsb();
                break;
            case ret:
                execRet();
                break;
            case ld:
                execLoad();
                break;
            case sto:
                execStore();
                break;
            case and:
                execAnd();
                break;
            case or:
                execOr();
                break;
            case not:
                execNot();
                break;
            case comp:
                execComp();
                break;
            case eq:
                execEq();
                break;
            case lt:
                execLt();
                break;
            case gt:
                execGt();
                break;
            case add:
                execAdd();
                break;
            case sub:
                execSub();
                break;
            case push:
                execPush();
                break;
            case drp:
                execDrop();
                break;
            case lshft:
                execLshft();
                break;
            case rshft:
                execRshft();
                break;
            case prnt:
                execPrint();
                break;
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

    void execNoop() {
        if (debugOn) prn("Noop");
        // No Mops
    }

    void execDebug() {
        System.out.println();
        System.out.print(stk.toString());
    }

    // NOTE convert to single shift only
    private static final Mop[] RSHFT_MOPS = { Mop.stkToAluIn, Mop.decStkIndex, Mop.AluRshft, Mop.aluOutToStk };
    void execRshft() {
        if (debugOn) prn("Rshft");
        execMops(RSHFT_MOPS);
    }

    // NOTE convert to single shift only
    private static final Mop[] LSHFT_MOPS = { Mop.stkToAluIn, Mop.decStkIndex, Mop.AluLshft, Mop.aluOutToStk };
    void execLshft() {
        if (debugOn) prn("Lshft");
        execMops(LSHFT_MOPS);
    }

    void prn(final String str) {
        System.out.print(",{" + (pc - 1) + ":" + str + "}");
    }

    // NOT MICRO-OPing at this time since it is just goofy at the moment
    void execPrint() {
        if (debugOn) prn("prnt");
        System.out.println("(" + stk.top() + ")");
    }

    // NOT MICRO-OPing at this time since it is just goofy at the moment
    void execEmit() {
        if (debugOn) prn("emit");
        System.out.println("(" + toChar(stk.top()) + ")");
    }

    private char toChar(final int top) {
        return (char) top;
    }

    private static final Mop[] DROP_MOPS = { Mop.decStkIndex };
    void execDrop() {
        if (debugOn) prn("drop");
        execMops(DROP_MOPS);
    }

    private static final Mop[] SUB_MOPS = { Mop.stkToAluIn, Mop.decStkIndex, Mop.AluSub, Mop.aluOutToStk };
    void execSub() {
        if (debugOn) prn("Sub");
        execMops(SUB_MOPS);
    }

    private static final Mop[] ADD_MOPS = { Mop.stkToAluIn, Mop.decStkIndex, Mop.AluAdd, Mop.aluOutToStk };
    void execAdd() {
        if (debugOn) prn("Add");
        execMops(ADD_MOPS);
    }

    private static final Mop[] GT_MOPS = { Mop.stkToAluIn, Mop.decStkIndex, Mop.AluGt, Mop.aluOutToStk };
    void execGt() {
        execMops(GT_MOPS);
        if (debugOn) prn("Gt(" + stk.top() + ")");
    }

    private static final Mop[] LT_MOPS = { Mop.stkToAluIn, Mop.decStkIndex, Mop.AluLt, Mop.aluOutToStk };
    void execLt() {
        execMops(LT_MOPS);
        if (debugOn) prn("Lt(" + stk.top() + ")");
    }

    private static final Mop[] EQ_MOPS = { Mop.stkToAluIn, Mop.decStkIndex, Mop.AluEq, Mop.aluOutToStk };
    void execEq() {
        if (debugOn) prn("Eq");
        execMops(EQ_MOPS);
    }

    private static final Mop[] NOT_MOPS = { Mop.AluNot, Mop.aluOutToStk };
    void execNot() {
        if (debugOn) prn("Not");
        execMops(NOT_MOPS);
    }

    private static final Mop[] COMP_MOPS = { Mop.AluComp, Mop.aluOutToStk };
    void execComp() {
        if (debugOn) prn("compl");
        execMops(COMP_MOPS);
    }

    static boolean intToBool(final int in) {
        return in != 0;
    }

    static int boolToInt(final boolean in) {
        return in ? 1 : 0;
    }

    private static final Mop[] OR_MOPS = { Mop.stkToAluIn, Mop.decStkIndex, Mop.AluOr, Mop.aluOutToStk };
    void execOr() {
        if (debugOn) prn("Or");
        execMops(OR_MOPS);
    }

    private static final Mop[] AND_MOPS = { Mop.stkToAluIn, Mop.decStkIndex, Mop.AluAnd, Mop.aluOutToStk };
    void execAnd() {
        if (debugOn) prn("And");
        execMops(AND_MOPS);
    }

    private final static Mop[] STORE_MOPS = { Mop.stkToMar, Mop.decStkIndex, Mop.stkToMdr, Mop.storeMem,
            Mop.decStkIndex };
    void execStore() {
        if (debugOn) prn(/* val + */"->Store(" + stk.top() + ")");
        execMops(STORE_MOPS);
    }

    private final static Mop[] LOAD_MOPS = { Mop.stkToMar, Mop.loadMem, Mop.mdrToStk };
    void execLoad() {
        if (debugOn) prn("Load(" + stk.top() + ") = " /* + mem2 */);
        execMops(LOAD_MOPS);
    }

    private final static Mop[] PUSH_MOPS = { Mop.pcToMar, Mop.loadMem, Mop.incStkIndex, Mop.mdrToStk, Mop.incPc };
    void execPush() {
        execMops(PUSH_MOPS);
        if (debugOn) prn("Push(" + mdr + ")");
    }

    private final static Mop[] RET_MOPS = { Mop.rstkToPc, Mop.decRstkInd };
    void execRet() {
        execMops(RET_MOPS);
        if (debugOn) prn("Ret(" + pc + ")");
    }

    private final static Mop[] GSB_MOPS = { Mop.incRstkInd, Mop.pcToRstk, Mop.stkToPc, Mop.decStkIndex };
    void execGsb() {
        if (debugOn) prn("Gsb(" + stk.top() + ")");
        execMops(GSB_MOPS);
    }

    private final static Mop[] JMPZ_MOPs = { Mop.stkToTmp, Mop.decStkIndex, Mop.jmpTmpZ, Mop.decStkIndex };
    void execJmpz() {
        if (debugOn) prn("jmpz(" + stk.top() + ")");
        execMops(JMPZ_MOPs);
    }

    private final static Mop[] JMP_MOPS = { Mop.stkToPc, Mop.decStkIndex };
    void execJmp() {
        if (debugOn) prn("jmp(" + stk.top() + ")");
        execMops(JMP_MOPS);
    }

    private final static Mop[] OVER_MOPS = { Mop.stkToTmp, Mop.decStkIndex, Mop.stkToTmp2, Mop.incStkIndex,
            Mop.incStkIndex,
            Mop.tmp2ToStk };
    void execOver() {
        if (debugOn) prn("Ovr(" + stk.utop() + ")");
        execMops(OVER_MOPS);
    }

    private final static Mop[] ROT_MOPS = { Mop.stkToTmp, Mop.decStkIndex, // capture 1st to tmp
            Mop.stkToTmp2, Mop.decStkIndex, // capture 2nd to tmp2
            Mop.incRstkInd, Mop.stkToRstk, // capture 3rd to Rstk
            Mop.tmpToStk, // return 1st to stk
            Mop.incStkIndex, Mop.rstkToStk, Mop.decRstkInd, // return 2nd to stk
            Mop.incStkIndex, Mop.tmp2ToStk }; // return 3rd to stk
    void execRot() {
        if (debugOn) prn("Rot");
        execMops(ROT_MOPS);
    }

    private final static Mop[] SWP_MOPS = { Mop.stkToTmp, Mop.decStkIndex, Mop.stkToTmp2, Mop.tmpToStk, Mop.incStkIndex,
            Mop.tmp2ToStk };
    void execSwp() {
        if (debugOn) prn("Swp");
        execMops(SWP_MOPS);
    }

    private final static Mop[] DUP_MOPS = { Mop.stkToTmp, Mop.incStkIndex, Mop.tmpToStk };
    void execDup() {
        if (debugOn) prn("Dup");
        execMops(DUP_MOPS);
    }

    void incrementPc() {
        execMop(Mop.incPc);
    }

    private final static Mop[] FETCH_INST_MOPS = { Mop.pcToMar, Mop.loadMem, Mop.mdrToInst };
    void fetchInstruction() {
        execMops(FETCH_INST_MOPS);
    }

    @Override
    public int getCount() {
        return count;
    }

    int mar = -1;
    int mdr = -1;
    int inst = -1;
    int tmp = -1;
    int tmp2 = -1;
    int aluIn = -1;
    int aluOut = -1;
    boolean condFlag = false;

    void execMops(final Mop[] mops) {
        // Streams produces about 2.7 MIPS
        // Arrays.stream(mops).forEach(m -> execMop(m));
        // Traditional looping produces about 4.2 MIPS
        // Switching to lambda MOPs accelerated to 7.5 MIPs
        // Switching the JMPZ MOp to ternary made random increase to 9-17 MIPs
        for (final Mop mop : mops) {
            execMop(mop);
        }
    }

    public interface Method {
        public void op(MicroVm vm);
    }

    void execMop(final Mop mop) {
        // Switching from case to lambda accelerated from 4.5 mips to 7 mips
        mop.meth.op(this);
    }

    /**
     * TODO: Moving the Mops int the enum should avoid the cost of the above switch
     * statement lookup vm -> vm.mdr = vm.mem.getMem(vm.mar);
     */
    enum Mop {
        incPc(vm -> ++vm.pc), //
        pcToMar(vm -> vm.mar = vm.pc), //
        loadMem(vm -> vm.mdr = vm.mem.getMem(vm.mar)), //
        mdrToInst(vm -> vm.inst = vm.mdr), //
        stkToPc(vm -> vm.pc = vm.stk.top()), //
        incStkIndex(vm -> vm.stk.inc()), //
        decStkIndex(vm -> vm.stk.drop()), //
        jmpTmpZ(vm -> vm.pc = (vm.stk.top() == 0) ? vm.tmp : vm.pc), //
        incRstkInd(vm -> vm.rstk.inc()), //
        decRstkInd(vm -> vm.rstk.drop()), //
        pcToRstk(vm -> vm.rstk.toTop(vm.pc)), //
        rstkToPc(vm -> vm.pc = vm.rstk.top()), //
        stkToTmp(vm -> vm.tmp = vm.stk.top()), //
        tmpToStk(vm -> vm.stk.toTop(vm.tmp)), //
        stkToTmp2(vm -> vm.tmp2 = vm.stk.top()), //
        tmp2ToStk(vm -> vm.stk.toTop(vm.tmp2)), //
        stkToRstk(vm -> vm.rstk.toTop(vm.stk.top())), //
        rstkToStk(vm -> vm.stk.toTop(vm.rstk.top())), //
        mdrToStk(vm -> vm.stk.toTop(vm.mdr)), //
        stkToMar(vm -> vm.mar = vm.stk.top()), //
        storeMem(vm -> vm.mem.setMem(vm.mar, vm.mdr)), //
        stkToMdr(vm -> vm.mdr = vm.stk.top()), //
        aluOutToStk(vm -> vm.stk.toTop(vm.aluOut)), //
        stkToAluIn(vm -> vm.aluIn = vm.stk.top()), //
        AluAnd(vm -> vm.aluOut = vm.stk.top() & vm.aluIn), //
        AluOr(vm -> vm.aluOut = vm.stk.top() | vm.aluIn), //
        AluComp(vm -> vm.aluOut = ~vm.stk.top()), //
        AluNot(vm -> vm.aluOut = boolToInt(!intToBool(vm.stk.top()))), //
        AluEq(vm -> vm.aluOut = boolToInt(vm.stk.top() == vm.aluIn)), //
        AluLt(vm -> vm.aluOut = boolToInt(vm.stk.top() < vm.aluIn)), //
        AluGt(vm -> vm.aluOut = boolToInt(vm.stk.top() > vm.aluIn)), //
        AluSub(vm -> vm.aluOut = vm.stk.top() - vm.aluIn), //
        AluAdd(vm -> vm.aluOut = vm.stk.top() + vm.aluIn), //
        AluRshft(vm -> vm.aluOut = vm.stk.top() >> vm.aluIn), //
        AluLshft(vm -> vm.aluOut = vm.stk.top() << vm.aluIn);


        private final Method meth;

        private Mop(final Method meth) {
            this.meth = meth;
        }
    }


}