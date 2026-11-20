package instructions;

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

import java.util.HashMap;
import java.util.Map;

/**
 * IS1 is just short for instruction set 1.
 *
 */
public enum IS1enum{
    Noop(noop),
    Dup(dup),
    Swp(swp),
    Over(over),
    Rot(rot),
    Jmp(jmp),
    Jmpz(jmpz),
    Gsb(gsb),
    Ret(ret),
    And(and),
    Or(or),
    Not(not),
    Eq(eq),
    Lt(lt),
    Gt(gt),
    Add(add),
    Sub(sub),
    Push(push),
    Drp(drp),
    Prnt(prnt),
    Ld(ld),
    Sto(sto),
    Comp(comp),
    Lshft(lshft),
    Rshft(rshft),
    Emit(emit),
    Halt(halt), //
    Debug(debug);

    private static Map<String,IS1enum> nameMap;
    private static Map<Integer,IS1enum> valMap;

    public static IS1enum lookup(final int val) {
//        System.out.println(valMap.toString());
        return valMap.get(val);
    }
    public static IS1enum lookup(final String name) {
        return nameMap.get(name);
    }

    final private int value;

    private IS1enum(final int val) {
        value = val;
        storeMe(this);
    }

    private static void storeMe(final IS1enum inst) {
        if(valMap==null) {valMap = new HashMap<>();}
        valMap.put(inst.getValue(), inst);

        if(nameMap==null) {nameMap = new HashMap<>();}
        nameMap.put(inst.getName(), inst);

        nameMap.put(inst.getName().toLowerCase(), inst);
        if(valMap==null) System.out.println("valMap is null");
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
