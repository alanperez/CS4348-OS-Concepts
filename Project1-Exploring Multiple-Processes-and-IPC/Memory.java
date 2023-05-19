import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory {

    static int[] memory;
    static String fileName;

    public static void main(String[] args) throws FileNotFoundException {
//       get file name from cli
        fileName = args[0];
//      create File Obj
        File file = new File(fileName);
//      create Scanner
        Scanner scanner = new Scanner(System.in);

        readInFIle();

        while(scanner.hasNextLine()) {
//            split the line into tokens in order to extract what we need
            String line = scanner.nextLine();
//          Splitting line, format was "Read:" + address + ":" "data"
            String[] tokens = line.split(":");

//          if first string is "Read", we read the address

//          if the first one is "Write" we write to memory

            if(tokens[0].equals("Write")) {
                int address = Integer.parseInt(tokens[1]);
                int data = Integer.parseInt(tokens[2]);
                write(address, data);
            }
        }
        scanner.close();

    }

    public static void readInFIle() throws FileNotFoundException {
        memory = new int [2000];
        int address = 0;
        File file = new File (fileName);
        Scanner sc = new Scanner(file);

//      process each line
        while(sc.hasNextLine()) {
//          remove whitespace
            String line = sc.nextLine().trim();

            if(line.isEmpty() || line.startsWith("/")) {
                continue;
            }

//            split tokens into multiple substrings, if no tokens -> SKIP
            String[] tokens = line.split("\\s");
            if(tokens.length == 0) {
                continue;
            }
//          extract first token by index
            String token = tokens[0];
//          if it starts with '.', set address
            if(token.startsWith(".")) {
                address = Integer.parseInt(token.substring(1));
                continue;
            }

            int val = Integer.parseInt(token);
            memory[address++] = val;
        }
        sc.close();
    }

//    return addr from memory method
    public static int read(int address) {
        return memory[address];
    }

    public static void write(int address, int data) {
        memory[address] = data;
    }
}
