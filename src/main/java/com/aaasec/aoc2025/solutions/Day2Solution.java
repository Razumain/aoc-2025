package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day2Solution extends Solution {
  public Day2Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {
    final String[] split = input.get(0).split(",");
    List<Range> ranges = new ArrayList<>();
    for (String s : split) {
      String[] split2 = s.split("-");
      ranges.add(new Range(Long.parseLong(split2[0]), Long.parseLong(split2[1])));
    }
    long iterations = 0;
    long maxIt=0;
    for(Range r : ranges) {
      iterations += r.size;
      maxIt = Math.max(maxIt, r.size);
      log.add("Range from %s to %s has %s numbers", r.min, r.max, r.size);
    }
    log.add("Total iterations: %s", iterations);
    log.add("Max iterations: %s", maxIt);

    String result = "";
    log.add("Result part 1: %s", result);

    long illegal = 0;
    for (Range r : ranges) {
      for (long id = r.min ; id < r.max ; id++) {
        boolean ok = checkid(id);
        if (!ok) {
          log.add("Illegal id: %s", id);
          illegal = illegal + id;
        }
      }
    }
    log.add("Illegal ids: %s", illegal);


  }

  private boolean checkid(final long id) {
    String idStr = Long.toString(id);
    boolean ok = true;
    if (idStr.length() %2 != 0) {
      return true;
    }
    int len = idStr.length()/2;
    return !idStr.substring(0, len).equals(idStr.substring(len));
  }
  private boolean checkid2(final long id) {
    String idStr = Long.toString(id);
    boolean ok = true;
    for (int i = 1; i <= idStr.length(); i++) {
      int partCount = idStr.length()/i;
      if (partCount > 1 && idStr.length() %i == 0) {
        String firstPart = idStr.substring(0, i);
        boolean match = true;
        for (int part = 1 ; part < partCount ; part++) {
          String partToCompare = idStr.substring(i*part, i*(part+1));
          if (!firstPart.equals(partToCompare)) {
            match = false;
            break;
          }
        }
        if (match) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void solvePart2(List<String> input) {
    final String[] split = input.get(0).split(",");
    List<Range> ranges = new ArrayList<>();
    for (String s : split) {
      String[] split2 = s.split("-");
      ranges.add(new Range(Long.parseLong(split2[0]), Long.parseLong(split2[1])));
    }
    long iterations = 0;
    long maxIt=0;
    for(Range r : ranges) {
      iterations += r.size;
      maxIt = Math.max(maxIt, r.size);
      log.add("Range from %s to %s has %s numbers", r.min, r.max, r.size);
    }
    log.add("Total iterations: %s", iterations);
    log.add("Max iterations: %s", maxIt);

    String result = "";
    log.add("Result part 1: %s", result);

    long illegal = 0;
    for (Range r : ranges) {
      for (long id = r.min ; id < r.max ; id++) {
        if (!checkid(id)){
          int sdf=0;
        }
        boolean ok = !checkid2(id);
        if (!ok) {
          checkid2(id);
          log.add("Illegal id: %s", id);
          illegal = illegal + id;
        }
      }
    }
    log.add("Part 2 Illegal ids: %s", illegal);
  }

  @Data
  @AllArgsConstructor
  public static class Range{
    final long min;
    final long max;
    final long size;

    public Range(final long min, final long max) {
      this.max = max;
      this.min = min;
      this. size = max - min + 1;
    }
  }

}


