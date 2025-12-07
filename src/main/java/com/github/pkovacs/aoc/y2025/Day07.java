package com.github.pkovacs.aoc.y2025;

import java.util.Arrays;

public class Day07 extends AbstractDay {

    public static void main(String[] args) {
        char[][] input = toCharMatrix(readLines(getInputPath()));
        long[][] counts = new long[input.length][input[0].length];

        // Start at the given position (with count 1)
        for (int j = 0; j < counts[0].length; j++) {
            if (input[0][j] == 'S') {
                counts[0][j] = 1;
            }
        }

        // Calculate combination counts (timelines) in each row (similarly to Pascal's triangle)
        int split = 0;
        for (int i = 1; i < counts.length; i++) {
            for (int j = 0; j < counts[0].length; j++) {
                long above = counts[i - 1][j]; // count above the current cell
                if (input[i][j] == '^' && above != 0) {
                    counts[i][j - 1] += above;
                    counts[i][j + 1] += above;
                    split++;
                } else {
                    counts[i][j] += above;
                }
            }
        }
        long sum = Arrays.stream(counts[counts.length - 1]).sum();

        System.out.println("Part 1: " + split);
        System.out.println("Part 2: " + sum);
    }

}

