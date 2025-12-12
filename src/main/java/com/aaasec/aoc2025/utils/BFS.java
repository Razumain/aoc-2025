package com.aaasec.aoc2025.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class BFS {

  // Best target found (if any) after search
  protected BFSTarget target;

  // All possible options (buttons, moves, etc.)
  protected List<? extends BFSOption> options;

  public BFS(final List<? extends BFSOption> options, final BFSTarget target) {
    this.options = options;
    this.target = target;
    init();  // run the search immediately
  }

  private void init() {
    // Start with "infinite" best path
    int initialBest = Integer.MAX_VALUE;
    BFSObject root = new BFSObject(initialBest, 0, options, target);
    root.search();  // run recursive search

    // If we found any SUCCESS, root.bestTarget will be non-null
    if (root.bestTarget != null) {
      this.target = root.bestTarget;
    }
  }

  // --- State classification for a BFSTarget ---

  public enum BFSState {
    // Target is busted / invalid
    ERROR,
    // Target configuration is a valid goal
    SUCCESS,
    // Still legal, but not yet a goal
    INCOMPLETE,
  }

  // --- Interfaces for problem-specific parts ---

  public interface BFSTarget {
    /**
     * Apply the given option to this target and
     * return a NEW target (do not mutate the original).
     */
    BFSTarget apply(BFSOption option);

    /**
     * Classify this state: ERROR, SUCCESS, or INCOMPLETE.
     */
    BFSState getState();

    /**
     * Return the list of options that were applied
     * from the root to reach this state.
     */
    List<? extends BFSOption> getAppliedOptions();
  }

  public interface BFSOption {
  }

  // --- Inner search node ---

  @Data
  public class BFSObject {

    int depth;
    BFSTarget startTarget;
    List<? extends BFSOption> allOptions;

    int bestpathLen;       // best path length found so far
    BFSTarget bestTarget;  // corresponding best target state
    Map<BFSTarget, Integer> seen = new HashMap<>();

    public BFSObject(final int bestpathLen,
        final int depth,
        final List<? extends BFSOption> options,
        final BFSTarget target) {
      this.bestpathLen = bestpathLen;
      this.depth = depth;
      this.allOptions = options;
      this.startTarget = target;
      this.bestTarget = null;
    }

    /**
     * Entry point: recursively search from startTarget.
     */
    public void search() {
      dfs(startTarget, depth);
    }

    /**
     * Depth-first search with branch-and-bound pruning.
     */
    private void dfs(BFSTarget currentTarget, int currentDepth) {
      if (currentDepth >= bestpathLen) {
        return; // branch-and-bound prune
      }

      // NEW: cycle and dominated-state prune
      Integer seenDepth = seen.get(currentTarget);
      if (seenDepth != null && seenDepth <= currentDepth) {
        // We've been here at an equal or better depth, no point exploring again
        return;
      }
      seen.put(currentTarget, currentDepth);

      BFSState state = currentTarget.getState();
      switch (state) {
      case ERROR -> {
        return;
      }
      case SUCCESS -> {
        int pathLen = currentTarget.getAppliedOptions().size();
        if (pathLen < bestpathLen) {
          bestpathLen = pathLen;
          bestTarget = currentTarget;
        }
        return;
      }
      case INCOMPLETE -> {
        List<BFSOption> blocked = new ArrayList<>();
        while (true) {
          BFSOption opt = getBestOptionChoice(allOptions, blocked, currentTarget);
          if (opt == null) break;
          blocked.add(opt);

          BFSTarget next = currentTarget.apply(opt);
          dfs(next, currentDepth + 1);
        }
      }
      }
    }
  }

  /**
   * Problem-specific option ordering:
   * choose the next best option that is NOT in blockedOptions,
   * given the current target.
   */
  protected abstract BFSOption getBestOptionChoice(
      final List<? extends BFSOption> options,
      final List<? extends BFSOption> blockedOptions,
      final BFSTarget target
  );
}
