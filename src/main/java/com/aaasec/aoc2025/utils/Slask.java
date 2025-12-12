package com.aaasec.aoc2025.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Slask {

  public static List<Path> getAllPaths(final String startId, final String endId, final Map<String, List<String>> connectionsMap) {

    List<Path> pathList = new ArrayList<>();
    final List<String> initCons = connectionsMap.get(startId);
    for (String con : initCons) {
      Path path = new Path();
      List<String> nodes = new ArrayList<>();
      nodes.add(startId);
      nodes.add(con);
      path.nodes = nodes;
      path.start = startId;
      path.end = con;
      pathList.add(path);
    }

    boolean done = false;
    while (!done) {
      boolean newNodes = false;
      List<Path> newPaths = new ArrayList<>();
      for(Path path : pathList) {
        final String last = path.nodes.getLast();
        final List<String> nextNodes = connectionsMap.get(last);
        boolean extendedCurrentPath = false;
        for (final String nextNode : nextNodes) {
          List<String> currentPathNodes = path.nodes;
          if (nextNode.equals(endId)) {
            path.nodes.add(nextNode);
            path.end = nextNode;
            // This path is done. It reached out and need no more paths
            break;
          }
          if (path.nodes.contains(nextNode)) {
            // This node already exist. Skip
            continue;
          }
          if (!extendedCurrentPath) {
            path.nodes.add(nextNode);
            path.end = nextNode;
            extendedCurrentPath = true;
            newNodes = true;
            continue;
          }
          Path newPath = path.clone();
          newPath.nodes.add(nextNode);
          newPath.end = nextNode;
          newPaths.add(newPath);
          newNodes = true;
        }
      }
      pathList.addAll(newPaths);
      System.out.println(String.format("Pathcount %s",pathList.size()) );
      done = !newNodes;
    }
    return pathList.stream().filter(p -> p.end.equals(endId)).toList();

  }

  public static class Path{
    public List<String> nodes;
    public String start;
    public String end;

    public Path clone() {
      Path newPath = new Path();
      newPath.nodes = new ArrayList<>(nodes);
      newPath.start = start;
      newPath.end = end;
      return newPath;
    }

  }


}
