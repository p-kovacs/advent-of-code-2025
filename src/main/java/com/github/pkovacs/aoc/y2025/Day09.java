package com.github.pkovacs.aoc.y2025;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.pkovacs.util.Bfs;
import com.github.pkovacs.util.Box;
import com.github.pkovacs.util.Graph;
import com.github.pkovacs.util.Pos;

public class Day09 extends AbstractDay {

    public static void main(String[] args) {
        var lines = readLines(getInputPath());
        var tiles = lines.stream().map(Day09::parsePos).toList();

        System.out.println("Part 1: " + solve1(tiles));
        System.out.println("Part 2: " + solve2(tiles));
    }

    private static long solve1(List<Pos> tiles) {
        long max = 0;
        for (int i = 0; i < tiles.size(); i++) {
            for (int j = i + 1; j < tiles.size(); j++) {
                var box = Box.bound(List.of(tiles.get(i), tiles.get(j)));
                max = Math.max(box.size(), max);
            }
        }
        return max;
    }

    private static long solve2(List<Pos> origTiles) {
        int n = origTiles.size();

        // Compress distances along the two axes: the i-th largest x/y coordinate is replaced with 2*i
        long[] xCoords = origTiles.stream().mapToLong(Pos::x).sorted().distinct().toArray();
        long[] yCoords = origTiles.stream().mapToLong(Pos::y).sorted().distinct().toArray();
        List<Pos> tiles = origTiles.stream()
                .map(p -> new Pos(2L * Arrays.binarySearch(xCoords, p.x), 2L * Arrays.binarySearch(yCoords, p.y)))
                .toList();

        // Collect the "loop" of tiles (line segments between the corner tiles)
        var loop = new HashSet<Pos>();
        for (int i = 0; i < n; i++) {
            tiles.get(i).lineTo(tiles.get((i + 1) % n)).forEach(loop::add);
        }

        // Collect the "exterior" tiles (using BFS within an appropriate bounding box)
        var boundingBox = Box.bound(tiles).extend(1);
        var graph = Graph.of(Pos::neighbors).filterNodes(p -> boundingBox.contains(p) && !loop.contains(p));
        Set<Pos> exterior = Bfs.findPaths(graph, boundingBox.min()).keySet();

        // Find the rectangle of maximum original size that doesn't overlap with the set of exterior tiles
        long max = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                var box = Box.bound(List.of(tiles.get(i), tiles.get(j)));
                long origSize = Box.bound(List.of(origTiles.get(i), origTiles.get(j))).size();
                if (origSize > max && isValidBox(box, exterior)) {
                    max = origSize;
                }
            }
        }

        return max;
    }

    /**
     * Checks if the given box has no overlap with the given set of exterior tiles. In fact, it is equivalent to
     * {@code box.stream().noneMatch(exterior::contains)}, but to make it faster, this method only checks the tiles
     * along the border of the box. (Stream API is deliberately not used to increase performance.)
     */
    private static boolean isValidBox(Box box, Set<Pos> exterior) {
        Pos min = box.min();
        Pos max = box.max();
        for (long x = min.x; x <= max.x; x++) {
            if (exterior.contains(new Pos(x, min.y)) || exterior.contains(new Pos(x, max.y))) {
                return false;
            }
        }
        for (long y = min.y + 1; y <= max.y - 1; y++) {
            if (exterior.contains(new Pos(min.x, y)) || exterior.contains(new Pos(max.x, y))) {
                return false;
            }
        }
        return true;
    }

    private static Pos parsePos(String s) {
        var parts = s.split(",");
        return new Pos(parseLong(parts[0]), parseLong(parts[1]));
    }

}
