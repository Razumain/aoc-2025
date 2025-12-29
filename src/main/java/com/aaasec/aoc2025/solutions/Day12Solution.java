package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;
import com.aaasec.aoc2025.utils.dlx.DancingLinks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day12Solution extends Solution {
  public Day12Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {
    List<Present> presents = parsePresents(input);
    List<Space> spaces = parseSpaces(input);

    // Precompute variants once
    int[] presentSize = new int[6];
    for (int s = 0; s < 6; s++) {
      final Present present = presents.get(s);
      int[][] shape = present.shape;
      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          if (shape[y][x] == 1) presentSize[s]++;
        }
      }
    }


    int fitsCount = 0;
    int idx = 0;
    for (Space space : spaces) {
      idx ++;
      int size = space.x() * space.y();
      int requiredArea = 0;
      int[] presentCount = space.presents();
      for (int i=0; i<6; i++) {
        int area = presentCount[i] * presentSize[i];
        requiredArea += area;
      }
      boolean fits = size >= requiredArea;
      if (fits) {
        fitsCount++;
      }
      log.add("%d: Available %d, Required %d, Diff %d - Fits %s - Total fits %d", idx, size, requiredArea, size - requiredArea, fits ? "true" : "false", fitsCount);
    }


    log.add("Result part 2: %s", fitsCount);
  }

  @Override
  public void solvePart2(List<String> input) {
    log.add("No part 2 problem");
  }


  private List<Present> parsePresents(final List<String> input) {
    boolean done = false;
    List<Present> presents = new ArrayList<>();
    int idx = 0;
    while (!done) {
      String line = input.get(idx++);
      if (line.endsWith(":") && line.length() < 3) {
        int[][] shape = new int[3][3];
        for (int i = 0; i < 3 ; i++) {
          line = input.get(idx++);
          for (int j = 0; j < 3 ; j++) {
            if (line.charAt(j) == '#') {
              shape[i][j] = 1;
            }
          }
        }
        presents.add(new Present(shape));
        idx++;
      } else {
        done = true;
      }
    }
    return presents;
  }

  private List<Space> parseSpaces(final List<String> input) {

    int idx = 0;
    boolean skip = true;

    while (skip) {
      String line = input.get(idx);
      if (line.length() > 3 && line.indexOf("x") > 0) {
        skip = false;
      } else {
        idx++;
      }
    }

    List<Space> spaces = new ArrayList<>();
    for (int i = idx ; i < input.size() ; i++) {
      String line = input.get(i);
      final String[] split = line.split(": ");
      final String[] xy = split[0].split("x");
      int x = Integer.parseInt(xy[0]);
      int y = Integer.parseInt(xy[1]);
      final String[] presentsStr = split[1].split(" ");
      int[] presents = new int[6];
      for (int j = 0 ; j < 6 ; j++) {
        presents[j] = Integer.parseInt(presentsStr[j]);
      }
      spaces.add(new Space(x, y, presents));
    }
    return spaces;
  }


  public record Present(
      int[][] shape
  ) {}
  public record Space(
     int x, int y,
     int[] presents
  ){}

}
