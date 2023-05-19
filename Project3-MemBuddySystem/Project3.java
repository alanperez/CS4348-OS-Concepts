import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// General overview of what the buddy system does *geeks4geeks*:
//    The memory is divided into fixed-size blocks that are a power of 2 in size (such as 2, 4, 8, 16, 32, etc. bytes).
//    Each block is labeled with its size and a unique identifier, such as a binary number.
//    Initially, all the memory blocks are free and are linked together in a binary tree structure, with each node representing a block and the tree’s leaves representing the smallest available blocks.
//    When a process requests memory, the system finds the smallest available block that can accommodate the requested size. If the block is larger than the requested size, the system splits the block into two equal-sized “buddy” blocks.
//    The system marks one of the buddy blocks as allocated and adds it to the process’s memory allocation table, while the other buddy block is returned to the free memory pool and linked back into the binary tree structure.
//    When a process releases memory, the system marks the corresponding block as free and looks for its buddy block. If the buddy block is also free, the system merges the two blocks into a larger block and links it back into the binary tree structure.
public class Project3 {
//    min alloc size
    public static final int MIN_ALLOCATION_SIZE = 64 * 1024;
//
    private BinaryTree tree;

//    constructer, init BinaryTree with size
    public Project3(int size) {
        tree = new BinaryTree(size);
    }

//    Function for requesting memory, takes size and name for input
    public boolean request(int size, char name) {
//        if req size is less than min size we set it to min
        if (size < MIN_ALLOCATION_SIZE) {
            size = MIN_ALLOCATION_SIZE;
        }
        return tree.request(size, name);
    }

//    release memory
    public void release(char name) {
        tree.release(name);
    }

//    prints memory block of tree
    public void printMemoryBlock() {
        tree.printMemoryBlock();
    }

    public static void main(String[] args) throws IOException {
//      init to 1MB of mem
        Project3 buddySystemMemoryManagement = new Project3(1024 * 1024);
        buddySystemMemoryManagement.printMemoryBlock();
//        read our input file and store its contents
        List<String> items = Files.readAllLines(Paths.get("input.txt"));
        char nextRequestName = 'A';
//        loop through each line
        for (String item : items) {
            String[] itemArr = item.split(" ");
            if (itemArr[0].equalsIgnoreCase("Request")) {
                int size = Integer.parseInt(itemArr[1].substring(0, itemArr[1].length() - 1)) * 1024;
                System.out.println("Request " + itemArr[1]);
                boolean success = buddySystemMemoryManagement.request(size, nextRequestName);
                if (success) {
                    nextRequestName++;
                } else {
                    System.out.println("Error: can't execute request.");
                    System.exit(1);
                }
            } else if (itemArr[0].equalsIgnoreCase("Release")) {
                char name = itemArr[1].charAt(0);
                System.out.println("Release " + itemArr[1]);
                buddySystemMemoryManagement.release(name);
            }
            buddySystemMemoryManagement.printMemoryBlock();
        }
    }


    public class BinaryTree {

        private Node root;
        private int size;

        public BinaryTree(int size) {
            this.size = size;
            this.root = new Node(0, size);
        }

        public boolean request(int size, char name) {
            return request(root, size, name);
        }

//        recursive function for requesting memory
        private boolean request(Node node, int size, char name) {
            if (!node.isFree()) {
                return false;
            }

            int roundedSize = roundPow2(size);

            if (node.getSize() == roundedSize) {
                node.setName(name);
                return true;
            }

            if (node.getSize() >= roundedSize && node.getSize() > Project3.MIN_ALLOCATION_SIZE) {
                int newSize = node.getSize() / 2;
                if (node.getLeft() == null) {
                    node.setLeft(new Node(node.getStart(), newSize));
                }
                if (node.getRight() == null) {
                    node.setRight(new Node(node.getStart() + newSize, newSize));
                }

                if (request(node.getLeft(), size, name)) {
                    return true;
                }

                if (request(node.getRight(), size, name)) {
                    return true;
                }

//                if no alloc split and return false
                if (node.getLeft().isFree() && node.getRight().isFree()) {
                    node.setLeft(null);
                    node.setRight(null);
                }
            }

            return false;
        }
        private int roundPow2(int size) {
            int power = (int) Math.ceil(Math.log(size) / Math.log(2));
            return (int) Math.pow(2, power);
        }

        public boolean release(char name) {
            return release(root, name);
        }

        private boolean release(Node node, char name) {
            if (node == null) {
                return false;
            }

            if (node.getName() == name) {
                node.setName(Node.FREE);
                return true;
            }

            if (release(node.getLeft(), name) || release(node.getRight(), name)) {
                mergeFreeMemory(node);
                return true;
            }

            return false;
        }

        private void mergeFreeMemory(Node node) {
            if (node == null || node.getLeft() == null || node.getRight() == null) {
                return;
            }

            if (node.getLeft().isFree() && node.getRight().isFree()) {
                if (node.getLeft().isLeafNode() && node.getRight().isLeafNode()) {
                    node.setLeft(null);
                    node.setRight(null);
                } else {
                    mergeFreeMemory(node.getLeft());
                    mergeFreeMemory(node.getRight());
                }
            }
        }
        public int maxDepth() {
            return maxDepth(root);
        }

        /* Compute the "maxDepth" of a tree -- the number of
    nodes along the longest path from the root node
    down to the farthest leaf node.*/
//        Geeks for Geeks reference
//        int maxDepth(Node node)
//        {
//            if (node == null)
//                return 0;
//            else {
//                /* compute the depth of each subtree */
//                int lDepth = maxDepth(node.left);
//                int rDepth = maxDepth(node.right);
//
//                /* use the larger one */
//                if (lDepth > rDepth)
//                    return (lDepth + 1);
//                else
//                    return (rDepth + 1);
//            }
//        }
        private int maxDepth(Node node) {
            if (node == null) {
                return 0;
            } else {
                int leftDepth = maxDepth(node.getLeft());
                int rightDepth = maxDepth(node.getRight());

                return Math.max(leftDepth, rightDepth) + 1;
            }
        }
        public void printMemoryBlock() {
            List<String> lines = new ArrayList<>();
            printMemoryBlock(root, "", lines);
            String result = String.join("", lines);
            int dashesLength = result.length();
            String dashes = String.join("", Collections.nCopies(dashesLength, "-"));

            System.out.println(dashes);
            System.out.println(result);
            System.out.println(dashes);
        }
        private void printMemoryBlock(Node node, String prefix, List<String> lines) {
            if (node == null) {
                return;
            }

            if (node.getLeft() == null && node.getRight() == null) {
                if (node.isFree()) {
                    lines.add("| " + (node.getSize() / 1024) + "K       |");
                } else {
                    lines.add("| " + node.getName() + " " + (node.getSize() / 1024) + "K ");
                }
            }

            printMemoryBlock(node.getLeft(), prefix + " ", lines);
            printMemoryBlock(node.getRight(), prefix + " ", lines);
        }
    }


    public class Node {

        public static final char FREE = '-';
        private int start;
        private int size;
        private char name;
        private Node left;
        private Node right;

        public Node(int start, int size) {
            this.start = start;
            this.size = size;
            this.name = FREE;
            this.left = null;
            this.right = null;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public char getName() {
            return name;
        }

        public void setName(char name) {
            this.name = name;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public boolean isFree() {
            return name == FREE;
        }

        public boolean isLeafNode() {
            return left == null && right == null;
        }
    }
}
