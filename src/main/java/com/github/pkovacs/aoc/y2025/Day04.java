package com.github.pkovacs.aoc.y2025;

import java.util.List;

import com.github.pkovacs.util.CharTable;

public class Day04 extends AbstractDay {

    public static void main(String[] args) {
        var lines = readLines(getInputPath());

        System.out.println("Part 1: " + countRolls(lines, false));
        System.out.println("Part 2: " + countRolls(lines, true));
    }

    private static int countRolls(List<String> lines, boolean recursive) {
        var table = new CharTable(lines);
        int count = 0;
        while (true) {
            var toRemove = table.cells()
                    .filter(p -> table.get(p) == '@')
                    .filter(p -> table.neighbors8(p).filter(n -> table.get(n) == '@').count() < 4)
                    .toList();
            count += toRemove.size();
            toRemove.forEach(p -> table.set(p, '.'));

            if (!recursive || toRemove.isEmpty()) {
                break;
            }
        }
        return count;
    }

}
