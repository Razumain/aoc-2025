package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;
import com.aaasec.aoc2025.utils.PatternString;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Day3Solution extends Solution {
  public Day3Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {
    int totalvolt = 0;
    for (String line : input) {
      List<Integer> numbers = new ArrayList<>();
      for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
        numbers.add(Integer.parseInt(String.valueOf(c)));
      }
      PatternString pString = new PatternString(line.length());
      int max = 0;
      for (int i = 0; i < numbers.size()-1; i++) {
        int current = numbers.get(i);
        if (current > max) {
          max = current;
        }
      }
      int maxPos = numbers.indexOf(max);
      pString.setIndexChar(maxPos, 0);
      int nextMax = 0;
      int nextPos = 0;
      for (int j = maxPos + 1; j < numbers.size(); j++) {
        int current = numbers.get(j);
        if (current > nextMax) {
          nextMax = current;
          nextPos = j;
        }
      }
      pString.setIndexChar(nextPos, 1);
      int volt = max*10 + nextMax;
      log.add("Digits  : %s", line);
      log.add("Selected: %s", pString.toString());
      log.add("Joltage : %s", String.valueOf(volt));

      totalvolt += volt;
    }

    String result = "";
    log.add("Result part 1: %s", totalvolt);
  }

  @Override
  public void solvePart2(List<String> input) {
    long totalvolt = 0;
    for (String line : input) {
      List<Integer> numbers = new ArrayList<>();
      for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
        numbers.add(Integer.parseInt(String.valueOf(c)));
      }
      int max = 0;
      int pos = 0;
      List<Integer> voltDigits = new ArrayList<>();
      PatternString pString = new PatternString(line.length());
      for (int i = 0; i < 12 ; i++) {
        Integer[] maxDigitInfo = getMaxDigit(numbers, i, pos);
        int maxDigit = maxDigitInfo[0];
        pos = maxDigitInfo[1] +1;
        pString.setIndexChar(pos-1, i);
        voltDigits.add(maxDigit);
      }

      final List<String> voltDigStrList = voltDigits.stream().map(String::valueOf).toList();
      String joltageStr = String.join("", voltDigStrList);
      BigInteger joltage = new BigInteger(joltageStr);
      log.add("Digits  : %s", line);
      log.add("Selected: %s", pString.toString());
      log.add("Joltage : %s", joltage.toString());

      totalvolt += joltage.longValue();
    }

    String result = "";
    log.add("Result part 1: %s", totalvolt);
  }

  private Integer[] getMaxDigit(final List<Integer> numbers, final int digit, final int startPos) {
    int max = 0;
    int maxPos = 0;
    int lastPos = numbers.size() - (11-digit);
    for (int i = startPos; i < lastPos; i++) {
      int current = numbers.get(i);
      if (current > max) {
        max = current;
        maxPos = i;
      }
    }
    return new Integer[] {max, maxPos};
  }
}
