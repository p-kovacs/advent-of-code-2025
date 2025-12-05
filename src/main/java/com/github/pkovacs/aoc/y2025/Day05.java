package com.github.pkovacs.aoc.y2025;

import java.util.List;
import java.util.stream.Stream;

import com.github.pkovacs.util.Range;
import com.github.pkovacs.util.Utils;

public class Day05 extends AbstractDay {

    public static void main(String[] args) {
        var input = readString(getInputPath());
        var sections = collectSections(input);

        var ranges = sections.getFirst().stream().map(Utils::parseLongs).map(Range::bound).toList();
        var values = sections.getLast().stream().map(Utils::parseLong).toList();

        System.out.println("Part 1: " + solve1(ranges, values));
        System.out.println("Part 2: " + solve2(ranges));
    }

    private static long solve1(List<Range> ranges, List<Long> values) {
        return values.stream().filter(v -> ranges.stream().anyMatch(r -> r.contains(v))).count();
    }

    private static long solve2(List<Range> ranges) {
        var bounds = ranges.stream().flatMap(r -> Stream.of(r.min, r.max)).distinct().sorted().toList();
        long total = bounds.size();
        for (int i = 0; i < bounds.size() - 1; i++) {
            var gap = new Range(bounds.get(i) + 1, bounds.get(i + 1) - 1);
            if (ranges.stream().anyMatch(gap::overlaps)) {
                total += gap.size();
            }
        }
        return total;
    }

}
