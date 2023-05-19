import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BinaryTree {

    private Node root;
    private int size;

    public BinaryTree(int size) {
        this.size = size;
        this.root = new Node(0, size);
    }

    public boolean requestMemory(int size, char name) {
        return requestMemory(root, size, name);
    }

    private boolean requestMemory(Node node, int size, char name) {
        if (!node.isFree()) {
            return false;
        }

        int roundedSize = roundUpToNearestPowerOfTwo(size);

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

            if (requestMemory(node.getLeft(), size, name)) {
                return true;
            }

            if (requestMemory(node.getRight(), size, name)) {
                return true;
            }

            // If no allocation is made, revert the split and return false
            if (node.getLeft().isFree() && node.getRight().isFree()) {
                node.setLeft(null);
                node.setRight(null);
            }
        }

        return false;
    }
    private int roundUpToNearestPowerOfTwo(int size) {
        int power = (int) Math.ceil(Math.log(size) / Math.log(2));
        return (int) Math.pow(2, power);
    }

    public boolean releaseMemory(char name) {
        return releaseMemory(root, name);
    }

    private boolean releaseMemory(Node node, char name) {
        if (node == null) {
            return false;
        }

        if (node.getName() == name) {
            node.setName(Node.FREE);
            return true;
        }

        if (releaseMemory(node.getLeft(), name) || releaseMemory(node.getRight(), name)) {
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

    private int maxDepth(Node node) {
        if (node == null) {
            return 0;
        } else {
            int leftDepth = maxDepth(node.getLeft());
            int rightDepth = maxDepth(node.getRight());

            return Math.max(leftDepth, rightDepth) + 1;
        }
    }
    public void printMemory() {
        List<String> lines = new ArrayList<>();
        printMemory(root, "", lines);
        String result = String.join("", lines);
        int dashesLength = result.length();
        String dashes = String.join("", Collections.nCopies(dashesLength, "-"));

        System.out.println(dashes);
        System.out.println(result);
        System.out.println(dashes);
    }
    private void printMemory(Node node, String prefix, List<String> lines) {
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

        printMemory(node.getLeft(), prefix + " ", lines);
        printMemory(node.getRight(), prefix + " ", lines);
    }
}
