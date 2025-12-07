package com.github.pkovacs.aoc.y2025;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day06 extends AbstractDay {

    public static void main(String[] args) {
        var lines = readLines(getInputPath());

        System.out.println("Part 1: " + solve(lines, false));
        System.out.println("Part 2: " + solve(lines, true));
    }

    private static long solve(List<String> lines, boolean advanced) {
        int[] pos = IntStream.range(0, lines.getLast().length())
                .filter(i -> lines.getLast().charAt(i) != ' ').toArray();
        List<Character> operators = Arrays.stream(pos).mapToObj(i -> lines.getLast().charAt(i)).toList();
        List<List<Long>> values = parseValues(lines.subList(0, lines.size() - 1), pos, advanced);
        return IntStream.range(0, operators.size()).mapToLong(i -> evaluate(operators.get(i), values.get(i))).sum();
    }

    private static List<List<Long>> parseValues(List<String> lines, int[] pos, boolean advanced) {
        var result = new ArrayList<List<Long>>();
        for (int i = 0; i < pos.length; i++) {
            int to = (i < pos.length - 1) ? pos[i + 1] - 1 : lines.getFirst().length();
            result.add(parseValues(lines, pos[i], to, advanced));
        }
        return result;
    }

    private static List<Long> parseValues(List<String> lines, int from, int to, boolean advanced) {
        if (advanced) {
            var list = new ArrayList<Long>();
            for (int j = from; j < to; j++) {
                long v = 0;
                for (String line : lines) {
                    if (line.charAt(j) != ' ') {
                        v = v * 10 + (line.charAt(j) - '0');
                    }
                }
                list.add(v);
            }
            return list;
        } else {
            return lines.stream().map(line -> line.substring(from, to).trim()).map(Long::parseLong).toList();
        }
    }

    private static long evaluate(char operator, List<Long> values) {
        return (operator == '+')
                ? values.stream().mapToLong(i -> i).sum()
                : values.stream().mapToLong(i -> i).reduce(1, (a, b) -> a * b);
    }

}
