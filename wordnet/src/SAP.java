import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

public class SAP {
    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        this.digraph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        int[] anl = ancestorAndLength(v, w);
        return anl == null ? -1 : anl[1];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        int[] anl = ancestorAndLength(v, w);
        return anl == null ? -1 : anl[0];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int[] anl = ancestorAndLength(v, w);
        return anl == null ? -1 : anl[1];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int[] anl = ancestorAndLength(v, w);
        return anl == null ? -1 : anl[0];
    }

    private int[] ancestorAndLength(int v, int w) {
        BreadthFirstDirectedPaths vp = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths wp = new BreadthFirstDirectedPaths(digraph, w);

        return sap(vp, wp);
    }

    private int[] ancestorAndLength(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths vp = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths wp = new BreadthFirstDirectedPaths(digraph, w);

        return sap(vp, wp);
    }

    private int[] sap(BreadthFirstDirectedPaths path1, BreadthFirstDirectedPaths path2) {
        int minDist = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (path1.hasPathTo(i) && path2.hasPathTo(i)) {
                int dist = path1.distTo(i) + path2.distTo(i);
                if (minDist > dist) {
                    minDist = dist;
                    ancestor = i;
                }
            }
        }

        return ancestor == -1 ? null : new int[] { ancestor, minDist };
    }

    // do unit testing of this class
    public static void main(String[] args) {
//        In in = new In(args[0]);
//        Digraph G = new Digraph(in);
//        SAP sap = new SAP(G);
//        while (!StdIn.isEmpty()) {
//            int v = StdIn.readInt();
//            int w = StdIn.readInt();
//            int length   = sap.length(v, w);
//            int ancestor = sap.ancestor(v, w);
//            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
//        }
    }
}
