import edu.princeton.cs.algs4.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BaseballElimination {
    private final Map<String, Integer> teamIndices;
    private final int[] w, l, r;
    private final int[][] g;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int n = in.readInt();
        w = new int[n];
        l = new int[n];
        r = new int[n];
        g = new int[n][n];
        teamIndices = new HashMap<>(n);
        for (int i = 0; i < n; i++) {
            String team = in.readString();
            teamIndices.put(team, i);
            w[i] = in.readInt();
            l[i] = in.readInt();
            r[i] = in.readInt();
            for (int j = 0; j < n; j++) {
                g[i][j] = in.readInt();
            }
        }
        in.close();
    }

    // number of teams
    public int numberOfTeams() {
        return teamIndices.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teamIndices.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        validateTeam(team);
        return w[teamIndices.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        validateTeam(team);
        return l[teamIndices.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validateTeam(team);
        return r[teamIndices.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        return g[teamIndices.get(team1)][teamIndices.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validateTeam(team);
        if (solveTrivial(team) != null) {
            return true;
        }

        FordFulkerson fordFulkerson = solveNontrivial(team);
        return gamesOfOthers(team) != fordFulkerson.value();
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        String trivialSolution = solveTrivial(team);
        if (trivialSolution != null) {
            return Arrays.asList(trivialSolution);
        }

        FordFulkerson fordFulkerson = solveNontrivial(team);
        if (gamesOfOthers(team) == fordFulkerson.value()) {
            return null;
        }

        Bag<String> certificate = new Bag<>();
        for (String other : teamIndices.keySet()) {
            if (team.equals(other)) continue;
            if (fordFulkerson.inCut(vertexInNetwork(team, other))) {
                certificate.add(other);
            }
        }
        return certificate;
    }

    private int gamesOfOthers(String team) {
        int teamIndex = teamIndices.get(team);
        int numberOfTeams = numberOfTeams();
        int sum = 0;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamIndex) continue;
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j == teamIndex) continue;
                sum += g[i][j];
            }
        }
        return sum;
    }

    private void validateTeam(String team) {
        if (!teamIndices.containsKey(team)) {
            throw new IllegalArgumentException("Invalid team - " + team);
        }
    }

    private String solveTrivial(String team) {
        int teamIndex = teamIndices.get(team);
        for (String other : teamIndices.keySet()) {
            if (team.equals(other)) continue;
            int otherIndex = teamIndices.get(other);
            int n = w[teamIndex] + r[teamIndex] - w[otherIndex];
            if (n < 0) {
                return other;
            }
        }
        return null;
    }

    private FordFulkerson solveNontrivial(String team) {
        int numberOfTeams = numberOfTeams();
        int teamIndex = teamIndices.get(team);
        int others = numberOfTeams - 1;
        int games = others * (others - 1) / 2;
        int vertices = 2 + others + games;
        FlowNetwork network = new FlowNetwork(vertices);
        int s = 0, t = vertices - 1, vGame = 0;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamIndex) continue;
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j == teamIndex) continue;
                network.addEdge(new FlowEdge(s, ++vGame, g[i][j]));

                int vFirst = (i > teamIndex ? i - 1 : i) + games + 1;
                network.addEdge(new FlowEdge(vGame, vFirst, Double.POSITIVE_INFINITY));

                int vSecond = (j > teamIndex ? j - 1 : j) + games + 1;
                network.addEdge(new FlowEdge(vGame, vSecond, Double.POSITIVE_INFINITY));
            }
        }
        assert games == vGame + 1;

        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamIndex) continue;
            int v = (i > teamIndex ? i - 1 : i) + games + 1;
            double capacity = w[teamIndex] + r[teamIndex] - w[i];
            network.addEdge(new FlowEdge(v, t, capacity));
        }

        return new FordFulkerson(network, s, t);
    }

    private int vertexInNetwork(String team, String other) {
        int numberOfTeams = numberOfTeams();
        int others = numberOfTeams - 1;
        int games = others * (others - 1) / 2;
        int teamIndex = teamIndices.get(team);
        int otherIndex = teamIndices.get(other);
        return (otherIndex > teamIndex ? otherIndex - 1 : otherIndex) + games + 1;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
