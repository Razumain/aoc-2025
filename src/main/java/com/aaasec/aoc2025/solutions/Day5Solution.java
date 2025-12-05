package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;
import com.aaasec.aoc2025.utils.Range;
import com.aaasec.aoc2025.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day5Solution extends Solution {
  public Day5Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {

    int idx = 0;
    List<Range> ranges = new ArrayList<>();
    while (!input.get(idx).trim().isEmpty()) {
      ranges.add(new Range(input.get(idx)));
      idx++;
    }
    idx++;
    List<Long> ingredients = new ArrayList<>();
    while (idx < input.size()) {
      ingredients.add(Long.parseLong(input.get(idx)));
      idx++;
    }

    int freshCount = 0;
    for (long n : ingredients) {
      boolean fresh = false;
      for (Range r : ranges) {
        if (r.contains(n)) {
          fresh = true;
          break;
        }
      }
      if (fresh) {
        freshCount++;
      }
    }


    log.add("Result part 1: %s", freshCount);
  }

  @Override
  public void solvePart2(List<String> input) {

    int idx = 0;
    List<Range> ranges = new ArrayList<>();
    while (!input.get(idx).trim().isEmpty()) {
      ranges.add(new Range(input.get(idx)));
      idx++;
    }

    log.lfadd("Initial range count: %d", ranges.size());
    Utils.OverlapResult overlapResult = Utils.reduceOverlaps(ranges);
    while (overlapResult.reduced()) {
      overlapResult = Utils.reduceOverlaps(overlapResult.ranges());
    }

    log.add("Final range count: %d\n", overlapResult.ranges().size());
    long freshIdCount = 0;
    for (Range r : overlapResult.ranges()) {
      log.add("Range %s has %s numbers to add", r, r.size());
      freshIdCount += r.size();
    }

    log.lfadd("Result part 2: %s", freshIdCount);
  }

}
