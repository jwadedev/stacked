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
public class MicroVm2 implements Ivm, VmDebugable {

    final private IMem mem;
    private int count = 0;
    private static final boolean debugOn = false;

    // Register files
    final private MicroStk stk;
    final private MicroStk rstk;
    // Registers
    private int pc;
    private int mar = -1;
    private int mdr = -1;
    private int inst = -1;
    private int tmp = -1;
    private int tmp2 = -1;
    private int aluIn = -1;
    private int aluOut = -1;
    private final boolean condFlag = false; // TODO: What is this here for?
    public boolean haltBit = false; // TODO use this.

    public MicroVm2(final IMem newMem, final IStk1 stk, final IStk1 rstk) {
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

            // Move to next instruction
            incrementPc();
            // ++pc;

            // decode the instruction
            final Mop[] mops = decode(inst);

            // Execute the Mops for the instruction
            execMops(mops);

            // Fetch the next instruction
            fetchInstruction();
        }
        ;
    }

    private final static Mop[] FETCH_INST_MOPS = { Bus.pcToMar, Cmd.loadMdr, Bus.mdrToInst };
    void fetchInstruction() {
        execMops(FETCH_INST_MOPS);
    }

    void incrementPc() {
        Cmd.incPc.ex(this);
    }

    static boolean intToBool(final int in) {
        return in != 0;
    }

    static int boolToInt(final boolean in) {
        return in ? 1 : 0;
    }

    void prn(final String str) {
        System.out.print(",{" + (pc - 1) + ":" + str + "}");
    }

    private static char toChar(final int top) {
        return (char) top;
    }

    @Override
    public int getCount() {
        return count;
    }

    // TODO: WHat if this was all just a Map lookup??
    // Less code, no debug, but how would we debug
    Mop[] decode(final int inst) {
        switch (inst) {
            case noop:
                if (debugOn) prn("Noop");
                return NOOP_MOPS;
            case dup:
                if (debugOn) prn("Dup");
                return DUP_MOPS;
            case swp:
                if (debugOn) prn("Swp");
                return SWP_MOPS;
            case over:
                if (debugOn) prn("Ovr(" + stk.utop() + ")");
                return OVER_MOPS;
            case rot:
                if (debugOn) prn("Rot");
                return ROT_MOPS;
            case jmp:
                if (debugOn) prn("jmp(" + stk.top() + ")");
                return (JMP_MOPS);
            case jmpz:
                if (debugOn) prn("jmpz(" + stk.top() + ")");
                return (JMPZ_MOPs);
            case gsb:
                if (debugOn) prn("Gsb(" + stk.top() + ")");
                return (GSB_MOPS);
            case ret:
                if (debugOn) prn("Ret(?)");
                return (RET_MOPS);
            case ld:
                if (debugOn) prn("Load(" + stk.top() + ") = " /* + mem2 */);
                return (LOAD_MOPS);
            case sto:
                if (debugOn) prn(/* val + */"->Store(" + stk.top() + ")");
                return (STORE_MOPS);
            case and:
                if (debugOn) prn("And");
                return (AND_MOPS);
            case or:
                if (debugOn) prn("Or");
                return (OR_MOPS);
            case not:
                if (debugOn) prn("Not");
                return (NOT_MOPS);
            case comp:
                if (debugOn) prn("compl");
                return (COMP_MOPS);
            case eq:
                if (debugOn) prn("Eq");
                return (EQ_MOPS);
            case lt:
                if (debugOn) prn("Lt(?)");
                return (LT_MOPS);
            case gt:
                if (debugOn) prn("Gt(?)");
                return (GT_MOPS);
            case add:
                if (debugOn) prn("Add");
                return (ADD_MOPS);
            case sub:
                if (debugOn) prn("Sub");
                return (SUB_MOPS);
            case push:
                if (debugOn) prn("Push(?)");
                return (PUSH_MOPS);
            case drp:
                if (debugOn) prn("drop");
                return (DROP_MOPS);
            case lshft:
                if (debugOn) prn("Lshft");
                return (LSHFT_MOPS);
            case rshft:
                if (debugOn) prn("Rshft");
                return (RSHFT_MOPS);
            case prnt:
                if (debugOn) prn("prnt");
                return PRN_MOPS;
            case emit:
                if (debugOn) prn("emit");
                return EMIT_MOPS;
            case debug:
                // TODO: Need MOP for this
                return DEBUG_MOPS;
            default:
                throw new InvalidInstructionException(inst, pc);
        }
    }

    void execMops(final Mop[] mops) {
        // Streams produces about 2.7 MIPS
        // Arrays.stream(mops).forEach(m -> execMop(m));
        // Traditional looping produces about 4.2 MIPS
        // Switching to lambda MOPs accelerated from 4.5 to 7.5 MIPs
        // Switching the JMPZ MOp to ternary made random increase to 9-17 MIPs
        // Later slowed back to 5.3, don't know why
        // Now removed method execOp, and speed up to 18 MIPs
        for (final Mop mop : mops) {
            mop.ex(this);
        }
    }

    private static final Mop[] NOOP_MOPS = {};
    private static final Mop[] HALT_MOPS = { Cmd.halt };
    private static final Mop[] RSHFT_MOPS = { Bus.stkToAluIn, Cmd.decStkInd, Alu.rshft, Bus.aluOutToStk };
    private static final Mop[] LSHFT_MOPS = { Bus.stkToAluIn, Cmd.decStkInd, Alu.lshft, Bus.aluOutToStk };
    private static final Mop[] PRN_MOPS = { Cmd.prnTop };
    private static final Mop[] EMIT_MOPS = { Cmd.emitTop };
    private static final Mop[] DROP_MOPS = { Cmd.decStkInd };
    private static final Mop[] SUB_MOPS = { Bus.stkToAluIn, Cmd.decStkInd, Alu.sub, Bus.aluOutToStk };
    private static final Mop[] ADD_MOPS = { Bus.stkToAluIn, Cmd.decStkInd, Alu.add, Bus.aluOutToStk };
    private static final Mop[] GT_MOPS = { Bus.stkToAluIn, Cmd.decStkInd, Alu.gt, Bus.aluOutToStk };
    private static final Mop[] LT_MOPS = { Bus.stkToAluIn, Cmd.decStkInd, Alu.lt, Bus.aluOutToStk };
    private static final Mop[] EQ_MOPS = { Bus.stkToAluIn, Cmd.decStkInd, Alu.eq, Bus.aluOutToStk };
    private static final Mop[] NOT_MOPS = { Alu.not, Bus.aluOutToStk };
    private static final Mop[] COMP_MOPS = { Alu.comp, Bus.aluOutToStk };
    private static final Mop[] OR_MOPS = { Bus.stkToAluIn, Cmd.decStkInd, Alu.or, Bus.aluOutToStk };
    private static final Mop[] AND_MOPS = { Bus.stkToAluIn, Cmd.decStkInd, Alu.and, Bus.aluOutToStk };
    private final static Mop[] STORE_MOPS = { Bus.stkToMar, Cmd.decStkInd, Bus.stkToMdr, Cmd.storeMdr, Cmd.decStkInd };
    private final static Mop[] LOAD_MOPS = { Bus.stkToMar, Cmd.loadMdr, Bus.mdrToStk };
    private final static Mop[] PUSH_MOPS = { Bus.pcToMar, Cmd.loadMdr, Cmd.incStkInd, Bus.mdrToStk, Cmd.incPc };
    private final static Mop[] RET_MOPS = { Bus.rstkToPc, Cmd.decRstkInd };
    private final static Mop[] GSB_MOPS = { Cmd.incRstkInd, Bus.pcToRstk, Bus.stkToPc, Cmd.decStkInd };
    private final static Mop[] JMPZ_MOPs = { Bus.stkToTmp, Cmd.decStkInd, Cmd.jmpTmpZ, Cmd.decStkInd };
    private final static Mop[] JMP_MOPS = { Bus.stkToPc, Cmd.decStkInd };
    private final static Mop[] OVER_MOPS = { Bus.stkToTmp, Cmd.decStkInd, Bus.stkToTmp2, Cmd.incStkInd,
            Cmd.incStkInd, Bus.tmp2ToStk };
    private final static Mop[] ROT_MOPS = { Bus.stkToTmp, Cmd.decStkInd, // capture 1st to tmp
            Bus.stkToTmp2, Cmd.decStkInd, // capture 2nd to tmp2
            Cmd.incRstkInd, Bus.stkToRstk, // capture 3rd to Rstk
            Bus.tmpToStk, // return 1st to stk
            Cmd.incStkInd, Bus.rstkToStk, Cmd.decRstkInd, // return 2nd to stk
            Cmd.incStkInd, Bus.tmp2ToStk }; // return 3rd to stk
    private final static Mop[] SWP_MOPS = { Bus.stkToTmp, Cmd.decStkInd, Bus.stkToTmp2, Bus.tmpToStk, Cmd.incStkInd,
            Bus.tmp2ToStk };
    private final static Mop[] DUP_MOPS = { Bus.stkToTmp, Cmd.incStkInd, Bus.tmpToStk };
    private final static Mop[] DEBUG_MOPS = { Cmd.debug };



    public interface Mop {
        public void ex(MicroVm2 vm);
    }

    enum Cmd implements Mop {
        incPc(vm -> ++vm.pc), //
        incStkInd(vm -> vm.stk.inc()), //
        decStkInd(vm -> vm.stk.drop()), //
        incRstkInd(vm -> vm.rstk.inc()), //
        decRstkInd(vm -> vm.rstk.drop()), //
        jmpTmpZ(vm -> vm.pc = (vm.stk.top() == 0) ? vm.tmp : vm.pc), //
        halt(vm -> vm.haltBit = true), //
        debug(vm -> System.out.print("\n" + vm.stk.toString())),

        // Maybe pull these out as IO
        loadMdr(vm -> vm.mdr = vm.mem.getMem(vm.mar)), //
        storeMdr(vm -> vm.mem.setMem(vm.mar, vm.mdr)), //
        prnTop(vm -> System.out.println("(" + vm.stk.top() + ")")), //
        emitTop(vm -> System.out.println("(" + toChar(vm.stk.top()) + ")")), //
        ;

        private final Mop meth;

        private Cmd(final Mop meth) {
            this.meth = meth;
        }

        @Override
        public void ex(final MicroVm2 vm) {
            meth.ex(vm);
        }
    }

    enum Bus implements Mop {
        stkToPc(vm -> vm.pc = vm.stk.top()), //
        pcToRstk(vm -> vm.rstk.toTop(vm.pc)), //
        rstkToPc(vm -> vm.pc = vm.rstk.top()), //

        pcToMar(vm -> vm.mar = vm.pc), //
        mdrToInst(vm -> vm.inst = vm.mdr), //
        mdrToStk(vm -> vm.stk.toTop(vm.mdr)), //
        stkToMar(vm -> vm.mar = vm.stk.top()), //
        stkToMdr(vm -> vm.mdr = vm.stk.top()), //

        stkToTmp(vm -> vm.tmp = vm.stk.top()), //
        tmpToStk(vm -> vm.stk.toTop(vm.tmp)), //
        stkToTmp2(vm -> vm.tmp2 = vm.stk.top()), //
        tmp2ToStk(vm -> vm.stk.toTop(vm.tmp2)), //
        stkToRstk(vm -> vm.rstk.toTop(vm.stk.top())), //
        rstkToStk(vm -> vm.stk.toTop(vm.rstk.top())), //

        aluOutToStk(vm -> vm.stk.toTop(vm.aluOut)), //
        stkToAluIn(vm -> vm.aluIn = vm.stk.top()), //
        ;
        private final Mop meth;

        private Bus(final Mop meth) {
            this.meth = meth;
        }

        @Override
        public void ex(final MicroVm2 vm) {
            meth.ex(vm);
        }

    }
    enum Alu implements Mop {
        and(vm -> vm.aluOut = vm.stk.top() & vm.aluIn), //
        or(vm -> vm.aluOut = vm.stk.top() | vm.aluIn), //
        comp(vm -> vm.aluOut = ~vm.stk.top()), //
        not(vm -> vm.aluOut = boolToInt(!intToBool(vm.stk.top()))), //
        eq(vm -> vm.aluOut = boolToInt(vm.stk.top() == vm.aluIn)), //
        lt(vm -> vm.aluOut = boolToInt(vm.stk.top() < vm.aluIn)), //
        gt(vm -> vm.aluOut = boolToInt(vm.stk.top() > vm.aluIn)), //
        sub(vm -> vm.aluOut = vm.stk.top() - vm.aluIn), //
        add(vm -> vm.aluOut = vm.stk.top() + vm.aluIn), //
        rshft(vm -> vm.aluOut = vm.stk.top() >> vm.aluIn), //
        lshft(vm -> vm.aluOut = vm.stk.top() << vm.aluIn), //
        ;

        private final Mop meth;

        private Alu(final Mop meth) {
            this.meth = meth;
        }

        @Override
        public void ex(final MicroVm2 vm) {
            meth.ex(vm);
        }

    }

}