package com.aaasec.aoc2025.utils.dlx;

// Author: Rafal Szymanski <me@rafal.io>
// Modified: add support for SECONDARY columns (at-most-one constraints).
//
// Usage notes:
// - By default (old behavior), all columns are PRIMARY (exact cover of all columns).
// - To use secondary columns, call the constructor with primaryCols:
//     new DancingLinks(grid, primaryCols, handler)
//   Columns [0..primaryCols-1] are PRIMARY; [primaryCols..COLS-1] are SECONDARY.
//
// Secondary columns are not linked into the header row. They can still be covered/uncovered
// when a chosen row touches them, enforcing "at most once", but they are not required to be
// covered for a solution.

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DancingLinks {

  static final boolean verbose = false;

  class DancingNode {
    DancingNode L, R, U, D;
    ColumnNode C;

    // hooks node n1 `below` current node
    DancingNode hookDown(DancingNode n1) {
      assert (this.C == n1.C);
      n1.D = this.D;
      n1.D.U = n1;
      n1.U = this;
      this.D = n1;
      return n1;
    }

    // hook a node n1 to the right of `this` node
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
    int size; // number of ones in current column
    String name;
    boolean primary; // PRIMARY => exact cover required; SECONDARY => at-most-one

    public ColumnNode(String n, boolean primary) {
      super();
      size = 0;
      name = n;
      this.primary = primary;
      C = this;
    }

    void cover() {
      // Only PRIMARY columns are in the header list.
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

  private int primaryCols; // number of PRIMARY columns at start (0..primaryCols-1)

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

  private ColumnNode selectColumnNodeNaive() {
    return (ColumnNode) header.R;
  }

  // Selects among PRIMARY columns only (because only PRIMARY columns are linked in header row)
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

  // grid is a grid of 0s and 1s to solve the exact cover for
  // primaryCols indicates how many leading columns are PRIMARY; the rest are SECONDARY
  private ColumnNode makeDLXBoard(int[][] grid, int primaryCols) {
    final int COLS = grid[0].length;
    final int ROWS = grid.length;

    ColumnNode headerNode = new ColumnNode("header", true);
    ArrayList<ColumnNode> columnNodes = new ArrayList<>(COLS);

    // Create columns: first primaryCols are PRIMARY and linked into header row.
    for (int i = 0; i < COLS; i++) {
      boolean isPrimary = i < primaryCols;
      ColumnNode n = new ColumnNode(Integer.toString(i), isPrimary);
      columnNodes.add(n);
      if (isPrimary) {
        headerNode = (ColumnNode) headerNode.hookRight(n);
      }
    }

    // Restore header reference (same pattern as original code)
    headerNode = headerNode.R.C;
    headerNode.size = primaryCols;

    // Build rows
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

  private void showInfo() {
    System.out.println("Number of updates: " + updates);
  }

  // Old behavior: all columns are PRIMARY (exact cover of all columns)
  public DancingLinks(int[][] grid) {
    this(grid, grid[0].length, new DefaultHandler());
  }

  // Old behavior: all columns are PRIMARY (exact cover of all columns)
  public DancingLinks(int[][] grid, SolutionHandler h) {
    this(grid, grid[0].length, h);
  }

  // New behavior: first primaryCols columns are PRIMARY; remaining are SECONDARY
  public DancingLinks(int[][] grid, int primaryCols, SolutionHandler h) {
    this.primaryCols = primaryCols;
    header = makeDLXBoard(grid, primaryCols);
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
  }}

