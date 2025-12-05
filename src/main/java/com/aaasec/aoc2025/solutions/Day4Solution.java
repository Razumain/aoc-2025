package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;
import com.aaasec.aoc2025.utils.Matrix;
import com.aaasec.aoc2025.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Day4Solution extends Solution {
  public Day4Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {
    Matrix matrix = new Matrix(input);
    Utils.RR rr = Utils.removeFromMatrix(matrix, "@", ".", 1, "@", 3);
    log.add("Result: %s", rr.removed());
    log.lfadd("Stack of rolls after removal:\n%s", rr.matrix().toString());
  }

  @Override
  public void solvePart2(List<String> input) {
    Matrix matrix = new Matrix(input);
    int totalRemoved = 0;
    Utils.RR rr = Utils.removeFromMatrix(matrix, "@", ".", 1, "@", 3);
    totalRemoved += rr.removed();
    log.add("Movable: %s, Total Removed: %s", rr.removed(), totalRemoved);
    while (rr.removed() > 0) {
      matrix = rr.matrix();
      rr = Utils.removeFromMatrix(matrix, "@", ".", 1, "@", 3);
      totalRemoved += rr.removed();
      log.add("Movable: %s, Total Removed: %s", rr.removed(), totalRemoved);
    }
    log.add("Result part 2: %s", totalRemoved);

    log.lfadd("Final stack of rolls:\n%s", rr.matrix().toString());
  }



/*  public void oldSolvePart1(List<String> input) {
    int rows = input.size();
    int width = input.getFirst().length();
    String[][] rolls = new String[rows][width];
    for (int i = 0; i < rows; i++) {
      String line = input.get(i);
      for (int j = 0; j < width; j++) {
        rolls[i][j] = line.substring(j, j+1);
      }
    }
    int movableCount = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < width; j++) {
        if (rolls[i][j].equals("@")) {
          int adjecent = adjecentCount(i, j, rolls);
          if (adjecent < 4) {
            movableCount++;
          }
        }
      }
    }
    log.add("Result part 1: %s", movableCount);
  }

  private int adjecentCount(final int row, final int col, final String[][] rolls) {
    Integer prevRow = row-1 >= 0 ? row-1 : null;
    Integer nextRow = row+1 < rolls.length ? row+1 : null;
    Integer prevCol = col-1 >= 0 ? col-1 : null;
    Integer nextCol = col+1 < rolls[0].length ? col+1 : null;
    int count = 0;

    count += count(prevRow, prevCol, rolls);
    count += count(prevRow, col, rolls);
    count += count(prevRow, nextCol, rolls);
    count += count(row, prevCol, rolls);
    count += count(row, nextCol, rolls);
    count += count(nextRow, prevCol, rolls);
    count += count(nextRow, col, rolls);
    count += count(nextRow, nextCol, rolls);
    return count;
  }

  private int count(final Integer row, final Integer col, final String[][] rolls) {
    if (row == null || col == null) {
      return 0;
    }
    return rolls[row][col].equals("@") ? 1 : 0;
  }*/




/*  public void oldSolvePart2(List<String> input) {
    int rows = input.size();
    int width = input.getFirst().length();
    String[][] rolls = new String[rows][width];
    for (int i = 0; i < rows; i++) {
      String line = input.get(i);
      for (int j = 0; j < width; j++) {
        rolls[i][j] = line.substring(j, j+1);
      }
    }

    int totalRemoved = 0;
    RemoveResult removeResult = removeRolls(rows, width, rolls);
    String[][] newRolls = removeResult.rolls;
    int movableCount = removeResult.removed;
    totalRemoved += movableCount;
    log.add("Movable: %s, Total Removed: %s", movableCount, totalRemoved);
    while (movableCount > 0) {
      removeResult = removeRolls(rows, width, newRolls);
      newRolls = removeResult.rolls;
      movableCount = removeResult.removed;
      totalRemoved += movableCount;
      log.add("Movable: %s, Total Removed: %s", movableCount, totalRemoved);
    }

    log.add("Result part 2: %s", totalRemoved);

    String finalStack = "";
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < width; j++) {
        finalStack += newRolls[i][j];
      }
      finalStack += "\n";
    }

    log.lfadd("Final stack of rolls:\n%s", finalStack);
  }

  private RemoveResult removeRolls(final int rows, final int width, final String[][] rolls) {
    String[][] newRolls = new String[rows][width];
    int movableCount = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < width; j++) {
        newRolls[i][j] = rolls[i][j];
        if (rolls[i][j].equals("@")) {
          int adjecent = adjecentCount(i, j, rolls);
          if (adjecent < 4) {
            movableCount++;
            newRolls[i][j] = ".";
          }
        }
      }
    }
    return new RemoveResult(newRolls, movableCount);
  }

  @Data
  @AllArgsConstructor
  public static class RemoveResult{
    String[][] rolls;
    int removed;
  }*/

}
