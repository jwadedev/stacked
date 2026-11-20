package other;

public enum ISAenum {
	
    push,
    pop,
    
    dup,
    swap,
    over,
    
    jmp,        // cp top to pc
    jmpz,       // if 0 cp top to pc
    
    gosub,      // save pc
    ret,        // bring back pc
    pushR,      // rstack(stk.pop())
    popR,       // push(rstack.pop())
    
    and,
    or,
    not,
    
    eq,
    lt,
    gt,
    
    ld,
    str,
    
    add,
    sub
    
}
