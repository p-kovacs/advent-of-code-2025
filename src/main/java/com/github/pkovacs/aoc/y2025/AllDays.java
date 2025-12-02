package com.github.pkovacs.aoc.y2025;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * Verifies the solution for each day against the expected answers for my puzzle input files.
 */
public class AllDays {

    private static final List<Day> DAYS = List.of(
            new Day("Day 01", Day01::main, "1029", "5892"),
            new Day("Day 02", Day02::main, "22062284697", "46666175279"),
//            new Day("Day 03", Day03::main, "", ""),
//            new Day("Day 04", Day04::main, "", ""),
//            new Day("Day 05", Day05::main, "", ""),
//            new Day("Day 06", Day06::main, "", ""),
//            new Day("Day 07", Day07::main, "", ""),
//            new Day("Day 08", Day08::main, "", ""),
//            new Day("Day 09", Day09::main, "", ""),
//            new Day("Day 10", Day10::main, "", ""),
//            new Day("Day 11", Day11::main, "", ""),
//            new Day("Day 12", Day12::main, "", ""),
            new Day("The End", null, null, null)
    );

    public static void main(String[] args) {
        String format = "%-12s%-8s%-8s%8s%n";
        System.out.printf(format, "Day", "Part 1", "Part 2", "Time");

        DAYS.stream().filter(day -> day.mainMethod != null).forEach(day -> {
            long start = System.nanoTime();
            var results = runDay(day);
            long time = (System.nanoTime() - start) / 1_000_000L;

            System.out.printf(format, day.name, evaluate(day, results, 0), evaluate(day, results, 1), time + " ms");
        });
    }

    private static String evaluate(Day day, List<String> results, int index) {
        var expected = index == 0 ? day.expected1 : day.expected2;
        return results.size() == 2 && expected.equals(results.get(index)) ? "\u2714" : "FAILED";
    }

    private static List<String> runDay(Day day) {
        var origOut = System.out;
        try {
            var out = new ByteArrayOutputStream(200);
            System.setOut(new PrintStream(out));
            day.mainMethod.accept(null);
            return out.toString(StandardCharsets.UTF_8).lines().map(l -> l.split(": ")[1]).toList();
        } catch (Exception e) {
            return List.of();
        } finally {
            System.setOut(origOut);
        }
    }

    private record Day(String name, Consumer<String[]> mainMethod, String expected1, String expected2) {}

}
