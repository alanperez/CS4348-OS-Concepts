# CS4348-OS-Concepts

An introduction to fundamental concepts in operating systems: their design, implementation, and usage. Topics include process management, main memory management, virtual memory, I/O and device drivers, file systems, secondary storage management, and an introduction to critical sections and deadlocks

# Project 1 Overview - Exploring Multiple Processes and IPC

The project will simulate a simple computer system consisting of a CPU and Memory.
The CPU and Memory will be simulated by separate processes that communicate.
Memory will contain one program that the CPU will execute and then the simulation will end.

### Objectives

1)	Learn how multiple processes can communicate and cooperate.
2)	Understand low-level concepts important to an operating system. 
    -	Processor interaction with main memory.
    -	Processor instruction behavior.
    -	Role of registers.
    -	Stack processing.
    -	Procedure calls.	f.	System calls.
    -	Interrupt handling.
    -	Memory protection.
    -	I/O.
    -	Virtualization/emulation


### Problem Details

CPU
   It will have these registers:  PC, SP, IR, AC, X, Y.
   It will support the instructions shown on the next page of this document.
   It will run the user program at address 0.
   Instructions are fetched into the IR from memory.  The operand can be fetched into a local variable.
   Each instruction should be executed before the next instruction is fetched.
   The user stack resides at the end of user memory and grows down toward address 0.
   The system stack resides at the end of system memory and grows down toward address 0.
   There is no hardware enforcement of stack size.
   The program ends when the End instruction is executed.  The 2 processes should end at that time.
   The user program cannot access system memory (exits with error message).
   
Memory
   It will consist of 2000 integer entries, 0-999 for the user program, 1000-1999 for system code.
   It will support two operations:
       read(address) -  returns the value at the address
       write(address, data) - writes the data to the address
   Memory will read an input file containing a program into its array, before any CPU fetching begins.
   Note that the memory is simply storage; it has no real logic beyond reading and writing.
 
   Timer
     A timer will interrupt the processor after every X instructions, where X is a command-line parameter.
     The timer is always counting, whether in user mode or kernel mode.

   Interrupt processing
     There are two forms of interrupts:  the timer and a system call using the int instruction.
     In both cases the CPU should enter kernel mode.
     The stack pointer should be switched to the system stack.
     The SP and PC registers (and only these registers) should be saved on the system stack by the CPU.
     The handler may save additional registers. 
     A timer interrupt should cause execution at address 1000.
     The int instruction should cause execution at address 1500.
     The iret instruction returns from an interrupt.
     Interrupts should be disabled during interrupt processing to avoid nested execution.
     To make it easy, do not allow interrupts during system calls or vice versa.

 
  Instruction set

    1 = Load value                    
    2 = Load addr
    3 = LoadInd addr   
   
    4 = LoadIdxX addr
   
    5 = LoadIdxY addr
    6 = LoadSpX
    7 = Store addr
    8 = Get 
    9 = Put port

     10 = AddX
     11 = AddY
     12 = SubX
     13 = SubY
     14 = CopyToX
     15 = CopyFromX
     16 = CopyToY
     17 = CopyFromY
     18 = CopyToSp
     19 = CopyFromSp   
     20 = Jump addr
     21 = JumpIfEqual addr
     22 = JumpIfNotEqual addr
     23 = Call addr
     24 = Ret 
     25 = IncX 
     26 = DecX 
     27 = Push
     28 = Pop
     29 = Int 
     30 = IRet
     50 = End	Load the value into the AC
Load the value at the address into the AC
Load the value from the address found in the given address into the AC
(for example, if LoadInd 500, and 500 contains 100, then load from 100).
Load the value at (address+X) into the AC
(for example, if LoadIdxX 500, and X contains 10, then load from 510).
Load the value at (address+Y) into the AC
Load from (Sp+X) into the AC (if SP is 990, and X is 1, load from 991).
Store the value in the AC into the address
Gets a random int from 1 to 100 into the AC
If port=1, writes AC as an int to the screen
If port=2, writes AC as a char to the screen
Add the value in X to the AC
Add the value in Y to the AC
Subtract the value in X from the AC
Subtract the value in Y from the AC
Copy the value in the AC to X
Copy the value in X to the AC
Copy the value in the AC to Y
Copy the value in Y to the AC
Copy the value in AC to the SP
Copy the value in SP to the AC 
Jump to the address
Jump to the address only if the value in the AC is zero
Jump to the address only if the value in the AC is not zero
Push return address onto stack, jump to the address
Pop return address from the stack, jump to the address
Increment the value in X
Decrement the value in X
Push AC onto stack
Pop from stack into AC
Perform system call
Return from system call
End execution



