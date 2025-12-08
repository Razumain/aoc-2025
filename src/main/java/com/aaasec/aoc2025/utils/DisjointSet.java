package com.aaasec.aoc2025.utils;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DisjointSet class implements a data structure for tracking a set of elements partitioned into disjoint subsets.
 * It provides efficient operations for union and find through the use of path compression and union by size.
 * This structure is commonly used in graph-related problems.
 */
public class DisjointSet {

  Node[] nodes;

  public DisjointSet(int numberOfNodes) {
    this.nodes = new Node[numberOfNodes];
    // register each node as its own group of size 1
    for (int i = 0; i < numberOfNodes; i++) {
      nodes[i] = new Node(i, i, 1);
    }
  }

  /**
   * Finds the representative element (or root) of the set containing the given node. This method uses path
   * compression to optimize future calls by making nodes point directly to the root.
   *
   * @param nodeIdx the index of the node to find the representative of
   * @return the index of the representative (root) of the set containing the node
   */
  public Node findRoot(int nodeIdx) {
    Node node = nodes[nodeIdx];
    if (node.parent != nodeIdx) {
      Node root = findRoot(node.parent);
      node.parent = root.index;   // path compression
      return root;
    } else {
      return node;
    }
  }

  /**
   * Merges the sets containing two specified elements into a single set. If the two elements are already in the same
   * set, no action is performed. Uses union by size strategy to attach the smaller set to the larger set to keep the
   * structure efficient.
   *
   * @param firstIndex the first element
   * @param secondIndex the second element
   */
  public void combineSets(int firstIndex, int secondIndex) {
    Node firstRoot = findRoot(firstIndex);
    Node secondRoot = findRoot(secondIndex);
    if (firstRoot.index == secondRoot.index) {
      // nodes are already in the same group
      return;
    }

    Node smallestGroupRoot;
    Node largestGroupRoot;

    if (firstRoot.size < secondRoot.size) {
      smallestGroupRoot = firstRoot;
      largestGroupRoot = secondRoot;
    } else {
      smallestGroupRoot = secondRoot;
      largestGroupRoot = firstRoot;
    }

    // Join sets (add the smallest set to the largest set)
    nodes[smallestGroupRoot.index].parent = largestGroupRoot.index;
    nodes[largestGroupRoot.index].size += nodes[smallestGroupRoot.index].size;
  }

  public List<Integer> getSetSizes() {
    List<Integer> sizes = new ArrayList<>();
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].parent == i) {
        sizes.add(nodes[i].size);
      }
    }
    return sizes;
  }


  /**
   * Determines the size of the largest set in the current data structure.
   *
   * @return the size of the largest set
   */
  public int getBiggestSetSize() {
    int max = 0;
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].parent == i && nodes[i].size > max) {
        max = nodes[i].size;
      }
    }
    return max;
  }

  public List<Integer> getRootNodes() {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < nodes.length; i++) {
      if (findRoot(nodes[i].index).index == i) {
        result.add(i);
      }
    }
    return result;
  }

  /**
   * Retrieves the disjoint sets formed by the nodes in the structure.
   * Each set is represented as a mapping from the representative element (or root) of the set
   * to a list of nodes that belong to that set.
   *
   * @return a map where the key is the root node of a set, and the value is a list of nodes belonging to that set
   */
  public Map<Integer, List<Node>> getDisjointSets() {
    Map<Integer, List<Node>> result = new HashMap<>();

    for (Node node : nodes) {
      Node root = findRoot(node.index);  // also does path compression
      result.computeIfAbsent(root.index, k -> new ArrayList<>())
          .add(node);
    }

    return result;
  }

  @AllArgsConstructor
  public final class Node{
    // The immediate parent node index of this node
    public int parent;
    // The index of this node
    public int index;
    // The count of this node and subordinate nodes that chain to this node
    public int size;
  }
}
