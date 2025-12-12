package com.github.pkovacs.aoc.y2025;

import java.util.ArrayList;
import java.util.stream.IntStream;

import com.github.pkovacs.util.CharTable;

/**
 * WARNING: this is a simple and stupid solution for this deceivingly designed puzzle. It seems that for the actual
 * input files, each region either can trivially contain all presents (in 3x3 subregions, without any overlap), or
 * trivially cannot contain them (when their total size exceeds the size of the region). Although that's NOT true
 * for the example. For complicated cases, this would be a really hard problem.
 */
public class Day12 extends AbstractDay {

    public static void main(String[] args) {
        var input = readString(getInputPath());
        var sections = collectSections(input);

        // Parse shapes and check that each of them fits in a 3x3 region
        var shapeSizes = new ArrayList<Integer>();
        for (var sec : sections.subList(0, sections.size() - 1)) {
            var table = new CharTable(sec.subList(1, sec.size()));
            if (table.width() != 3 || table.height() != 3) {
                throw new IllegalArgumentException("Unexpected shape.");
            }
            shapeSizes.add(table.count('#'));
        }

        // Parse regions and check if they can be trivially categorized
        int minCount = 0, maxCount = 0;
        for (var line : sections.getLast()) {
            int[] ints = parseInts(line);
            int size = ints[0] * ints[1];

            int subregionCount = (ints[0] / 3) * (ints[1] / 3);
            int shapeCount = IntStream.range(0, shapeSizes.size()).map(i -> ints[i + 2]).sum();
            if (subregionCount >= shapeCount) {
                minCount++;
            }

            int minSize = IntStream.range(0, shapeSizes.size()).map(i -> ints[i + 2] * shapeSizes.get(i)).sum();
            if (size >= minSize) {
                maxCount++;
            }
        }

        if (minCount != maxCount) {
            throw new IllegalArgumentException(
                    String.format("The answer is between %d and %d (inclusive).", minCount, maxCount));
        }

        System.out.println("Part 1: " + minCount);
        System.out.println("Part 2: " + 0);
    }

}