# Project 2 Overview - Threads and Semaphores

## Project Description


Language/Platform

This project must target a Unix platform and execute properly on our cs1 Linux server.
The project must be written in C, C++, or Java.
If using C or C++, you must use POSIX pthreads and semaphores (no mutexes, locks, etc.)
If using Java, you must use Java Threads and Java Semaphores (java.util.concurrent.Semaphore).  
You should not use the “synchronized” keyword in Java.
You should not use any Java classes that have built-in mutual exclusion.
Any mechanisms for thread coordination other than the semaphore are not allowed.


Post Office Simulation

A Post Office is simulated by using threads to model customer and employee behavior.  

This project is similar to the “barbershop” example in the textbook.  The following rules apply:

Customer:
1)	50 customers visit the Post Office (1 thread per customer up to 50), all created initially.
2)	Only 10 customers can be inside the Post Office at a time.
3)	Each customer upon creation is randomly assigned one of the following tasks:
a)	buy stamps
b)	mail a letter
c)	mail a package
4)	Times for each task are defined in the task table.


Postal Worker:
1)	3 created initially, one thread each.
2)	Serves next customer in line.
3)	Service time varies depending on customer task.


Scales:
1)	Used by the postal worker when mailing a package.
2)	There is only one, which can only be used one at a time. 
3)	The scales are not a thread.  They are just a resource the postal worker threads use. 


Other rules:
1)	A thread should sleep 1 second in the program for each 60 seconds listed in the table.  
2)	All mutual exclusion and coordination must be achieved with semaphores.  
3)	A thread may not use sleeping as a means of coordination.  
4)	Busy waiting (polling) is not allowed. 
5)	Mutual exclusion should be kept to a minimum to allow the most concurrency.
6)	Each thread should print when it is created and when it is joined.
7)	Each thread should only print its own activities.  The customer threads prints customer actions and the postal worker threads prints postal worker actions.  
8)	Your output must include the same information and the same set of steps as the sample output.



# Project 3 Overview - Buddy System Memory Management


## Problem Overview

This project will simulate the buddy system of performing memory allocations.  

The project will allow the user to input a set of memory requests and releases.  It will output a representation of memory showing occupied and free spaces.


Design

You may design your own implementation approach, but here are a few constraints.  

The memory space is 1 megabyte (1024*1024 bytes).  Your program should read in a list of requests from a space-delimited text file named input.txt.  The format of the text file should have one request per line.  A request can either be a request to allocate memory or to release memory.  For an allocation request, the format is “Request size” where size is in kilobytes.  The request should be assigned a name alphabetically (first request is A).  For a release, the format is “Release name” where name is the alphabetically assigned name of a prior request.  

You can assume the input file format is valid.  If a request cannot be satisfied, the program should exit with an error.  The program should support requests as small as 64K.

Your output should be a graphical representation of memory as shown in the output section.  This can be output as text, or you can draw it graphically using a graphics package such as JavaFX.  

Your program should be able to reproduce the example shown in the slides as well as any similar set of requests.

 
Sample Input

Request 100K
Request 240K
Request 64K
Release C
Release A
Release B


Sample Output

Below is sample text-based output for the sample input.  

--------------
|     1024K  |
--------------

Request 100K
--------------------------------------------------
| A    128K |      128K |      256K |      512K  |
--------------------------------------------------

Request 240K
--------------------------------------------------
| A    128K |      128K | B    256K |      512K  |
--------------------------------------------------

Request 64K
--------------------------------------------------------------
| A    128K | C     64K |       64K | B    256K |      512K  |
--------------------------------------------------------------

Release C
--------------------------------------------------
| A    128K |      128K | B    256K |      512K  |
--------------------------------------------------

Release A
--------------------------------------
|      256K | B    256K |      512K  |
--------------------------------------

Release B
--------------
|     1024K  |
--------------

