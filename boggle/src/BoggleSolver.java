import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {
    private final AlphabetTrieST<Object> trie = new AlphabetTrieST<>();

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        Object val = new Object();
        for (String word : dictionary) {
            if (word.length() < 3) continue;
            trie.put(word, val);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int rows = board.rows();
        int cols = board.cols();
        boolean[][] marked = new boolean[rows][cols];
        Set<String> words = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                search(board, words, marked, "", i, j);
            }
        }
        return words;
    }

    private void search(BoggleBoard board, Set<String> words, boolean[][] marked, String prefix, int row, int col) {
        if (row < 0 || row >= board.rows() || col < 0 || col >= board.cols() || marked[row][col]) return;

        String candidate = prefix + board.getLetter(row, col);
        if (candidate.endsWith("Q")) {
            candidate += "U";
        }
        if (!trie.containsPrefix(candidate)) return;
        if (trie.contains(candidate)) {
            words.add(candidate);
        }

        marked[row][col] = true;
        search(board, words, marked, candidate, row - 1, col);
        search(board, words, marked, candidate, row, col - 1);
        search(board, words, marked, candidate, row - 1, col - 1);
        search(board, words, marked, candidate, row + 1, col);
        search(board, words, marked, candidate, row, col + 1);
        search(board, words, marked, candidate, row + 1, col + 1);
        search(board, words, marked, candidate, row - 1, col + 1);
        search(board, words, marked, candidate, row + 1, col - 1);
        marked[row][col] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!trie.contains(word)) return 0;
        int len = word.length();
        if (len >= 8)      return 11;
        else if (len == 7) return 5;
        else if (len == 6) return 3;
        else if (len == 5) return 2;
        else               return 1;
    }


    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}