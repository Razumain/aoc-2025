package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day6Solution extends Solution {
  public Day6Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {

    String[] row1 = input.get(0).split("\\s+");
    String[] row2 = input.get(1).split("\\s+");
    String[] row3 = input.get(2).split("\\s+");
    String[] row4 = input.get(3).split("\\s+");
    String[] op = input.get(4).split("\\s+");

    long total = 0;
    for (int i = 0; i < row1.length; i++) {
      long res = 0;
      if (op[i].equals("+")) {
        res = Long.parseLong(row1[i]) + Long.parseLong(row2[i]) + Long.parseLong(row3[i]) + Long.parseLong(row4[i]);
        total += res;
        continue;
      }
      if (op[i].equals("*")) {
        res = Long.parseLong(row1[i]) * Long.parseLong(row2[i]) * Long.parseLong(row3[i]) * Long.parseLong(row4[i]);
        total += res;
      }
    }



    log.add("Result part 1: %s", total);
  }

  @Override
  public void solvePart2(List<String> input) {

    int startLoc = input.getFirst().length()-1;
    List<Problem> problems = new ArrayList<>();
    boolean complete = false;
    while (!complete) {
      Problem p = new Problem(input, startLoc);
      problems.add(p);
      startLoc = p.nextLoc;
      if (startLoc < 1) {
        complete = true;
      }
    }

    log.add("Logging 10 first problems");
    for (int i = 0; i < 10; i++) {
      Problem p = problems.get(i);
      log.add("Problem %d (%s) values: %s", i, p.op, String.join(",", p.values.stream().map(Object::toString).toList()));
      log.add("Problem %d Solution: %s", i, p.solve());
    }
    log.add("Total of %d problems", problems.size());
    long total = 0;
    for (Problem p : problems) {
      total += p.solve();
    }
    log.add("Result part 2: %s", total);
  }

  public static class Problem {
    public List<Long> values;
    String op;
    int nextLoc;

    public Problem(List<String> input, int startLoc) {

      boolean complete = false;
      values = new ArrayList<>();
      while (!complete) {
        Long val = getVal(startLoc, input);
        String operand = getOp(startLoc, input);
        if (val != null) {
          values.add(val);
        }
        if (!operand.isEmpty()) {
          this.op = operand;
        }
        if (val == null || startLoc == 0) {
          complete = true;
        }
        startLoc--;
      }
      nextLoc = startLoc;
    }

    public long solve() {
      if (values == null || values.isEmpty()) {
        return 0;
      }
      long result = values.get(0);
      for (int i = 1; i < values.size(); i++) {
        Long value = values.get(i);
        if (op.equals("+")) {
          result += value;
        } else {
          result *= value;
        }
      }
      return result;
    }

    private String getOp(final int startLoc, final List<String> input) {
      String opString = input.get(4);
      String op = String.valueOf(opString.charAt(startLoc));
      return op.trim();
    }

    private Long getVal(final int startLoc, final List<String> input) {
      char[] p1 = new char[4];
      for (int i = 0; i < 4; i++) {
        p1[i] = input.get(i).charAt(startLoc);
      }
      final String valStr = new String(p1).trim();
      if (valStr.isEmpty()) {
        return null;
      }
      return Long.parseLong(valStr);
    }
  }

  record PR(boolean more, Problem p, int nextLoc){}


}
