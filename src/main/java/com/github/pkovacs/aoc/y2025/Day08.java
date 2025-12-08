package com.github.pkovacs.aoc.y2025;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.github.pkovacs.util.Vector;

public class Day08 extends AbstractDay {

    public static void main(String[] args) {
        var lines = readLines(getInputPath());

        // Calculate and sort distances
        var positions = lines.stream().map(Day08::parseVector).toList();
        var connections = collectSortedConnections(positions);

        // Run Kruskal's algorithm
        var circuits = new UnionFind(positions.size());
        int cnt = 0;
        long ans1 = 0, ans2 = 0;
        int circuitCount = positions.size();
        for (var c : connections) {
            if (circuits.find(c.i) != circuits.find(c.j)) {
                circuits.merge(c.i, c.j);
                if (--circuitCount == 1) {
                    ans2 = positions.get(c.i).x * positions.get(c.j).x;
                    break;
                }
            }
            if (++cnt == 1000) {
                ans1 = circuits.getSetSizes().stream().limit(3).mapToLong(i -> i).reduce(1L, (a, b) -> a * b);
            }
        }

        System.out.println("Part 1: " + ans1);
        System.out.println("Part 2: " + ans2);
    }

    private static Vector parseVector(String s) {
        var parts = s.split(",");
        return new Vector(parseLong(parts[0]), parseLong(parts[1]), parseLong(parts[2]));
    }

    private static List<Connection> collectSortedConnections(List<Vector> positions) {
        var connections = new ArrayList<Connection>();
        for (int i = 0; i < positions.size(); i++) {
            for (int j = i + 1; j < positions.size(); j++) {
                connections.add(new Connection(i, j, positions.get(i).distSq(positions.get(j))));
            }
        }
        connections.sort(Comparator.comparing(Connection::dist));
        return connections;
    }

    private record Connection(int i, int j, long dist) {
    }

    /**
     * A simple implementation of the union-find data structure.
     * <p>
     * It's efficient enough for this puzzle and easier to understand than the common implementation using a
     * <a href="https://en.wikipedia.org/wiki/Disjoint-set_data_structure">disjoint-set forest</a>.
     * As this puzzle involves a full graph, the bottleneck is to calculate and sort the O(n^2) distances for
     * Kruskal's algorithm, and much more find() operations are performed than merge() operations, so a disjoint-set
     * forest wouldn't be faster.
     */
    private static class UnionFind {

        final int[] setId;
        final List<List<Integer>> sets = new ArrayList<>();

        UnionFind(int n) {
            setId = new int[n];
            for (int i = 0; i < n; i++) {
                setId[i] = i;
                sets.add(new ArrayList<>(List.of(i)));
            }
        }

        int find(int i) {
            return setId[i];
        }

        void merge(int i, int j) {
            if (sets.get(setId[i]).size() < sets.get(setId[j]).size()) {
                mergeSets(setId[i], setId[j]);
            } else {
                mergeSets(setId[j], setId[i]);
            }
        }

        void mergeSets(int from, int to) {
            sets.get(from).forEach(i -> setId[i] = to);
            sets.get(to).addAll(sets.get(from));
            sets.get(from).clear();
        }

        List<Integer> getSetSizes() {
            return sets.stream().map(List::size).filter(s -> s > 0).sorted().toList().reversed();
        }

    }

}
