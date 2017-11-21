import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new IllegalArgumentException();
        }
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int max = 0, maxSum = Integer.MIN_VALUE;
        for (int i = 0; i < nouns.length; i++) {
            String noun = nouns[i];
            int sum = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (i == j) continue;
                sum += wordnet.distance(noun, nouns[j]);
            }

            if (maxSum < sum) {
                maxSum = sum;
                max = i;
            }
        }

        return nouns[max];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}