package com.aaasec.aoc2025.utils.dlx;

import java.util.List; /**
 * Use this to answer the question "does at least one solution exist?"
 * It stops the DLX search immediately when the first solution is found.
 */
public class ExistsSolutionHandler implements SolutionHandler {

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
