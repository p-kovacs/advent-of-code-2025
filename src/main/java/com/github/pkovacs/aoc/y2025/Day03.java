package com.github.pkovacs.aoc.y2025;

public class Day03 extends AbstractDay {

    public static void main(String[] args) {
        var lines = readLines(getInputPath());
        var banks = lines.stream().map(Day03::toIntArray).toList();

        System.out.println("Part 1: " + banks.stream().mapToLong(a -> findMaxValue(a, 2)).sum());
        System.out.println("Part 2: " + banks.stream().mapToLong(a -> findMaxValue(a, 12)).sum());
    }

    private static int[] toIntArray(String line) {
        return line.chars().map(c -> c - '0').toArray();
    }

    private static long findMaxValue(int[] a, int digits) {
        long value = 0;
        int lastIndex = -1;
        for (int k = 0; k < digits; k++) {
            // Select the k-th digit to be the first one among the largest digits in the relevant part of the array
            int next = -1;
            for (int i = lastIndex + 1; i <= a.length - digits + k; i++) {
                if (a[i] > next) {
                    next = a[i];
                    lastIndex = i;
                }
            }
            value = value * 10 + next;
        }
        return value;
    }

}
