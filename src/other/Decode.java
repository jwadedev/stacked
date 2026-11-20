package other;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Decode implements IDecode {

    int instruction;
    
    static Map<ISAenum,List<MOp>> mopMap = new HashMap<>();
    static {
        mopMap.put(ISAenum.jmp,Arrays.asList(MOp.setPc,MOp.drop));
        mopMap.put(ISAenum.gosub,Arrays.asList(MOp.svPc,MOp.setPc,MOp.drop));
        mopMap.put(ISAenum.jmpz, Arrays.asList(MOp.setPcZ,MOp.drop));
    }
    
    @Override
    public void setInstruction(int inst) {
        instruction = inst;
    }

    @Override
    public MOp[] getMOps() {
        // TODO Auto-generated method stub
        return null;
    }

}
