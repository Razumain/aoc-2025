package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class Day9Solution extends Solution {
  public Day9Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {

    List<Corner> corners = new ArrayList<>();
    for (String line : input) {
      String[] split = line.split(",");
      Corner c = new Corner(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
      corners.add(c);
    }

    long maxArea = 0;
    for (int i = 0; i < corners.size(); i++) {
      for (int j = i+1; j < corners.size(); j++) {
        Corner c1 = corners.get(i);
        Corner c2 = corners.get(j);
        long x = Math.abs (c1.x - c2.x)+1;
        long y = Math.abs (c1.y - c2.y)+1;
        long area = x * y;
        //log.add("Area between (%d, %d) and (%d, %d) = %d", c1.x, c1.y, c2.x, c2.y , area);
        maxArea = Math.max(maxArea, area);
      }
    }
    log.add("Max area: %s", maxArea);
  }




  @Override
  public void solvePart2(List<String> input) {
    // 1. Parse corners in loop order
    List<Corner> corners = new ArrayList<>();
    for (String line : input) {
      String[] split = line.split(",");
      long x = Long.parseLong(split[0].trim());
      long y = Long.parseLong(split[1].trim());
      corners.add(new Corner(x, y));
    }
    int n = corners.size();

    // 2. Build xStops and yStops (unique sorted)
    TreeSet<Long> xSet = new TreeSet<>();
    TreeSet<Long> ySet = new TreeSet<>();
    for (Corner c : corners) {
      xSet.add(c.x());
      ySet.add(c.y());
    }


    // My collections here
    Map<Long, List<Corner>> xMap = new HashMap<>();
    Map<Long, List<Corner>> yMap = new HashMap<>();
    Map<Long, Span> yInteriorSpanByX = new HashMap<>();
    Map<Long, Span> xInteriorSpanByY = new HashMap<>();

    long yMinLine = -1;
    long yMaxLine = -1;
    long xMinLine = -1;
    long xMaxLine = -1;

    for (Long x : xSet) {
      for (int i = 0; i < n; i++) {
        Corner c = corners.get(i);
        if (c.x() == x) {
          xMap.computeIfAbsent(x, k -> new ArrayList<>()).add(c);
        }
      }
      if (xMap.get(x).size() == 2) {
        Span ySpan = getBoundary(yMinLine, yMaxLine, xMap.get(x).getFirst().y(), xMap.get(x).getLast().y());
        yMinLine = ySpan.min;
        yMaxLine = ySpan.max;
        yInteriorSpanByX.put(x, ySpan);
      } else {
        throw new RuntimeException("xMap size is not 2");
      }
    }
    for (Long y: ySet) {
      for (int j = 0; j < n; j++) {
        Corner c = corners.get(j);
        if (c.y() == y) {
          yMap.computeIfAbsent(y, k -> new ArrayList<>()).add(c);
        }
      }
      if (yMap.get(y).size() == 2) {
        Span xSpan = getBoundary(xMinLine, xMaxLine, yMap.get(y).getFirst().x(), yMap.get(y).getLast().x());
        xMinLine = xSpan.min;
        xMaxLine = xSpan.max;
        xInteriorSpanByY.put(y, xSpan);
      } else {
        throw new RuntimeException("yMap size is not 2");
      }
    }

    long maxArea = 0;
    for (int i = 0; i < n; i++) {
      for (int j = i+1; j < n; j++) {
        Corner c1 = corners.get(i);
        Corner c2 = corners.get(j);
        long x = Math.abs (c1.x - c2.x)+1;
        long y = Math.abs (c1.y - c2.y)+1;
        long area = x * y;
        if (area > maxArea) {
          if (boundaryTest(c1, c2, ySet, xInteriorSpanByY, xSet, yInteriorSpanByX)) {
            maxArea = area;
          }
        }
      }
    }

    log.add("MaxArea: %s", maxArea);
  }

  private Span getBoundary(long minLine, long maxLine, long edgeCornerVal1, long edgeCornerVal2) {
    if (minLine == -1 && maxLine == -1) {
      minLine = Math.min(edgeCornerVal1, edgeCornerVal2);
      maxLine = Math.max(edgeCornerVal1, edgeCornerVal2);
      return new Span(minLine, maxLine);
    }
    if (minLine + maxLine == edgeCornerVal1 + edgeCornerVal2) {
      return new Span(minLine, maxLine);
    }
    if (edgeCornerVal1 == minLine) {
      // Swap min line to the new corner val
      minLine = edgeCornerVal2;
      return new Span(minLine, maxLine);
    }
    if (edgeCornerVal2 == minLine) {
      minLine = edgeCornerVal1;
      return new Span(minLine, maxLine);
    }
    if (edgeCornerVal1 == maxLine) {
      maxLine = edgeCornerVal2;
      return new Span(minLine, maxLine);
    }
    if (edgeCornerVal2 == maxLine) {
      maxLine = edgeCornerVal1;
      return new Span(minLine, maxLine);
    }
    long minCv = Math.min(edgeCornerVal1, edgeCornerVal2);
    long maxCv = Math.max(edgeCornerVal1, edgeCornerVal2);
    return new Span(Math.min(minLine, minCv), Math.max(maxLine, maxCv));
  }

  private boolean boundaryTest(final Corner c1, final Corner c2, final TreeSet<Long> ySet, final Map<Long, Span> xInteriorSpanByY,
      final TreeSet<Long> xSet, final Map<Long, Span> yInteriorSpanByX) {

    long minX = Math.min(c1.x(), c2.x());
    long maxX = Math.max(c1.x(), c2.x());
    long minY = Math.min(c1.y(), c2.y());
    long maxY = Math.max(c1.y(), c2.y());

    for (Long y : ySet) {
      if (y < minY || y >= maxY) continue;
      Span xSpan = xInteriorSpanByY.get(y);
      if (!inXBoundary(xSpan, minX, maxX)) {
        return false;
      }
    }
    for (Long x : xSet) {
      if (x < minX || x >= maxX) continue;
      Span ySpan = yInteriorSpanByX.get(x);
      if (!inYBoundary(ySpan, minY, maxY)) {
        return false;
      }
    }
    return true;
  }

  private boolean inYBoundary(Span span, final long areaMinY, final long areaMaxY) {
    long minY = Math.min(span.min(), span.max());
    long maxY = Math.max(span.min(), span.max());
    return areaMinY >= minY && areaMaxY <= maxY;
  }

  private boolean inXBoundary(Span span, final long areaMinX, final long areaMaxX) {
    long minX = Math.min(span.min(),  span.max());
    long maxX = Math.max(span.min(),   span.max());
    return areaMinX >= minX && areaMaxX <= maxX;
  }

  public record Span(
      long min, long max
  ){}

  public record Corner(long x, long y) {}


/*
  public void solvePart2_2(List<String> input) {

    // 1. Parse corners in loop order
    List<Corner> corners = new ArrayList<>();
    for (String line : input) {
      String[] split = line.split(",");
      long x = Long.parseLong(split[0].trim());
      long y = Long.parseLong(split[1].trim());
      corners.add(new Corner(x, y));
    }
    int n = corners.size();

    // 2. Build xStops and yStops (unique sorted)
    TreeSet<Long> xSet = new TreeSet<>();
    TreeSet<Long> ySet = new TreeSet<>();
    for (Corner c : corners) {
      xSet.add(c.x());
      ySet.add(c.y());
    }
    long[] xStops = xSet.stream().mapToLong(Long::longValue).toArray();
    long[] yStops = ySet.stream().mapToLong(Long::longValue).toArray();

    int xStopCount = xStops.length;
    int yStopCount = yStops.length;

    int xZoneCount = xStopCount - 1;
    int yZoneCount = yStopCount - 1;

    // 3. Maps from coordinate to stop index
    Map<Long, Integer> xIndex = new HashMap<>();
    Map<Long, Integer> yIndex = new HashMap<>();
    for (int i = 0; i < xStopCount; i++) {
      xIndex.put(xStops[i], i);
    }
    for (int j = 0; j < yStopCount; j++) {
      yIndex.put(yStops[j], j);
    }

    // Pre-map each corner to stop indices
    int[] cornerXIdx = new int[n];
    int[] cornerYIdx = new int[n];
    for (int i = 0; i < n; i++) {
      Corner c = corners.get(i);
      cornerXIdx[i] = xIndex.get(c.x());
      cornerYIdx[i] = yIndex.get(c.y());
    }

    // 4. Build polygon edges (from corner i to i+1, wrapping)
    class Edge {
      long x1, y1, x2, y2;
      Edge(long x1, long y1, long x2, long y2) {
        this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
      }
      boolean isVertical() { return x1 == x2; }
      boolean isHorizontal() { return y1 == y2; }
    }

    List<Edge> edges = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      Corner a = corners.get(i);
      Corner b = corners.get((i + 1) % n);
      edges.add(new Edge(a.x(), a.y(), b.x(), b.y()));
    }

    // 5. Compute insideZone[xZone][yZone]
    boolean[][] insideZone = new boolean[xZoneCount][yZoneCount];

    for (int yi = 0; yi < yZoneCount; yi++) {
      double yMid = (yStops[yi] + yStops[yi + 1]) / 2.0;

      // All x where a vertical edge crosses this horizontal line
      List<Double> crossings = new ArrayList<>();
      for (Edge e : edges) {
        if (!e.isVertical()) continue;

        long minY = Math.min(e.y1, e.y2);
        long maxY = Math.max(e.y1, e.y2);

        // strict between minY and maxY so we don't hit vertices twice
        if (yMid > minY && yMid < maxY) {
          crossings.add((double) e.x1);
        }
      }

      Collections.sort(crossings);

      // For each zone in x, decide inside/outside based on crossings
      for (int xi = 0; xi < xZoneCount; xi++) {
        double xMid = (xStops[xi] + xStops[xi + 1]) / 2.0;

        // Count how many crossings are <= xMid
        int count = 0;
        for (double cx : crossings) {
          if (cx <= xMid) count++;
          else break;
        }

        // Ray casting: inside if count is odd
        insideZone[xi][yi] = (count % 2 == 1);
      }
    }

    // 6. Build 2D prefix sum over insideZone
    int[][] prefix = new int[xZoneCount + 1][yZoneCount + 1];
    for (int xi = 0; xi < xZoneCount; xi++) {
      for (int yi = 0; yi < yZoneCount; yi++) {
        int val = insideZone[xi][yi] ? 1 : 0;
        prefix[xi + 1][yi + 1] =
            prefix[xi][yi + 1]
                + prefix[xi + 1][yi]
                - prefix[xi][yi]
                + val;
      }
    }

    // Helper to query sum in rectangle of zones
    java.util.function.IntBinaryOperator zoneSum = (fromPacked, toPacked) -> {
      // pack (xZoneFrom,yZoneFrom) and (xZoneTo,yZoneTo) into ints if you want,
      // but simpler is to just write a small method. I'll inline a simple version instead.
      return 0; // placeholder, see method below
    };

    // I'll instead write a small method below and call it.
    // 7. Try all pairs of corners
    long maxArea = 0;
    for (int i = 0; i < n; i++) {
      Corner c1 = corners.get(i);
      int xIdx1 = cornerXIdx[i];
      int yIdx1 = cornerYIdx[i];
      for (int j = i + 1; j < n; j++) {
        Corner c2 = corners.get(j);
        int xIdx2 = cornerXIdx[j];
        int yIdx2 = cornerYIdx[j];

        if (c1.x() == c2.x() || c1.y() == c2.y()) {
          continue; // not opposite corners of a rectangle
        }

        int xFrom = Math.min(xIdx1, xIdx2);
        int xTo   = Math.max(xIdx1, xIdx2) - 1;
        int yFrom = Math.min(yIdx1, yIdx2);
        int yTo   = Math.max(yIdx1, yIdx2) - 1;

        int zoneWidth  = xTo - xFrom + 1;
        int zoneHeight = yTo - yFrom + 1;
        int totalZones = zoneWidth * zoneHeight;

        int insideCount = queryPrefix(prefix, xFrom, yFrom, xTo, yTo);
        if (insideCount != totalZones) continue; // rectangle not fully inside

        long width  = Math.abs(c1.x() - c2.x()) + 1;
        long height = Math.abs(c1.y() - c2.y()) + 1;
        long area   = width * height;
        if (area > maxArea) {
          maxArea = area;
        }
      }
    }

    log.add("Result part 2: %s", maxArea);
  }

  // Helper: sum over insideZone[xFrom..xTo][yFrom..yTo] using prefix.
  private int queryPrefix(int[][] prefix, int xFrom, int yFrom, int xTo, int yTo) {
    if (xFrom > xTo || yFrom > yTo) return 0;
    return prefix[xTo + 1][yTo + 1]
        - prefix[xFrom][yTo + 1]
        - prefix[xTo + 1][yFrom]
        + prefix[xFrom][yFrom];
  }

  public class square {
    Corner corner1;
    Corner corner2;
    long area;
  }
*/

}
