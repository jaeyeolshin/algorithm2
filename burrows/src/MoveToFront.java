import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] mtf = ascii();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(update(mtf, c));
        }
        BinaryStdOut.close();
    }

    private static char update(char[] mtf, char c) {
        int p = 0;
        for (; p < mtf.length; p++) {
            if (mtf[p] == c)
                break;
        }

        for (int i = p; i >= 1; i--) {
            mtf[i] = mtf[i - 1];
        }
        mtf[0] = c;

        return (char) p;
    }

    private static char[] ascii() {
        char[] ascii = new char[256];
        for (int i = 0; i < ascii.length; i++)
            ascii[i] = (char) i;

        return ascii;
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] mtf = ascii();
        while (!BinaryStdIn.isEmpty()) {
            int i = BinaryStdIn.readInt(8);
            char c = mtf[i];
            BinaryStdOut.write(c);
            update(mtf, c);
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}