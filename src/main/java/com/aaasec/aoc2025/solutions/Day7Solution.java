package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;
import com.aaasec.aoc2025.utils.Matrix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class Day7Solution extends Solution {
  public Day7Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {
    int startPoint = 0;
    String firstLine = input.get(0);
    for (int i = 0; i < input.size(); i++) {
      if (firstLine.charAt(i) == 'S') {
        startPoint = i;
        break;
      }
    }
    int count = 0;
    List<Integer> pathCols = new ArrayList<>();
    pathCols.add(startPoint);
    for (int i = 0; i < input.size(); i++) {
      String line = input.get(i);
      List<Integer> newPathCols = new ArrayList<>();
      for (int j = 0; j < pathCols.size(); j++) {
        int col = pathCols.get(j);
        char c = line.charAt(col);
        if (c == '^') {
          addIfNew(newPathCols, col-1);
          addIfNew(newPathCols, col+1);
          count++;
        } else {
          addIfNew(newPathCols, col);
        }
      }
      pathCols = new ArrayList<>(newPathCols);
    }
    log.add("Result part 1: %s", count);

  }

  private void addIfNew(final List<Integer> newPathCols, final int i) {
    if (!(newPathCols.contains(i))) {
      newPathCols.add(i);
    }
  }

  /**
   * Solves the problem for Part 2 by calculating the desired result based on the input list of strings.
   * This solution uses a bottom-up approach to compute the number of paths for each cell in a grid structure.
   * <p>
   * The basic idea is that each point on a line has the same number of paths as the point below IF the current
   * point is empty ("."). If the current point is a splitter. Then the number of paths for this point onward towards
   * the bottom IS the number of paths from the 2 points on the line below that is - 1 column and +1 column from
   * the current point.
   * <p>
   * The fact that we do the calculation bottom up, allows us to know the number of paths below us. Making the
   * traversal a simple add operation at each splitter, traversing to the top.
   *
   * @param input a list of strings representing the input data, where each string corresponds to a line of the grid
   */
  @Override
  public void solvePart2(List<String> input) {

    // Get start location
    int startPoint = 0;
    for (int i = 0; i < input.size(); i++) {
      if (input.getFirst().charAt(i) == 'S') {
        startPoint = i;
        break;
      }
    }

    // Create an empty path collector with one path per column
    List<Long> startPaths = new ArrayList<>();
    for (int i = 0; i < input.size(); i++) {
      startPaths.add(1L);
    }
    // Create a baseline = empty path collector
    List<Long> nextLinePaths = new ArrayList<>(startPaths);
    // Traverse the graph bottom up
    for (int i = input.size()-1; i >= 0; i--) {
      List<Long> paths = new ArrayList<>(startPaths);
      String line = input.get(i);
      for (int j = 0; j < line.length(); j++) {
        char c = line.charAt(j);
        if (c == '^') {
          paths.set(j, nextLinePaths.get(j-1) + nextLinePaths.get(j+1));
        } else  {
          paths.set(j, nextLinePaths.get(j));
        }
      }
      nextLinePaths = paths;
    }

    long result = nextLinePaths.get(startPoint);

    log.add("Result part 2: %s", result);
  }
}
