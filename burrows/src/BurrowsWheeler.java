import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        final String s = BinaryStdIn.readString();
        final CircularSuffixArray csa = new CircularSuffixArray(s);
        final int n = csa.length();
        int first = 0;
        for (; first < n; first++) {
            if (csa.index(first) == 0)
                break;
        }
        BinaryStdOut.write(first);

        for (int i = 0; i < n; i++) {
            int lastIndex = (csa.index(i) + n - 1) % n;
            BinaryStdOut.write(s.charAt(lastIndex));
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        final int first = BinaryStdIn.readInt();
        final char[] input = BinaryStdIn.readString().toCharArray();
        final char[] sorted = new char[input.length];
        final int[] count = radixSortCount(input);
        final int[] next = new int[input.length];

        for (int i = 0; i < input.length; i++) {
            sorted[count[input[i]]] = input[i];
            int index = count[input[i]]++;
            next[index] = i;
        }

        for (int i = 0, nextIndex = first; i < input.length; i++) {
            BinaryStdOut.write(sorted[nextIndex]);
            nextIndex = next[nextIndex];
        }
        BinaryStdOut.close();
    }

    private static int[] radixSortCount(char[] cs) {
        final int r = 256;
        int[] count = new int[r + 1];
        for (int i = 0; i < cs.length; i++) {
            count[cs[i] + 1]++;
        }

        for (int i = 0; i < r; i++) {
            count[i + 1] += count[i];
        }
        return count;
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}