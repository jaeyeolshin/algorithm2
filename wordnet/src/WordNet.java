import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.DepthFirstDirectedPaths;
import edu.princeton.cs.algs4.DepthFirstOrder;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.Map;

public class WordNet {
    private final Map<String, Bag<Integer>> nounMap = new HashMap<>();
    private final Map<Integer, String> synMap = new HashMap<>();
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }

        In in = new In(synsets);
        int vcnt = 0;
        while (in.hasNextLine()) {
            vcnt++;
            String line = in.readLine();
            String[] splited = line.split(",");
            int num = Integer.parseInt(splited[0]);
            String synset = splited[1];
            synMap.put(num, synset);
            String[] nouns = synset.split(" ");
            for (String noun : nouns) {
                nounMap.computeIfAbsent(noun, k -> new Bag<>()).add(num);
            }
        }
        in.close();

        in = new In(hypernyms);
        Digraph digraph = new Digraph(vcnt);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] splited = line.split(",");
            int child = Integer.parseInt(splited[0]);
            for (int i = 1; i < splited.length; i++) {
                digraph.addEdge(child, Integer.parseInt(splited[i]));
            }
        }
        in.close();

        DirectedCycle cycle = new DirectedCycle(digraph);
        if (cycle.hasCycle()) {
            throw new IllegalArgumentException();
        }
        DepthFirstOrder dfo = new DepthFirstOrder(digraph);
        int root = dfo.post().iterator().next();
        if (digraph.outdegree(root) != 0) {
            throw new IllegalArgumentException();
        }

        Digraph reversed = digraph.reverse();
        DepthFirstDirectedPaths dfs = new DepthFirstDirectedPaths(reversed, root);
        for (int i = 0; i < reversed.V(); i++) {
            if (!dfs.hasPathTo(i)) {
                throw new IllegalArgumentException();
            }
        }

        this.sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return nounMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        return sap.length(nounMap.get(nounA), nounMap.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        int ancestor = sap.ancestor(nounMap.get(nounA), nounMap.get(nounB));
        return synMap.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}