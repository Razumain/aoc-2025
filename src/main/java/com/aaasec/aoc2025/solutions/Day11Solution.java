package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day11Solution extends Solution {
  public Day11Solution(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {

    Map<String, List<String>> connections = parseConnectionGraphInput(input);
    List<String> sortedTopoList = topoSort(connections);
    Map<String, Long> fromSvr = countPathsFrom("you", connections, sortedTopoList);
    long youToOutConnections = fromSvr.getOrDefault("out", 0L);

    log.add("Result part 1: %s", youToOutConnections);
  }


  @Override
  public void solvePart2(List<String> input) {
    Map<String, List<String>> connections = parseConnectionGraphInput(input);
    List<String> sortedTopoList = topoSort(connections);

    Map<String, Long> fromSvr = countPathsFrom("svr", connections, sortedTopoList);
    Map<String, Long> fromDac = countPathsFrom("dac", connections, sortedTopoList);
    Map<String, Long> fromFft = countPathsFrom("fft", connections, sortedTopoList);

    long svrToDac = fromSvr.get("dac");
    long svrToFft = fromSvr.get("fft");
    long dacToFft = fromDac.get("fft");
    long dacToOut = fromDac.get("out");
    long fftToDac = fromFft.get("dac");
    long fftToOut = fromFft.get("out");

    // Paths: svr -> dac -> fft -> out
    long dacThenFft = svrToDac * dacToFft * fftToOut;
    // Paths: svr -> fft -> dac -> out
    long fftThenDac = svrToFft * fftToDac * dacToOut;
    long result = dacThenFft + fftThenDac;

    log.add("Result part 2: %d", result);
  }

  /**
   * Create a Topologically sorted list of connection nodes based on a connection map.
   * The connection map MUST provide a DAG (Dedicated Asyclic Graph) where all nodes can be ordered
   * so that there are no connections between a node and any node above it. All connections are forward in the list.
   *
   * @param connections A map of nodes and the other nodes it connects to as forward connections.
   * @return topologically sorted list of connected nodes
   */
  private List<String> topoSort(Map<String, List<String>> connections) {
    Map<String, Integer> inConnectCountMap = new HashMap<>();

    // Make in connection Map with all nodes as members and 0 in connection count
    for (Map.Entry<String, List<String>> entry : connections.entrySet()) {
      String from = entry.getKey();
      inConnectCountMap.putIfAbsent(from, 0);
      for (String to : entry.getValue()) {
        inConnectCountMap.putIfAbsent(to, 0);
      }
    }

    // Count incoming connections for each node
    for (Map.Entry<String, List<String>> entry : connections.entrySet()) {
      //List destinations for all connections
      for (String to : entry.getValue()) {
        // Add incoming connection to destination
        inConnectCountMap.put(to, inConnectCountMap.get(to) + 1);
      }
    }

    // Put id of all nodes with no incoming connections on stack (roots)
    ArrayDeque<String> stack = new ArrayDeque<>();
    for (Map.Entry<String, Integer> entry : inConnectCountMap.entrySet()) {
      if (entry.getValue() == 0) {
        stack.add(entry.getKey());
      }
    }

    // Kahn's algorithm
    // Create sorted list
    List<String> sortedTopoList = new ArrayList<>();
    while (!stack.isEmpty()) {
      // Pull a node that has no incoming connections from stack (current root)
      String root = stack.poll();
      // Save this as next item on topo list
      sortedTopoList.add(root);
      // As This is now on topo list, Reduce incoming connections from child nodes by 1
      for (String value : connections.getOrDefault(root, List.of())) {
        // Decrement incoming
        int inConnectCount = inConnectCountMap.get(value) - 1;
        inConnectCountMap.put(value, inConnectCount);
        if (inConnectCount == 0) {
          // No parents left. Push to stack of roots ready to add to the topo list
          stack.add(value);
        }
      }
    }

    if (sortedTopoList.size() != inConnectCountMap.size()) {
      // This is not a DAG. Abort
      throw new IllegalStateException("Graph has a cycle; can't do DP path counting safely.");
    }

    return sortedTopoList;
  }

  /**
   * Count paths from a specific start node to any end node
   * @param start start node
   * @param connections Connections map
   * @param sortedTopoList topologically sorted node list
   * @return Map for connection counts to each map item
   */
  private Map<String, Long> countPathsFrom(String start, Map<String, List<String>> connections, List<String> sortedTopoList
  ) {
    // Init path count to 0 for all nodes
    Map<String, Long> paths = new HashMap<>();
    for (String node : sortedTopoList) {
      paths.put(node, 0L);
    }
    // Set start node paths to 1
    if (paths.containsKey(start)) {
      paths.put(start, 1L);
    }

    for (String nextFromTopo : sortedTopoList) {
      long cnt = paths.get(nextFromTopo);
      if (cnt == 0L) {
        // No incoming connections to this node. Ignore
        continue;
      }
      for (String to : connections.getOrDefault(nextFromTopo, List.of())) {
        //Add parent incoming connections to child inc connections
        paths.put(to, paths.get(to) + cnt);
      }
    }
    return paths;
  }

  /**
   * Parse puzzle input data
   * @param input puzzle input
   * @return Structured connection map
   */
  private Map<String, List<String>> parseConnectionGraphInput(List<String> input) {
    Map<String, List<String>> graph = new HashMap<>();
    for (String line : input) {
      String[] split = line.split(" ");
      String id = line.substring(0, 3);
      List<String> dst = new ArrayList<>();
      for (int i = 1; i < split.length; i++) {
        dst.add(split[i]);
      }
      graph.put(id, dst);
    }
    return graph;
  }




}
