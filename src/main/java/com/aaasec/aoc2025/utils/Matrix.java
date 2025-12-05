package com.aaasec.aoc2025.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a two-dimensional matrix of strings, with utility methods for manipulation
 * and querying of its structure and contents.
 */
public class Matrix {

  @Getter private final String[][] values;
  @Getter private final int rows;
  @Getter private final int cols;

  /**
   * Constructs a Matrix with the specified number of rows and columns.
   *
   * @param rows the number of rows for the matrix
   * @param cols the number of columns for the matrix
   */
  Matrix(int rows, int cols) {
    this.values = new String[rows][cols];
    this.rows = rows;
    this.cols = cols;
  }

  /**
   * Constructs a Matrix object initialized with the provided two-dimensional array of string values.
   * The matrix dimensions (rows and columns) are determined based on the input array.
   *
   * @param values a two-dimensional array of strings representing the initial values of the matrix,
   *               where each inner array corresponds to a row in the matrix, and all rows must have the same length
   */
  public Matrix(final String[][] values) {
    this.rows = values.length;
    this.cols = values[0].length;
    this.values = new String[rows][cols];

    for (int i = 0; i < rows; i++) {
      System.arraycopy(values[i], 0, this.values[i], 0, cols);
    }
  }

  /**
   * Constructs a Matrix object based on a list of strings, where each string represents
   * a row in the matrix. The number of rows is determined by the size of the input list,
   * and the number of columns is determined by the length of the first string in the list.
   *
   * @param orgLines a list of strings, where each string corresponds to a row of the matrix
   *                 and all strings must have the same length to form a valid matrix
   */
  public Matrix(List<String> orgLines) {
    List<String> lines = new ArrayList<>(orgLines);
    this.rows = lines.size();
    this.cols = lines.get(0).length();
    values = new String[rows][cols];
    for (int i = 0; i< rows; i++) {
      for (int j = 0; j< cols; j++) {
        values[i][j] = lines.get(i).substring(j, j+1);
      }
    }
  }

  /**
   * Counts the number of adjacent cells within a specified radius from a given location
   * that match the specified value in the matrix. Note that the location itself is not counted.
   *
   * @param loc the location in the matrix from which to search for adjacent cells
   * @param radius the radius within which to count adjacent cells
   * @param value the value to match in the adjacent cells
   * @return the count of adjacent cells within the radius that match the specified value
   */
  public int countAdjecent(Location loc, int radius, String value) {
    if (radius < 0) {
      throw new IllegalArgumentException("Radius must be non-negative");
    }
    if (radius == 0) {
      return 0;
    }
    int count = 0;
    for (int i = -radius; i <= radius; i++) {
      for (int j = -radius; j <= radius; j++) {
        if (i == 0 && j == 0) {
          continue;
        }
        Location adjLoc = new Location(loc.row + i, loc.col + j);
        if (!isOutOfBounds(adjLoc) && values[adjLoc.row][adjLoc.col].equals(value)) {
          count++;
        }
      }
    }
    return count;
  }

  /**
   * Retrieves the value at the specified location in the matrix.
   *
   * @param loc the location in the matrix specified by its row and column indices
   * @return the value stored at the specified location in the matrix
   */
  public String getValue(Location loc) {
    return values[loc.row][loc.col];
  }

  /**
   * Updates the value at the specified location in the matrix.
   *
   * @param loc the location in the matrix specified by its row and column indices
   * @param value the new value to be set at the specified location
   */
  public void setValue(Location loc, String value) {
    values[loc.row][loc.col] = value;
  }

  /**
   * Determines whether the specified location is out of the bounds of the matrix.
   *
   * @param loc the location to check, specified by its row and column indices
   * @return true if the location is outside the bounds of the matrix; false otherwise
   */
  boolean isOutOfBounds(Location loc) {
    return loc.row < 0 || loc.row >= rows || loc.col < 0 || loc.col >= cols;
  }

  /**
   * Determines whether the specified location is on any edge of the matrix
   * based on the provided number of rows and columns.
   *
   * @param loc the location to check, specified by its row and column indices
   * @param rows the total number of rows in the matrix
   * @param cols the total number of columns in the matrix
   * @return an {@code Edge} record indicating whether the location is on the
   *         left edge, right edge, top edge, bottom edge, a corner, or none.
   */
  public Edge isOnEdge(Location loc, int rows, int cols) {
    return new Edge(loc.col == 0, loc.col == cols-1, loc.row == 0, loc.row == rows-1);
  }

  /**
   * Returns a string representation of the matrix.
   * Each row of the matrix is represented as a single line in the resulting string,
   * with rows separated by newline characters.
   *
   * @return the string representation of the matrix, with rows separated by newline characters
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        sb.append(values[i][j]);
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * A record representing the edges of a matrix location. It indicates whether a
   * given location lies on specific edges or corners of the matrix or none of them.
   *
   * @param left indicates if the location is on the left edge of the matrix
   * @param right indicates if the location is on the right edge of the matrix
   * @param top indicates if the location is on the top edge of the matrix
   * @param bottom indicates if the location is on the bottom edge of the matrix
   * @param corner indicates if the location is at the corner of the matrix
   * @param none indicates if the location is not on any edge or corner of the matrix
   */
  public record Edge(
      boolean left, boolean right, boolean top, boolean bottom, boolean corner, boolean none
  ) {
    public Edge(final boolean left, final boolean right, final boolean top, final boolean bottom) {
      this(left, right, top, bottom,
          (left && top) || (right && top) || (left && bottom) || (right && bottom),
          !(left || right || top || bottom));
    }
  }

  /**
   * Represents a specific row and column location in a matrix.
   *
   * This record is immutable and is used to specify a position within a matrix
   * by its row and column indices. It can be utilized for operations such as
   * retrieving, updating, or evaluating values within the matrix at a specific
   * location.
   *
   * @param row the row index of the location, starting from 0
   * @param col the column index of the location, starting from 0
   */
  public record Location(
      int row, int col
  ) {}

}
