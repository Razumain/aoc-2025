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
    List<List<int[][]>> variantsByShape = new ArrayList<>();
    int[] blocks = new int[6];
    for (int s = 0; s < 6; s++) {
      blocks[s] = ShapeVariants.blockCount3x3(presents.get(s).shape());
      variantsByShape.add(ShapeVariants.generateVariants3x3(presents.get(s).shape()));
    }

    int fitsCount = 0;

    for (int idx = 0; idx < spaces.size(); idx++) {
      Space space = spaces.get(idx);
      System.out.printf("Checking space %d (%d x %d)...\n", idx, space.x(), space.y());
      log.add("Checking space %d (%d x %d)...", idx, space.x(), space.y());

      // Optional: avoid printf per space (slow); log occasionally instead
      // if (idx % 50 == 0) System.out.printf("Checking space %d (%d x %d)...\n", idx, space.x(), space.y());

      // NEW: build SPARSE rows for DLX
      var spec = DlxMatrixBuilder.buildSparse(space.x(), space.y(), space.presents(), variantsByShape, blocks);

      // If builder pruned it (e.g., area), it can return rowsOnes length 0
      if (spec.rowsOnes().length == 0) continue;

      DancingLinks.ExistsSolutionHandler h = new DancingLinks.ExistsSolutionHandler();

      // NEW: use sparse ctor: (colCount, primaryCols, rowsOnes, handler)
      DancingLinks dlx = new DancingLinks(spec.colCount(), spec.primaryCols(), spec.rowsOnes(), h);
      dlx.runSolver();

      if (h.found()) {
        fitsCount++;
        // Optional debug:
        System.out.printf("Space %d fits! -  Total fits = %d\n", idx, fitsCount);
        log.add("Space %d fits! -  Total fits = %d", idx, fitsCount);
      }
    }

    System.out.printf("Result part 1: %s\n", fitsCount);
    log.add("Result part 1: %s", fitsCount);
  }

  @Override
  public void solvePart2(List<String> input) {

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



  public final static class ShapeVariants {

    /** Returns unique variants; each variant is int[k][2] list of occupied (x,y) cells. */
    public static List<int[][]> generateVariants3x3(int[][] shape) {
      // shape is [row][col] (y,x) with 1 for '#'
      Set<String> seen = new HashSet<>();
      List<int[][]> variants = new ArrayList<>();

      // 8 transforms = 4 rotations of original + 4 rotations of flipped
      int[][] cur = copy3x3(shape);
      for (int r = 0; r < 4; r++) {
        addIfNew(cur, seen, variants);
        cur = rot90(cur);
      }

      int[][] flipped = flipH(shape); // horizontal mirror
      cur = copy3x3(flipped);
      for (int r = 0; r < 4; r++) {
        addIfNew(cur, seen, variants);
        cur = rot90(cur);
      }

      return variants;
    }

    private static void addIfNew(int[][] grid, Set<String> seen, List<int[][]> out) {
      int[][] cells = extractAndNormalizeCells(grid); // occupied cells as (x,y)
      String key = canonicalKey(cells);
      if (seen.add(key)) out.add(cells);
    }

    private static int[][] extractAndNormalizeCells(int[][] grid) {
      List<int[]> tmp = new ArrayList<>();
      int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;

      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          if (grid[y][x] == 1) {
            tmp.add(new int[]{x, y});
            if (x < minX) minX = x;
            if (y < minY) minY = y;
          }
        }
      }

      // normalize to (0,0)
      for (int[] p : tmp) {
        p[0] -= minX;
        p[1] -= minY;
      }

      // sort for stable representation
      tmp.sort(Comparator.<int[]>comparingInt(a -> a[1]).thenComparingInt(a -> a[0]));

      int[][] cells = new int[tmp.size()][2];
      for (int i = 0; i < tmp.size(); i++) cells[i] = tmp.get(i);
      return cells;
    }

    private static String canonicalKey(int[][] cells) {
      StringBuilder sb = new StringBuilder();
      for (int[] p : cells) sb.append(p[0]).append(',').append(p[1]).append(';');
      return sb.toString();
    }

    private static int[][] copy3x3(int[][] a) {
      int[][] b = new int[3][3];
      for (int y = 0; y < 3; y++) System.arraycopy(a[y], 0, b[y], 0, 3);
      return b;
    }

    // Rotate 90° clockwise
    private static int[][] rot90(int[][] a) {
      int[][] b = new int[3][3];
      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          b[x][2 - y] = a[y][x];
        }
      }
      return b;
    }

    // Horizontal flip (mirror left-right)
    private static int[][] flipH(int[][] a) {
      int[][] b = new int[3][3];
      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          b[y][2 - x] = a[y][x];
        }
      }
      return b;
    }

    /** Just the number of blocks (#) in a 3x3. */
    public static int blockCount3x3(int[][] shape) {
      int c = 0;
      for (int y = 0; y < 3; y++)
        for (int x = 0; x < 3; x++)
          if (shape[y][x] == 1) c++;
      return c;
    }
  }

  public static final class DlxMatrixBuilder {

    /** Sparse DLX input: each row is a list of column indices that are 1. */
    public record SparseSpec(int colCount, int primaryCols, int[][] rowsOnes) {}

    /**
     * Build SPARSE DLX rows for one region:
     * - Primary columns: one per required piece instance (sum of counts)
     * - Secondary columns: one per board cell (W*H)
     *
     * variantsByShape.get(s) => List<int[][]> variants, each variant is int[k][2] occupied cells (dx,dy)
     */
    public static SparseSpec buildSparse(
        int W,
        int H,
        int[] counts6,
        List<List<int[][]>> variantsByShape,
        int[] blocksPerShape // size 6
    ) {
      // Expand counts into instance -> shapeId
      List<Integer> instances = new ArrayList<>();
      for (int shapeId = 0; shapeId < 6; shapeId++) {
        for (int k = 0; k < counts6[shapeId]; k++) instances.add(shapeId);
      }

      int P = instances.size();
      int cells = W * H;
      int colCount = P + cells;

      // Area prune
      int requiredArea = 0;
      for (int s = 0; s < 6; s++) requiredArea += counts6[s] * blocksPerShape[s];
      if (requiredArea > cells) {
        return new SparseSpec(colCount, P, new int[0][]); // impossible
      }

      List<int[]> rows = new ArrayList<>();

      for (int inst = 0; inst < P; inst++) {
        int shapeId = instances.get(inst);

        for (int[][] variantCells : variantsByShape.get(shapeId)) {

          int maxDx = 0, maxDy = 0;
          for (int[] p : variantCells) {
            if (p[0] > maxDx) maxDx = p[0];
            if (p[1] > maxDy) maxDy = p[1];
          }

          // anchor ranges so variant fits
          for (int ay = 0; ay <= H - 1 - maxDy; ay++) {
            for (int ax = 0; ax <= W - 1 - maxDx; ax++) {

              // Sparse row: [pieceInstanceCol] + [cell columns occupied]
              int onesLen = 1 + variantCells.length;
              int[] ones = new int[onesLen];
              ones[0] = inst; // primary piece-instance column

              int t = 1;
              for (int[] p : variantCells) {
                int x = ax + p[0];
                int y = ay + p[1];
                int cellIndex = y * W + x;
                ones[t++] = P + cellIndex; // secondary cell column
              }

              rows.add(ones);
            }
          }
        }
      }

      int[][] rowsOnes = rows.toArray(new int[0][]);
      return new SparseSpec(colCount, P, rowsOnes);
    }
  }
}
