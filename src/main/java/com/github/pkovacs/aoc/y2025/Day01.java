package com.github.pkovacs.aoc.y2025;

public class Day01 extends AbstractDay {

    public static void main(String[] args) {
        var lines = readLines(getInputPath());

        int pos = 50;
        int ans1 = 0, ans2 = 0;
        for (var line : lines) {
            int dist = Integer.parseInt(line.substring(1));
            int to = (line.charAt(0) == 'L') ? pos - dist : pos + dist;
            ans1 += to % 100 == 0 ? 1 : 0;
            ans2 += Math.abs(to / 100) + ((to <= 0 && pos != 0) ? 1 : 0);
            pos = Math.floorMod(to, 100);
        }

        System.out.println("Part 1: " + ans1);
        System.out.println("Part 2: " + ans2);
    }

}
