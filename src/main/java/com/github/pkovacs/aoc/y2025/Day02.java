package com.github.pkovacs.aoc.y2025;

import java.util.ArrayList;
import java.util.List;

import com.github.pkovacs.util.Range;

public class Day02 extends AbstractDay {

    public static void main(String[] args) {
        var input = readString(getInputPath());
        var ranges = parseRanges(input);

        System.out.println("Part 1: " + sumInvalid(ranges, false));
        System.out.println("Part 2: " + sumInvalid(ranges, true));
    }

    private static List<Range> parseRanges(String input) {
        var parts = input.trim().split(",");
        var ranges = new ArrayList<Range>();
        for (var p : parts) {
            var bounds = p.split("-");
            ranges.add(new Range(parseLong(bounds[0]), parseLong(bounds[1])));
        }
        return ranges;
    }

    private static long sumInvalid(List<Range> ranges, boolean advanced) {
        long sum = 0;
        for (var range : ranges) {
            for (long i = range.min(); i <= range.max(); i++) {
                var s = String.valueOf(i);
                int n = s.length();
                if (advanced) {
                    for (int k = 1; k <= n / 2; k++) {
                        if (n % k == 0 && s.equals(s.substring(0, k).repeat(n / k))) {
                            sum += i;
                            break;
                        }
                    }
                } else if (n % 2 == 0 && s.equals(s.substring(0, n / 2).repeat(2))) {
                    sum += i;
                }
            }
        }
        return sum;
    }

}
