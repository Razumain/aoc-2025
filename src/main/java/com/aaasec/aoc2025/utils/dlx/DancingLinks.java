package com.aaasec.aoc2025.utils.dlx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DancingLinks {

  static final boolean verbose = false;

  class DancingNode {
    DancingNode L, R, U, D;
    ColumnNode C;

    DancingNode hookDown(DancingNode n1) {
      assert (this.C == n1.C);
      n1.D = this.D;
      n1.D.U = n1;
      n1.U = this;
      this.D = n1;
      return n1;
    }

    DancingNode hookRight(DancingNode n1) {
      n1.R = this.R;
      n1.R.L = n1;
      n1.L = this;
      this.R = n1;
      return n1;
    }

    void unlinkLR() {
      this.L.R = this.R;
      this.R.L = this.L;
      updates++;
    }

    void relinkLR() {
      this.L.R = this.R.L = this;
      updates++;
    }

    void unlinkUD() {
      this.U.D = this.D;
      this.D.U = this.U;
      updates++;
    }

    void relinkUD() {
      this.U.D = this.D.U = this;
      updates++;
    }

    public DancingNode() {
      L = R = U = D = this;
    }

    public DancingNode(ColumnNode c) {
      this();
      C = c;
    }
  }

  class ColumnNode extends DancingNode {
    int size;         // number of ones in current column
    String name;
    boolean primary;  // PRIMARY => must be covered; SECONDARY => at-most-one

    public ColumnNode(String n, boolean primary) {
      super();
      size = 0;
      name = n;
      this.primary = primary;
      C = this;
    }

    void cover() {
      // Only PRIMARY columns are linked into the header list.
      if (primary) unlinkLR();

      for (DancingNode i = this.D; i != this; i = i.D) {
        for (DancingNode j = i.R; j != i; j = j.R) {
          j.unlinkUD();
          j.C.size--;
        }
      }

      if (primary) header.size--; // counts remaining PRIMARY columns only
    }

    void uncover() {
      for (DancingNode i = this.U; i != this; i = i.U) {
        for (DancingNode j = i.L; j != i; j = j.L) {
          j.C.size++;
          j.relinkUD();
        }
      }

      if (primary) relinkLR();
      if (primary) header.size++;
    }
  }

  private ColumnNode header;
  private int solutions = 0;
  private int updates = 0;
  private SolutionHandler handler;
  private List<DancingNode> answer;

  private int primaryCols;

  // Heart of the algorithm
  private void search(int k) {
    // With secondary columns: solution when ALL PRIMARY columns have been removed.
    if (header.R == header) {
      if (verbose) {
        System.out.println("-----------------------------------------");
        System.out.println("Solution #" + solutions + "\n");
      }
      handler.handleSolution(answer);
      if (verbose) {
        System.out.println("-----------------------------------------");
      }
      solutions++;
    } else {
      ColumnNode c = selectColumnNodeHeuristic();
      c.cover();

      for (DancingNode r = c.D; r != c; r = r.D) {
        answer.add(r);

        for (DancingNode j = r.R; j != r; j = j.R) {
          j.C.cover();
        }

        search(k + 1);

        r = answer.remove(answer.size() - 1);
        c = r.C;

        for (DancingNode j = r.L; j != r; j = j.L) {
          j.C.uncover();
        }
      }
      c.uncover();
    }
  }

  private ColumnNode selectColumnNodeHeuristic() {
    int min = Integer.MAX_VALUE;
    ColumnNode ret = null;
    for (ColumnNode c = (ColumnNode) header.R; c != header; c = (ColumnNode) c.R) {
      if (c.size < min) {
        min = c.size;
        ret = c;
      }
    }
    return ret;
  }

  /**
   * Dense matrix builder (compatibility).
   * grid is ROWS x COLS with 0/1.
   */
  private ColumnNode makeDLXBoardDense(int[][] grid, int primaryCols) {
    final int COLS = grid[0].length;
    final int ROWS = grid.length;

    ColumnNode headerNode = new ColumnNode("header", true);
    ArrayList<ColumnNode> columnNodes = new ArrayList<>(COLS);

    for (int i = 0; i < COLS; i++) {
      boolean isPrimary = i < primaryCols;
      ColumnNode n = new ColumnNode(Integer.toString(i), isPrimary);
      columnNodes.add(n);
      if (isPrimary) headerNode = (ColumnNode) headerNode.hookRight(n);
    }

    headerNode = headerNode.R.C;
    headerNode.size = primaryCols;

    for (int i = 0; i < ROWS; i++) {
      DancingNode prev = null;
      for (int j = 0; j < COLS; j++) {
        if (grid[i][j] == 1) {
          ColumnNode col = columnNodes.get(j);
          DancingNode newNode = new DancingNode(col);
          if (prev == null) prev = newNode;
          col.U.hookDown(newNode);
          prev = prev.hookRight(newNode);
          col.size++;
        }
      }
    }

    return headerNode;
  }

  /**
   * Sparse matrix builder (recommended).
   * rowsOnes is an array of rows; each row contains the column indices that have a 1.
   * colCount is the total number of columns.
   */
  private ColumnNode makeDLXBoardSparse(int colCount, int[][] rowsOnes, int primaryCols) {
    final int COLS = colCount;

    ColumnNode headerNode = new ColumnNode("header", true);
    ArrayList<ColumnNode> columnNodes = new ArrayList<>(COLS);

    for (int i = 0; i < COLS; i++) {
      boolean isPrimary = i < primaryCols;
      ColumnNode n = new ColumnNode(Integer.toString(i), isPrimary);
      columnNodes.add(n);
      if (isPrimary) headerNode = (ColumnNode) headerNode.hookRight(n);
    }

    headerNode = headerNode.R.C;
    headerNode.size = primaryCols;

    for (int[] colsInRow : rowsOnes) {
      DancingNode prev = null;
      for (int idx = 0; idx < colsInRow.length; idx++) {
        int colIdx = colsInRow[idx];
        ColumnNode col = columnNodes.get(colIdx);

        DancingNode newNode = new DancingNode(col);
        if (prev == null) prev = newNode;

        col.U.hookDown(newNode);
        prev = prev.hookRight(newNode);
        col.size++;
      }
    }

    return headerNode;
  }

  private void showInfo() {
    System.out.println("Number of updates: " + updates);
  }

  // --------------------
  // Constructors
  // --------------------

  // Old behavior: all columns PRIMARY, dense input
  public DancingLinks(int[][] grid) {
    this(grid, grid[0].length, new DefaultHandler());
  }

  // Old behavior: all columns PRIMARY, dense input
  public DancingLinks(int[][] grid, SolutionHandler h) {
    this(grid, grid[0].length, h);
  }

  // Dense input + primaryCols
  public DancingLinks(int[][] grid, int primaryCols, SolutionHandler h) {
    this.primaryCols = primaryCols;
    header = makeDLXBoardDense(grid, primaryCols);
    handler = h;
  }

  // NEW: Sparse input + primaryCols (recommended for AoC Day 12)
  public DancingLinks(int colCount, int primaryCols, int[][] rowsOnes, SolutionHandler h) {
    this.primaryCols = primaryCols;
    header = makeDLXBoardSparse(colCount, rowsOnes, primaryCols);
    handler = h;
  }

  public void runSolver() {
    solutions = 0;
    updates = 0;
    answer = new LinkedList<>();
    try {
      search(0);
    } catch (ExistsSolutionHandler.FoundSolution ignore) {
      // abort on first solution
    }
    if (verbose) showInfo();
  }

  // --------------------
  // Handler types (as you had them)
  // --------------------

  public interface SolutionHandler {
    void handleSolution(List<DancingLinks.DancingNode> solution);
  }

  public static class DefaultHandler implements SolutionHandler {
    public void handleSolution(List<DancingLinks.DancingNode> answer) {
      for (DancingLinks.DancingNode n : answer) {
        StringBuilder ret = new StringBuilder();
        ret.append(n.C.name).append(' ');
        DancingLinks.DancingNode tmp = n.R;
        while (tmp != n) {
          ret.append(tmp.C.name).append(' ');
          tmp = tmp.R;
        }
        System.out.println(ret);
      }
    }
  }

  /**
   * Use this to answer the question "does at least one solution exist?"
   * It stops the DLX search immediately when the first solution is found.
   */
  public static class ExistsSolutionHandler implements SolutionHandler {

    /** Thrown to abort DLX search as soon as a solution is found. */
    public static final class FoundSolution extends RuntimeException {
      public FoundSolution() { super("Solution found"); }
    }

    private boolean found = false;

    public boolean found() {
      return found;
    }

    @Override
    public void handleSolution(List<DancingLinks.DancingNode> solution) {
      found = true;
      throw new FoundSolution();
    }
  }
}
