import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("null argument");

        char[] input = s.toCharArray();
        index = new int[input.length];
        for (int i = 0; i < index.length; i++) {
            index[i] = i;
        }

        sort(index, input);
    }

    private void merge(int[] a, int[] aux, int lo, int mid, int hi, char[] input) {
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)                     a[k] = aux[j++];
            else if (j > hi)                      a[k] = aux[i++];
            else if (less(aux[j], aux[i], input)) a[k] = aux[j++];
            else                                  a[k] = aux[i++];
        }
    }

    private void sort(int[] a, int[] aux, int lo, int hi, char[] input) {
        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid, input);
        sort(a, aux, mid + 1, hi, input);
        merge(a, aux, lo, mid, hi, input);
    }

    private void sort(int[] a, char[] input) {
        int[] aux = new int[a.length];
        sort(a, aux, 0, a.length-1, input);
    }

    private boolean less(int v, int w, char[] input) {
        int n = input.length;
        for (int i = 0; i < n; i++) {
            int result = Character.compare(input[v], input[w]);
            if (result != 0) return result < 0;
            else {
                v = (v + 1) % n;
                w = (w + 1) % n;
            }
        }
        return false;
    }

    // length of s
    public int length() {
        return index.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= index.length)
            throw new IllegalArgumentException("out of range");

        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < csa.length(); i++)
            StdOut.println("index[" + i + "] = " + csa.index(i));
    }
}