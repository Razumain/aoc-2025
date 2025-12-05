package com.aaasec.aoc2025.utils;

import com.aaasec.aoc2025.solutions.Day4Solution;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
class MatrixTest {

  public static final List<String> INPUT = List.of(
      "..@@.@@@@.",
      "@@@.@.@.@@",
      "@@@@@.@.@@",
      "@.@@@@..@.",
      "@@.@@@@.@@",
      ".@@@@@@@.@",
      ".@.@.@.@@@",
      "@.@@@.@@@@",
      ".@@@@@@@@.",
      "@.@.@@@.@."
  );

  @Test
  void matrixTest() throws Exception {

    Matrix m = new Matrix(INPUT);

    int extractable = 0;
    for (int i = 0; i < m.getRows() ;  i++) {
      for (int j = 0; j < m.getCols(); j++) {
        Matrix.Location loc = new Matrix.Location(i, j);
        if (m.getValue(loc).equals("@")) {
          if (m.countAdjecent(loc, 1, "@") < 4) {
            extractable++;
          }
        }
      }
    }
    Assertions.assertEquals(13, extractable);
    log.info("Extractable: {}", extractable);
    log.info("{}", m);

    // Do task 2
    extractable = 0;
    RemoveResult rr = removeMovable(m);
    extractable += rr.removed;
    log.info("Removed: {}", rr.removed);
    log.info("updatedMatrix\n{}", rr.matrix.toString());

    while (rr.removed > 0) {
      rr = removeMovable(rr.matrix);
      extractable += rr.removed;
      log.info("Removed: {}", rr.removed);
      log.info("Total removed: {}", extractable);
      log.info("updatedMatrix\n{}", rr.matrix.toString());
    }

    String finalMatrix = "..........\n"
        + "..........\n"
        + "..........\n"
        + "....@@....\n"
        + "...@@@@...\n"
        + "...@@@@@..\n"
        + "...@.@.@@.\n"
        + "...@@.@@@.\n"
        + "...@@@@@..\n"
        + "....@@@...";

    Assertions.assertEquals(finalMatrix, rr.matrix.toString().trim());

  }

  RemoveResult removeMovable(Matrix m) {
    Matrix newMatrix = new Matrix(m.getValues());
    int removed = 0;
    for (int i = 0; i < m.getRows() ;  i++) {
      for (int j = 0 ; j<m.getCols() ; j++) {
        Matrix.Location loc = new Matrix.Location(i, j);
        if (m.getValue(loc).equals("@")) {
          if (m.countAdjecent(loc, 1, "@") < 4) {
            newMatrix.setValue(loc, ".");
            removed++;
          }
        }
      }
    }
    return new RemoveResult(newMatrix, removed);
  }


  public record RemoveResult(Matrix matrix, int removed){}


}
