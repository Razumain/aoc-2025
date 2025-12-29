package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Day1Solution extends Solution {
  public Day1Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {


    int pos = 50;
    int passw = 0;
    for (String line : input) {
      int step = Integer.parseInt(line.substring(1));
      boolean right = line.startsWith("R");
      pos = right ? pos + step : pos - step;
      pos = pos % 100;
      if (pos == 0) {
        passw++;
      }

    }

    log.add("Result part 1: %s", passw);
  }

  @Override
  public void solvePart2(List<String> input) {
    int pos = 50;
    int passw = 0;
    for (String line : input) {
      int step = Integer.parseInt(line.substring(1));
      boolean right = line.startsWith("R");
      if (right) {
        pos = pos + step;
        if (pos >= 100) {
          passw = passw + pos/100;
        }
      } else {
        if (pos == 0) {
          pos = 100;
        }
        pos = pos - step;
        if (pos <= 0) {
          passw = passw + Math.abs(pos/100) +1 ;
        }
      }
      pos = pos % 100;
      if (pos < 0) {
        pos = 100 + pos;
      }

    }
    log.add("Result part 2: %s", passw);
  }
}
