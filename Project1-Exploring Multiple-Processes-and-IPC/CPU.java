
import java.io.*;
import java.util.Scanner;
import java.lang.Runtime;
public class CPU {

    static int PC;
    static int SP;
    static int IR;
    static int AC;
    static int X;
    static int Y;
    static int instructions;

     static PrintWriter pw;
     static OutputStream os;
     static InputStream is;
     static Scanner sc;
     static Process proc;
     static boolean flag;
    static boolean kernalMode;
    static boolean interruptFlag;
    static int interruptTimer;
    static int timer = 0;
    
    
    public static void main(String[] args) {
//        System.out.println("PC: " + PC);
//        System.out.println("SP: " + SP);
//        System.out.println("IR: " + IR);
//        System.out.println("AC: " + AC);
//        System.out.println("X: " + X);
//        System.out.println("Y: " + Y);
//        System.out.println("TIMER: " + timer);
    	
    	
        if (args.length < 2) {
            System.out.println("must have more than 0 args, must include text file and the timer, Example: java CPU sample1.txt 2");
            System.exit(1);
        }
        AC = 0;
        PC = 0;
        SP = 1000;
        IR = 0;
        X = 0;
        Y = 0;
//        kernel = false;
        int timer = Integer.parseInt(args[1]);
        flag = true;

        int maxTimer = timer;
        try {
        	
//        	mem process to commun
            Runtime runtime = Runtime.getRuntime();
            proc = runtime.exec("Java Memory " + args[0]);
            is = proc.getInputStream();
            os = proc.getOutputStream();
            pw = new PrintWriter(os);
            sc = new Scanner(proc.getInputStream());

            while (flag) {
            	
            	timer++;
//            	System.out.println("Timer: " + timer);
//            	System.out.println("PC INSIDE FLAG: " + PC);
                fetch();
                exec();
//                System.out.println("PC INSIDE FLAG: " + PC);
                
                
                if(!interruptFlag) {
                	
                	if(maxTimer > timer && !kernalMode) {
                     	kernalMode = true;
                    	int temp = SP;
                    	SP = 2000;
                    	push(temp);
                		push(PC);
            			push(IR);
            			push(AC);
            			push(X);
            			push(Y);
                    	timer = 0;
                    	PC = 1000;
                	}

                }

            }
//          running = true;
          System.out.println("done");
          pw.flush();
          
          proc.waitFor();
          int exitVal = proc.exitValue();
          pw.close();
          os.close();
          sc.close();
          
          System.out.println("Process exited: " + exitVal);

        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Error executing,");
        }


    }
//    todo: Build a interuptt handler
    
//    
    public static void fetch() {
        IR = read(PC++);
    }


    public static void exec() {
        switch(IR) {

//          load val into the AC
            case 1:
                fetch();
                AC = IR;
                break;
//          Load value at the address into AC
            case 2:
                fetch();
                AC = read(IR);
//          load the val from address into AC
            case 3:
                fetch();
                AC = read(read(IR));
                break;
//          Load val at (address + Y ) into AC
            case 4: // LOAD VAL AT (ADDRESS + X)
                fetch();
                AC = read(IR + X);
                break;
            case 5: // LOAD VAL AT (ADDRESS + Y)
                fetch();
                AC = read(IR + Y);
            case 6:
                AC = read(SP + X);
                break;
            case 7: // Store val into AC into address
                fetch();
                write(IR, AC);
                break;
            case 8: // get random int from 1-100
                AC = (int)(Math.random() * 100+1);
            case 9:
                fetch();
                if(IR == 1) // IF 1M WRITE AC to screen
                    System.out.print(AC);
                if (IR == 2) { // if 2 , WRITE AC AS A CLEAR
                    System.out.print((char)AC);
                }
                break;
            case 10:
                AC += X; // Add val into Xfrom the AC
            case 11:
                AC += Y; // Add val into Yfrom the AC
                break;
            case 12:
                AC -= X; // Subtract val in X from AC
                break;
            case 13:
                AC -= Y; // Subtract the val in Y from AC
                break;
            case 14:
                AC = X; // COpy the val into the AC to X
                break;
            case 15:
                X = AC; // Copy the val in X to the AC
            case 16:
                Y = AC; // Copy val in y to the AC;
                break;
            case 17:
                AC = Y; // copy  the value in Y to the AC
                break;

            case 18:
                SP = AC;  // Copy the val in AC to the SP;
                break;
            case 19:
                AC = SP; // COPY THE VALUE IN SP TO AC
                break;
            case 20:
                fetch();
                PC = IR; // Jump to ADDRESS
                break;
            case 21:
                fetch();// JUMP TO ADD ONLY IF THE VALUE AC IS zero
                if(AC == 0)
                    PC = IR;
                break;
            case 22:
                fetch();// JUMP TO ADD ONLY IF THE VALUE AC IS NOT zero
                if(AC != 0)
                    PC = IR;
                break;
            case 23:
                fetch();
//                Push return address onto stack, Jump t the address
//                SP--;
//                write(SP,PC)
                push(AC);
                PC = IR;
                break;
            case 24:
                PC = pop(); // Pop return address from the stack
                break;
            case 25:
                X++; //increment the value X
                break;
            case 26:
//                decrement the value in X
                X--;
                break;
            case 27:
                push(AC); // Push AK onto stack
                break;
            case 28:
//                Pop from the stack into AC
                AC = pop();
                break;
            case 29:
//                Performs system call
                if(!kernalMode) {
                    kernalMode = true;
                    int temp = SP;
                    SP = 2000;
                    SP--;
                    write(SP,X);
                    SP--;
                    write( SP, Y);
                    write(1999, SP);
                    SP = 1999;
                    SP--;
                    write(SP,PC);
                    PC = 1500;
                }
                break;
            case 30:
//                Return from Sys Call
                PC = pop();
                SP = pop();
                kernalMode = false;
                break;
            case 50:
                System.exit(0);
            default:
                System.out.println("yo");


        }
    }

//    popping of stack
    public static int pop() {
        return read(SP++);
    }
//    pushing on stack
    public static void push(int data) {
        write(--SP, data);
    }

    public static void write(int address, int data) {
//        Check to make sure address isn't being violated
        if(address > 999 && !kernalMode) {
            System.out.println("Memory violation: accessing system address 1000 in user mode");
            System.exit(1);

        }

        pw.println("Write:" + address + ":" + data);
        pw.flush();
    }


    public static int read(int address) {
//        check for memory violation
        if(address > 999 && !kernalMode) {
            System.out.println("Memory violation: accessing system address 1000 in user mode");
            System.exit(1);
        }

        pw.println("Read:" + address);
        pw.flush();
        return Integer.parseInt(sc.nextLine());
    }


}
