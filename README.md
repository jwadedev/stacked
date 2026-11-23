# stacked
This is a stack machine which executes programs in it's own machine code.  There is also an assembler, to create the machine code, and a small standard library.

This project happened because I wanted to understand how the combinational logic I'd learned in school, could result in a computer that runs all the programs we write.  I understood programming language and combinational logic, but was missing the middle of how it worked.

## The VM

I built the VM around an execution stack, because any expression can be calculated on a stack. This choice simplified the machine, but means all calculations need to be in postfix notation.  So a+b+c would be a,b,+,c,+ in the machine code.  

Every piece of data in the machine is just an integer. Occasionally those are treated as numbers, but mostly the machine sees them as instructions.  The whole computer's memory is an integer array. So the integer machine code is just part of that array.  

Throughout the project I used Test Driven Development which was essential. Writing tests first for each feature ensured that the new piece worked, and didn't break any or the rest.  As the complexity grew the thorough test set protected against break other features..

The first functional stack machine is  [VM1](https://github.com/jwadedev/stacked/blob/main/src/vm/Vm1.java).  
To run it do the following...
1. Construct it:  Vm = new Vm1( memory, exec_stack, return_stack)
2. Vm.addProg( int[] prog) // This loads the program into the memory.
3. Vm.run()

Run just causes the system to loop through this cycle.. You can see it here [Run method](https://github.com/jwadedev/stacked/blob/main/src/vm/Vm1.java#L96)
1. get instruction()
2. incrementPc()
3. executeInstruction()


Here is what makes different VMs unique...
1. [Vm1](https://github.com/jwadedev/stacked/blob/main/src/vm/Vm1.java) : Initial and simplest implementation.
2. [ModVm1](https://github.com/jwadedev/stacked/blob/main/src/vm/ModVm1.java) : Functionally same but logic pulled out into separate files.
3. [ModVm2](https://github.com/jwadedev/stacked/blob/main/src/vm/ModVm2.java) : Changed stacks to multi parameter, for speed. Not much improvement.
4. [MicroVm1](https://github.com/jwadedev/stacked/blob/main/src/vm/MicroVm.java) : Broke cpu operations into Micro Ops, for more realistic HW copy.
5. [MicroVm2](https://github.com/jwadedev/stacked/blob/main/src/vm/MicroVm2.java) : Micro Ops in classes ALU ops, Bus ops, Cmd ops. Decode() returns MicroOps for the operation, and execute then does those in series.

## The Assembler

Once the first VM was operational, I discovered the difficulty of writing programs as an array of integers in postfix notation.

I wrote a simpler assembler for the machine language which my machine executed. Then I got very into improving it, and this is much better then the original.
[The Assember](https://github.com/jwadedev/stacked/blob/main/src/assembler/Asm.java) is the final product.  This takes in a string of text, and creates an executable program.  The assembler support creating 'if,else,endif' blocks and 'while wend' blocks. It will handle the jump addressing.  It also supports defining jump targets.  A name with a ':' on the front acts as a location identifier for jump command.  A ';' acts as a return statement, popping of the return stack. A number in the code is to be pushed onto the execution stack, so in machine code it will be a Push followed by the number. Finally there are many stack specific operations, many inspired by the Forth language. Also, anything in (parenthesis) is treated as a comment and has no impact on the program.

Basic Stack machine ops.
  * Noop: No Op
  * Dup: Duplicate top of stack
  * Swp: Swap top to items on stack  a,b --> b,a
  * Over: Push copy of 2nd item on stack over top item on stack  a,b --> b,a,b
  * Rot: Rotate top 3 items on stack.  a,b,c --> c,a,b
  * Jmp: Set the PC (program counter) to the int on top of the stack.
  * Jmpz: If the 2nd value on the stack is 0, jump set PC to the value on top of the stack.
  * Gsb: Push the current PC onto the return stack.  Then set the PC to top value on execution stack.
  * Ret: Set the PC to the value on top of the return stack.
  * And: Boolean AND the top two values on the execution stack and store to stack.
  * Or: Boolean OR the top two values on the execution stack and store to stack.
  * Not: Boolean NOT the op value on the exec stack and store to stack.
  * Eq: Store True on execution stack if top to items on stack match each other.
  * Lt: Store True on execution stack if 2nd item on exec stack is less then the 1st.
  * Gt: Store True on execution stack if 2nd item on exec stack is greater then the 1st.
  * Add: Replace the top to items on exec stack with their sum
  * Sub: Replace the top to items on exec stack with their difference
  * Push: Push whatever value follows this onto the exec stack
  * Drp: Drop the top item on the execution stack
  * Prnt: Print to terminal the top item on the exec stack.
  * Ld: Use the top exec value as memory address, replace it with the value in that location in memory
  * Sto: Use the top exec value as memory address. Write the value second on stack into the specified memory address.
  * Comp: Do a Bitwise not on top of stack
  * Lshft: Bitwise shift bits left on top of exec stack
  * Rshft: Bitwise shift bits right on top of exec stack
  * Emit: Print to terminal top integer as a charecter
  * Halt: Stop the program execution
  * Debug: Print out the entire execution stack for debugging.


## The Standard Library

There is a standard library, I'm looking for it to add to the repo..
You can see from the test file what the methods in the standard libary look like..
 * [Fibinacci](https://github.com/jwadedev/stacked/blob/main/test/assembler/AsmToVmTest.java#L152)
 * [Bubble Sort](https://github.com/jwadedev/stacked/blob/main/test/assembler/AsmToVmTest.java#L294)
 * [Multiply](https://github.com/jwadedev/stacked/blob/main/test/assembler/AsmToVmTest.java#L336)
 * 

