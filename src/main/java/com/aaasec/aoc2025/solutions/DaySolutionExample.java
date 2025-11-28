package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DaySolutionExample extends Solution {
  public DaySolutionExample(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {
    log.add("Trying out my implementation to solve problem 1");
    log.add("\nWorking on the selected input:\n%s", String.join("\n", input));
  }

  @Override
  public void solvePart2(List<String> input) {
    log.add("Trying out my implementation to solve problem 2");
    log.add("This part is not done yet. Coming soon...");
  }
}
