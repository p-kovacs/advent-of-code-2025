package com.github.pkovacs.aoc.y2025;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day11 extends AbstractDay {

    public static void main(String[] args) {
        var lines = readLines(getInputPath());

        var graph = buildGraph(lines);
        var cache = new HashMap<String, PathCounts>();

        System.out.println("Part 1: " + dfs(graph, cache, "you").total());
        System.out.println("Part 2: " + dfs(graph, cache, "svr").c11);
    }

    private static Map<String, List<String>> buildGraph(List<String> lines) {
        var graph = new HashMap<String, List<String>>();
        for (var line : lines) {
            var parts = line.replace(":", "").split(" ");
            graph.put(parts[0], List.of(parts).subList(1, parts.length));
        }
        return graph;
    }

    private static PathCounts dfs(Map<String, List<String>> graph, Map<String, PathCounts> cache, String node) {
        if (node.equals("out")) {
            return new PathCounts(1, 0, 0, 0);
        }

        var cached = cache.get(node);
        if (cached != null) {
            return cached;
        }

        var result = graph.get(node).stream()
                .map(next -> dfs(graph, cache, next))
                .map(res -> res.visit(node))
                .reduce(PathCounts::add).orElseThrow();
        cache.put(node, result);
        return result;
    }

    /**
     * Record to store path counts.
     * c00 represents path count without visiting any of the two designated nodes of part 2 (i.e., "dac" and "fft"),
     * c01 represents path count visiting only one of these nodes ("dac"),
     * c10 represents path count visiting only the other designated node ("fft"), and
     * c11 represents path count visiting both designated nodes.
     */
    record PathCounts(long c00, long c01, long c10, long c11) {

        PathCounts add(PathCounts other) {
            return new PathCounts(c00 + other.c00, c01 + other.c01, c10 + other.c10, c11 + other.c11);
        }

        PathCounts visit(String node) {
            return switch (node) {
                case "dac" -> new PathCounts(0, c00, 0, c01);
                case "fft" -> new PathCounts(0, 0, c00, c01);
                default -> this;
            };
        }

        long total() {
            return c00 + c01 + c10 + c11;
        }

    }

}
