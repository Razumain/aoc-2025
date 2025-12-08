package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;
import com.aaasec.aoc2025.utils.DisjointSet;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This was a tricky problem. I made some detailed notes to self to remember the principles of the solution
 * Key to the success was effective sorting and prioritization of shortest connections as well
 * as the simple tree organization solution used to group connections with connected nodes.
 */
public class Day8Solution extends Solution {
  public Day8Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {

    List<Point3D> points = new ArrayList<>();
    for (String line : input) {
      String[] split = line.split(",");
      points.add(new Point3D(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
    }

    int pointCount = points.size();
    log.add("Total number of points: %d", pointCount);
    int maxConnectionCount = pointCount * (pointCount - 1) / 2;
    log.add("Max number of connections: %d", maxConnectionCount);
    // Select 1000 for personal input and 10 for test data
    int selectedMaxConnectionCount = 1000;
    int targetSize = Math.min(maxConnectionCount, selectedMaxConnectionCount);
    log.add("Selected max number of connections: %d", targetSize);

    final List<Connection> shortestConnections = getShortestDistances(points, targetSize);
    DisjointSet disjointSet = new DisjointSet(pointCount);
    for (Connection connection : shortestConnections) {
      disjointSet.combineSets(connection.firstIndex, connection.secondIndex);
    }

    List<Integer> sizes =
        disjointSet.getSetSizes().stream().sorted(Collections.reverseOrder()).toList();
    long total = (long) sizes.get(0) * sizes.get(1) * sizes.get(2);
    log.add("Largest group sizes are %d, %d, and %d", sizes.get(0), sizes.get(1), sizes.get(2));
    log.add("Result part 1 (size 1 * size 2 * size 3): %d", total);
  }

  @Override
  public void solvePart2(List<String> input) {
    List<Point3D> points = new ArrayList<>();
    for (String line : input) {
      String[] split = line.split(",");
      points.add(new Point3D(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
    }

    int pointCount = points.size();
    log.add("Total number of points: %d", pointCount);
    int maxConnectionCount = pointCount * (pointCount - 1) / 2;
    log.add("Max number of connections: %d", maxConnectionCount);
    // Select 10000 for personal input and 100 for test data
    int selectedMaxConnectionCount = 10000;
    int targetSize = Math.min(maxConnectionCount, selectedMaxConnectionCount);
    log.add("Selected max number of connections: %d", targetSize);

    final List<Connection> shortestConnections = getShortestDistances(points, targetSize);
    DisjointSet disjointSet = new DisjointSet(pointCount);

    Connection lastConnection = null;
    int lastRootNodeCount = pointCount + 1;
    boolean success = false;
    for (int i = 0; i < shortestConnections.size(); i++) {
      Connection connection = shortestConnections.get(i);
      // Bind the indexes together for each connection
      lastConnection = connection;
      disjointSet.combineSets(connection.firstIndex, connection.secondIndex);
      List<Integer> rootNodes = disjointSet.getRootNodes();
      if (rootNodes.size() < lastRootNodeCount && rootNodes.size() < 10) {
        log.add("Root node count %d at connection %d", rootNodes.size(), i + 1);
      }
      if (rootNodes.size() == 1) {
        // All nodes belong to the same group
        success = true;
        break;
      }
      lastRootNodeCount = rootNodes.size();
    }
    if (!success) {
      throw new IllegalStateException("Could not find a solution for part 2");
    }

    Point3D firstPoint = points.get(lastConnection.firstIndex);
    Point3D secondPoint = points.get(lastConnection.secondIndex);
    BigInteger xProduct = BigInteger.valueOf(firstPoint.x).multiply(BigInteger.valueOf(secondPoint.x));

    log.add("Result part 2 (Last connecting nodes x1 * x2): %s", xProduct.toString());
  }

  public List<Connection> getShortestDistances(List<Point3D> points, int targetSize) {
    int totalPoints = points.size();

    // Priority queue putting the longest distance on top
    PriorityQueue<Connection> priorityQueue =
        new PriorityQueue<>(targetSize, (a, b) -> Long.compare(b.distSq, a.distSq));

    // run through all point combinations and find the targetSize shortest connections
    for (int i = 0; i < totalPoints; i++) {
      Point3D a = points.get(i);
      for (int j = i + 1; j < totalPoints; j++) {
        Point3D b = points.get(j);

        // Euclidean distance is sqrt(xDist^2 + yDist^2 + zDist^2). We are fine with comparing the on SQRT value (distSq)
        // This keeps things simpler and lets us use integer values
        long dx = a.x - b.x;
        long dy = a.y - b.y;
        long dz = a.z - b.z;
        long distSq = dx * dx + dy * dy + dz * dz;

        if (priorityQueue.size() < targetSize) {
          // We have not filled the candidate spaces yet. Just add.
          priorityQueue.offer(new Connection(i, j, distSq));
        }
        else if (distSq < priorityQueue.peek().distSq) {
          // We have the requested number of candidates, but this new onw was better than the worst on the queue
          // Pull the worst candidate from the queue
          priorityQueue.poll();
          // Add our replacement
          priorityQueue.offer(new Connection(i, j, distSq));
        }
      }
    }

    // Sort shortest connections
    List<Connection> result = new ArrayList<>(priorityQueue);
    result.sort(Comparator.comparingLong(e -> e.distSq));
    return result;
  }

  /**
   * Representing a connection point in the 3d space
   * @param x x coord
   * @param y y coord
   * @param z z coord
   */
  record Point3D (
      int x, int y, int z
  ){}

  /**
   * Represents a connection between two nodes
   */
  static class Connection {
    final int firstIndex;
    final int secondIndex;
    final long distSq;

    /**
     * Constructor
     * @param firstIndex the index of the first point in a list of points
     * @param secondIndex the index of the second point in a list of points
     * @param distSq the squared distance between the points in 3d space
     */
    Connection(int firstIndex, int secondIndex, long distSq) {
      this.firstIndex = firstIndex;
      this.secondIndex = secondIndex;
      this.distSq = distSq;
    }
  }

}

