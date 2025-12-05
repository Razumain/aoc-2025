package com.aaasec.aoc2025.utils;

import com.aaasec.aoc2025.solutions.Day5Solution;

import java.util.ArrayList;
import java.util.List;

public class Utils {

  /**
   * Removes specific values from a given matrix by replacing them with a replacement value.
   * This method serves as an overloaded variant of the main removeFromMatrix method,
   * utilizing default criteria for removal evaluation.
   *
   * @param matrix the original matrix from which values will be removed or replaced
   * @param removeStr the string value in the matrix to be removed or replaced
   * @param replacementStr the string value to replace occurrences of the removeStr
   * @return an instance of RR containing the modified matrix and the total count of replaced values
   */
  public static RR removeFromMatrix(final Matrix matrix, final String removeStr, final String replacementStr) {
    return removeFromMatrix(matrix, removeStr, replacementStr, 1, removeStr, 3);
  }

  /**
   * Removes specific values from a given matrix by replacing them with a replacement value based on defined criteria.
   * The method checks each cell in the matrix and replaces the value if it matches the specified removal string
   * and if the count of adjacent cells (within the given radius) containing the target string is less than or equal
   * to the maximum allowable count. The method returns a new matrix and the count of replaced values.
   *
   * @param matrix the original matrix from which values will be removed or replaced
   * @param removeStr the string value in the matrix to be removed or replaced
   * @param replacementStr the string value to replace occurrences of the removeStr
   * @param radius the radius within which to evaluate adjacent cells
   * @param targetStr the string value to match in the adjacent cells during the evaluation
   * @param maxCount the maximum count of adjacent cells containing targetStr that allows replacement
   * @return an instance of RR containing the modified matrix and the total count of replaced values
   */
  public static RR removeFromMatrix(final Matrix matrix, final String removeStr, final String replacementStr,
      int radius, final String targetStr, final int maxCount) {
    Matrix newMatrix = new Matrix(matrix.getValues());
    int removed = 0;
    for (int i = 0; i < matrix.getRows(); i++) {
      for (int j = 0; j < matrix.getCols(); j++) {
        Matrix.Location loc = new Matrix.Location(i, j);
        if (matrix.getValue(loc).equals(removeStr)) {
          if (matrix.countAdjecent(loc, radius, targetStr) <= maxCount) {
            newMatrix.setValue(loc, replacementStr);
            removed++;
          }
        }
      }
    }
    return new RR(newMatrix, removed);
  }

  /**
   * A record that encapsulates the result of modifying a matrix by removing or replacing specific values.
   * This data structure holds the modified matrix and the total count of values that were replaced during the operation.
   * It is primarily used as the return type for matrix processing methods.
   *
   * @param matrix the modified matrix after specific values have been removed or replaced
   * @param removed the total count of values that were replaced in the matrix
   */
  public record RR(Matrix matrix, int removed) {}


  public static OverlapResult reduceOverlaps(final List<Range> ranges) {
    List<Range> reducedRanges = new ArrayList<>();
    for (int i = 0; i < ranges.size(); i++) {
      Range r1 = ranges.get(i);
      for (int j = i + 1; j < ranges.size(); j++) {
        if (i == j) continue;
        Range r2 = ranges.get(j);
        if (r1.overlaps(r2)) {
          Range reduced = new Range(Math.min(r1.getMin(), r2.getMin()), Math.max(r1.getMax(), r2.getMax()));
          for (int k = 0; k < ranges.size(); k++) {
            if (k != i && k != j) {
              reducedRanges.add(ranges.get(k));
            }
          }
          reducedRanges.add(reduced);
          return new OverlapResult(true, reducedRanges);
        }
      }
    }
    return new OverlapResult(false, ranges);
  }

  public record OverlapResult(boolean reduced, List<Range> ranges) {
  }

}
