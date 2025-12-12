package com.github.pkovacs.aoc.y2025;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class Day10 extends AbstractDay {

    public static void main(String[] args) {
        var lines = readLines(getInputPath());

        var machines = lines.stream().map(Machine::new).toList();

        System.out.println("Part 1: " + machines.stream().mapToLong(Machine::solve1).sum());
        System.out.println("Part 2: " + machines.stream().mapToLong(Machine::solve2).sum());
    }

    private static class Machine {

        final String pattern;
        final List<List<Integer>> buttons;
        final int[] target;

        /**
         * The "augmented matrix" representation of the linear equations, where the variables are the push counts
         * of the buttons. The matrix contains the coefficients and also the right hand side values as the last column.
         */
        long[][] matrix;

        /** Calculated upper bounds for each variable. */
        long[] limits;

        /** Current values of the variables, -1 means unknown. */
        long[] x;

        /** Best result (total push count) found so far. */
        long best;

        Machine(String line) {
            var parts = line.split(" ");
            pattern = parts[0].substring(1, parts[0].length() - 1).replace(".", "0").replace("#", "1");
            buttons = Arrays.stream(parts, 1, parts.length - 1).map(p -> listOf(parseInts(p))).toList();
            target = parseInts(parts[parts.length - 1]);
        }

        /**
         * Simple solution for part 1. As it's not reasonable to push a button twice or more, we iterate over all
         * subsets of the buttons and select the smallest set that yields the expected pattern.
         */
        long solve1() {
            int targetBits = Integer.parseInt(new StringBuilder(pattern).reverse().toString(), 2);
            int[] buttonBits = buttons.stream().mapToInt(b -> b.stream().mapToInt(j -> 1 << j).sum()).toArray();

            int minCount = Integer.MAX_VALUE;
            for (int s = (1 << buttonBits.length) - 1; s >= 0; s--) {
                int bits = 0;
                for (int i = 0; i < buttonBits.length; i++) {
                    if ((s & (1 << i)) != 0) {
                        bits ^= buttonBits[i];
                    }
                }
                if (bits == targetBits) {
                    minCount = min(minCount, Integer.bitCount(s));
                }
            }

            return minCount;
        }

        /**
         * Solves part 2 using a DFS search (actually, it's branch-and-bound). However, in order to make it much
         * faster, <a href="https://en.wikipedia.org/wiki/Gaussian_elimination">Gaussian elimination</a> is applied
         * to the system of linear equations before the search process. In some cases, there is only one feasible
         * solution, which is found by this elimination. In other cases, it simplifies the equations to contain
         * fewer variables (that is, fewer buttons contribute to the counters).
         * <p>
         * For example, for the input line {@code "[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}"},
         * the original matrix looks like this:
         * <pre>
         *    1    1    1    0   10
         *    1    1    0    1   11
         *    1    1    0    1   11
         *    1    0    1    0    5
         *    1    1    1    0   10
         *    0    1    0    0    5
         * </pre>
         * After the Gaussian elimination, it looks like this:
         * <pre>
         *    1    0    0    1    6
         *    0    1    0    0    5
         *    0    0    1   -1   -1
         *    0    0    0    0    0
         *    0    0    0    0    0
         *    0    0    0    0    0
         * </pre>
         * Based on this simplified matrix, the push count of button 1 can be calculated directly (5 times).
         * Then we can enumerate the possible push counts of button 0, which must be between 0 and 6, inclusive
         * (based on the first equation). For each value, the push counts of buttons 3 and then of button 2 can be
         * calculated directly again.
         */
        long solve2() {
            // Initialize the matrix and the limits for each variable
            initMatrix();
            limits = new long[buttons.size()];
            Arrays.fill(limits, Long.MAX_VALUE);
            calculateLimits(matrix, limits);

            // Perform Gaussian elimination to simplify the matrix
            simplifyMatrix();

            // Attempt to decrease limits for the variables based on the simplified matrix
            calculateLimits(matrix, limits);

            // Search for the optimal solution
            x = new long[buttons.size()];
            Arrays.fill(x, -1);
            best = Integer.MAX_VALUE;
            search(0);

            return best;
        }

        private void initMatrix() {
            matrix = new long[target.length][buttons.size() + 1];
            for (int i = 0; i < matrix.length; i++) {
                matrix[i][buttons.size()] = target[i];
                for (int j = 0; j < buttons.size(); j++) {
                    if (buttons.get(j).contains(i)) {
                        matrix[i][j] = 1;
                    }
                }
            }
        }

        private void calculateLimits(long[][] matrix, long[] limits) {
            int[] rowsWithNonnegativeValues = IntStream.range(0, matrix.length)
                    .filter(i -> IntStream.range(0, buttons.size()).noneMatch(j -> matrix[i][j] < 0))
                    .toArray();

            for (int j : IntStream.range(0, buttons.size()).toArray()) {
                long limit = Arrays.stream(rowsWithNonnegativeValues)
                        .filter(i -> matrix[i][j] > 0)
                        .mapToLong(i -> matrix[i][buttons.size()] / matrix[i][j])
                        .min().orElse(Long.MAX_VALUE);
                limits[j] = min(limits[j], limit);
            }
        }

        /**
         * Performs Gaussian elimination to simplify the system of linear equations. For numerical stability and
         * efficiency reasons, integer values are used, and the rows of the matrix are multiplied as necessary.
         */
        private void simplifyMatrix() {
            for (int i = 0; i < min(matrix.length, buttons.size()); i++) {
                // Select pivot element: the one with maximum absolute value in column i
                // (this selection attempts to avoid zero elements and also improves numerical stability of the method)
                int row = i;
                var pivot = matrix[i][i];
                for (int k = i + 1; k < matrix.length; k++) {
                    if (abs(matrix[k][i]) > abs(pivot)) {
                        pivot = matrix[k][i];
                        row = k;
                    }
                }
                if (pivot == 0) {
                    // We cannot continue (the rank of the matrix can be lower than the number of equations)
                    break;
                }

                // Swap rows
                var pivotRow = matrix[row];
                matrix[row] = matrix[i];
                matrix[i] = pivotRow;

                // Normalize row i
                normalizeRow(matrix[i]);
                if (pivot < 0) {
                    for (int j = 0; j < matrix[i].length; j++) {
                        matrix[i][j] *= -1;
                    }
                }
                pivot = matrix[i][i];

                // Transform all rows except for i
                for (int k = 0; k < matrix.length; k++) {
                    if (k != i) {
                        long coeff = matrix[k][i];
                        for (int j = 0; j < matrix[k].length; j++) {
                            matrix[k][j] = pivot * matrix[k][j] - coeff * matrix[i][j];
                        }
                    }
                }
            }

            // Normalize rows
            Arrays.stream(matrix).forEach(Machine::normalizeRow);
        }

        private static void normalizeRow(long[] row) {
            var minPositiveValue = Arrays.stream(row).map(Math::abs).filter(i -> i > 0).min();
            if (minPositiveValue.isPresent() && minPositiveValue.getAsLong() > 1) {
                long gcd = gcd(Arrays.stream(row).map(Math::abs).toArray());
                for (int j = 0; j < row.length; j++) {
                    row[j] /= gcd;
                }
            }
        }

        private void search(int knownCount) {
            if (knownCount == x.length) {
                // A feasible solution is found, update best result
                best = min(best, Arrays.stream(x).sum());
                return;
            }

            // Check if we can cut this branch
            if (Arrays.stream(x).filter(v -> v >= 0).sum() >= best) {
                return;
            }

            // Check if we can calculate a variable directly from an equation with a single unknown variable
            for (int i : IntStream.range(0, matrix.length).toArray()) {
                int[] unknowns = IntStream.range(0, x.length).filter(j -> matrix[i][j] != 0 && x[j] == -1).toArray();
                if (unknowns.length == 1) {
                    int k = unknowns[0];
                    long value = calculateValue(i, k);
                    if (value < 0) {
                        return; // not a feasible solution (variables must be non-negative integers)
                    }

                    x[k] = value;
                    search(knownCount + 1);
                    x[k] = -1;
                    return;
                }
            }

            // No variables can be calculated, so we select a variable and iterate over its possible values
            int selected = selectVariableToIterate();
            for (long value = 0; value <= limits[selected]; value++) {
                x[selected] = value;
                search(knownCount + 1);
            }
            x[selected] = -1;
        }

        private long calculateValue(int i, int k) {
            long numerator = matrix[i][x.length] - IntStream.range(0, x.length)
                    .filter(j -> j != k && matrix[i][j] != 0).mapToLong(j -> matrix[i][j] * x[j]).sum();
            return numerator % matrix[i][k] == 0 ? numerator / matrix[i][k] : -1;
        }

        /**
         * Selects the next variable to iterate over. It is selected as the one that is involved in an equation
         * with the fewest unknown variables, and among those, the one with the lowest upper bound for iteration.
         */
        private int selectVariableToIterate() {
            int fewestUnknown = IntStream.range(0, matrix.length)
                    .map(this::getUnknownVariableCount)
                    .filter(c -> c > 0)
                    .min().orElseThrow();
            return IntStream.range(0, x.length).boxed()
                    .filter(j -> x[j] == -1 && IntStream.range(0, matrix.length)
                            .anyMatch(i -> matrix[i][j] != 0 && getUnknownVariableCount(i) == fewestUnknown))
                    .min(Comparator.comparing(j -> limits[j])).orElseThrow();
        }

        private int getUnknownVariableCount(int rowIndex) {
            return (int) IntStream.range(0, x.length).filter(j -> matrix[rowIndex][j] != 0 && x[j] == -1).count();
        }

    }

}
