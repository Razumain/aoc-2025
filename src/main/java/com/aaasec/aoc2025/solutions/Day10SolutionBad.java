package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;
import com.aaasec.aoc2025.utils.BFS;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Day10SolutionBad extends Solution {
  public Day10SolutionBad(final int day, final String title, final File inputFile) throws IOException {
    super(day, title, inputFile);
  }

  @Override
  public void solvePart1(List<String> input) {

    Setup[] setups = new Setup[input.size()];
    for (int i = 0; i < input.size(); i++) {
      String line = input.get(i);
      final String[] split = line.split("\\s+");
      boolean[] indicators = getIndicators(split[0]);
      List<Integer>[] buttons = getButtons(split);
      List<Integer> joltages = getJoltages(split[split.length-1]);
      setups[i] = new Setup(indicators, buttons, joltages);
    }

    int minPressTotals = 0;
    for (Setup setup : setups) {
      int minPresses = minPressesForSetup(setup);
      minPressTotals += minPresses;
    }

    String result = "";
    log.add("Result part 1: %s", minPressTotals);
  }

  public int minPressesForSetup(Setup setup) {
    boolean[] target  = setup.indicators;
    List<Integer>[] buttons = setup.buttons;

    int numButtons = buttons.length;
    int numLights  = target.length;
    List<Integer> winningCombo = new ArrayList<>();

    boolean[] lights;

    int minPresses = Integer.MAX_VALUE;
    PressGenerator gen = new PressGenerator(numButtons);
    while (gen.hasNext()) {
      List<Integer> combo = gen.next();
      if (combo == null) break;
      if (combo.size() > minPresses) {
        continue;
      }
      lights = new boolean[numLights];
      for (int buttonIdx : combo) {
        applyButton(buttons[buttonIdx], lights);
      }
      if (matches(lights, target)) {
        minPresses = Math.min(minPresses, combo.size());
        winningCombo = combo;
      }
    }
    log.add("Winning combo: %s - presses: %d", winningCombo.stream().map(String::valueOf).toList(), minPresses);
    return minPresses;
  }

  public static class PressGenerator {
    int numButtons;
    int depth;            // current combination size (number of buttons pressed)
    int[] buttonPresses;  // holds indices of current combination
    boolean done;

    public PressGenerator(final int numButtons) {
      this.numButtons = numButtons;
      this.depth = 0;   // start at 0; first call to next() will move to depth = 1
    }

    boolean hasNext() {
      return !done;
    }

    List<Integer> next() {
      if (done) {
        return null;
      }

      while (true) {
        List<Integer> nextCombo = getNext();
        if (nextCombo != null) {
          return nextCombo;
        }

        // No more combinations of this depth; increase depth
        depth++;
        buttonPresses = null; // force reset for new depth

        if (depth > numButtons) {
          this.done = true;
          return null;
        }
        // Loop again: getNext() will now initialize first combination of new depth
      }
    }

    private List<Integer> getNext() {
      if (depth == 0) {
        return null;
      }

      if (buttonPresses == null) {
        buttonPresses = new int[depth];
        for (int i = 0; i < depth; i++) {
          buttonPresses[i] = i;
        }
        return toList(buttonPresses, depth);
      }

      // Generate next combination of size = depth in lexicographic order
      int k = depth;
      int n = numButtons;

      int i = k - 1;
      while (i >= 0 && buttonPresses[i] == n - k + i) {
        i--;
      }

      if (i < 0) {
        return null;
      }
      buttonPresses[i]++;
      // Reset the following positions
      for (int j = i + 1; j < k; j++) {
        buttonPresses[j] = buttonPresses[j - 1] + 1;
      }

      return toList(buttonPresses, depth);
    }

    private static List<Integer> toList(int[] arr, int len) {
      List<Integer> out = new ArrayList<>(len);
      for (int i = 0; i < len; i++) {
        out.add(arr[i]);
      }
      return out;
    }
  }


  @Override
  public void solvePart2(List<String> input) {
    Setup[] setups = new Setup[input.size()];
    for (int i = 0; i < input.size(); i++) {
      String line = input.get(i);
      final String[] split = line.split("\\s+");
      boolean[] indicators = getIndicators(split[0]);
      List<Integer>[] buttons = getButtons(split);
      List<Integer> joltages = getJoltages(split[split.length-1]);
      setups[i] = new Setup(indicators, buttons, joltages);
    }

    int totalPressed = 0;
    int idx = 0;
    for (Setup setup : setups) {
      System.out.println("Processing setup " + idx++);
      final List<Integer>[] buttons = setup.buttons;
      List<JoltageBFSOption> options = new ArrayList<>();
      for (List<Integer> button : buttons) {
        options.add(new JoltageBFSOption(button));
      }
      JoltageBFSTarget target = new JoltageBFSTarget(new ArrayList<>(), getEmptyJoltages(setup.joltages.size()), setup.joltages);
      JoltageBFS bfs = new JoltageBFS(options, target);
      final BFS.BFSTarget result = bfs.getTarget();
      if (result.getState() != BFS.BFSState.SUCCESS) {
        log.add("Setup %d failed: %s", idx, result.getState());
      } else {
        int minPressedForJoltage = result.getAppliedOptions().size();
        totalPressed += minPressedForJoltage;
      }
    }

    String result = "";
    log.add("Result part 2: %s", totalPressed);
  }

  private List<Integer> getEmptyJoltages(final int size) {
    List<Integer> joltage = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      joltage.add(0);
    }
    return joltage;
  }

  public static class JoltageBFS extends BFS{

    public JoltageBFS(final List<JoltageBFSOption> options, final BFSTarget target) {
      super(options, target);
    }

    @Override
    protected BFSOption getBestOptionChoice(final List<? extends BFSOption> options, final List<? extends BFSOption> blockedOptions,
        final BFSTarget target) {
      return options.stream()
          .filter(o -> !blockedOptions.contains(o))  // Exclude blocked options (optional if not needed)
          .max(Comparator.comparingInt(o -> ((JoltageBFSOption) o).button.size()))  // Changed to max for largest size
          .orElse(null);
    }
  }

  @AllArgsConstructor
  @EqualsAndHashCode(onlyExplicitlyIncluded = true)
  public static class JoltageBFSTarget implements BFS.BFSTarget {

    @EqualsAndHashCode.Include
    List<JoltageBFSOption> appliedOptions;
    @EqualsAndHashCode.Include
    List<Integer> joltage;
    @EqualsAndHashCode.Exclude
    List<Integer> targetJoltage;

    @Override
    public BFS.BFSTarget apply(final BFS.BFSOption option) {
      if (option instanceof JoltageBFSOption joltageBFSOption) {
        JoltageBFSTarget newTarget = new JoltageBFSTarget(new ArrayList<>(appliedOptions), new ArrayList<>(joltage), targetJoltage);
        applyButtonToJoltage(joltageBFSOption.button, newTarget.joltage);
        newTarget.appliedOptions.add(joltageBFSOption);
        return newTarget;
      }
      throw new IllegalArgumentException("Unexpected option type: " + option.getClass().getSimpleName());
    }

    @Override
    public BFS.BFSState getState() {
      int state = jotageMatche(joltage, targetJoltage);
      return switch (state) {
        case -1 -> BFS.BFSState.INCOMPLETE;
        case 0 -> BFS.BFSState.SUCCESS;
        case 1 -> BFS.BFSState.ERROR;
        default -> throw new IllegalStateException("Unexpected value: " + state);
      };
    }

    @Override
    public List<? extends BFS.BFSOption> getAppliedOptions() {
      return appliedOptions;
    }
  }

  @AllArgsConstructor
  public static class JoltageBFSOption implements BFS.BFSOption {
    public List<Integer> button;
  }




  // Generic


  public record Setup(
      boolean[] indicators,
      List<Integer>[] buttons,
      List<Integer> joltages
  ){}

  private static void applyButton(List<Integer> button, boolean[] lights) {
    for (int lightIndex : button) {
      lights[lightIndex] = !lights[lightIndex];
    }
  }

  private static void applyButtonToJoltage(List<Integer> button, List<Integer> joltage) {
    for (int lightIndex : button) {
      joltage.set(lightIndex, joltage.get(lightIndex) + 1);
    }
  }

  private static boolean matches(boolean[] lights, boolean[] target) {
    if (lights.length != target.length) return false;
    for (int i = 0; i < lights.length; i++) {
      if (lights[i] != target[i]) {
        return false;
      }
    }
    return true;
  }

  private static int jotageMatche(List<Integer> joltage, List<Integer> target) {
    if (joltage.size() != target.size()) return 1;
    for (int i = 0; i < joltage.size(); i++) {
      if (joltage.get(i) > target.get(i)) {
        // Busted
        return 1;
      }
    }
    for (int i = 0; i < joltage.size(); i++) {
      if (!Objects.equals(joltage.get(i), target.get(i))) {
        // Incomplete
        return -1;
      }
    }
    // Match
    return 0;
  }




  private List<Integer>[] getButtons(final String[] split) {
    List[] buttons = new List[split.length-2];
    for (int i = 1; i < split.length-1; i++) {
      String s = split[i];
      s = s.substring(1, s.length()-1);
      final String[] numbers = s.split(",");
      List<Integer> button = new ArrayList<>();
      for (int j = 0; j < numbers.length; j++) {
        button.add(Integer.parseInt(numbers[j]));
      }
      buttons[i-1] = button;
    }
    return buttons;
  }

  private boolean[] getIndicators(final String s) {
    boolean[] indicators = new boolean[s.length()-2];
    for (int i = 1; i < s.length()-1; i++) {
      if (s.charAt(i) == '#') {
        indicators[i-1] = true;
      } else {
        indicators[i-1] = false;
      }
    }
    return indicators;
  }

  private List<Integer> getJoltages(final String joltageInfo) {
    String values = joltageInfo.substring(1, joltageInfo.length()-1);
    final String[] numbers = values.split(",");
    List<Integer> joltages = new ArrayList<>();
    for (int i = 0; i < numbers.length; i++) {
      joltages.add(Integer.parseInt(numbers[i]));
    }
    return joltages;
  }




  /*  public static int minPressesForSetup2(Setup setup) {
    boolean[] target  = setup.indicators;
    List<Integer>[] buttons = setup.buttons;

    int numButtons = buttons.length;
    int numLights  = target.length;

    boolean[] lights = new boolean[numLights];

    int best = dfsButtons(0, buttons, lights, target, 0, Integer.MAX_VALUE);
    return (best == Integer.MAX_VALUE) ? -1 : best;
  }

  private static int dfsButtons(
      int idx,
      List<Integer>[] buttons,
      boolean[] lights,
      boolean[] target,
      int pressesSoFar,
      int bestSoFar
  ) {
    int n = buttons.length;

    // Prune: if we've already pressed more than best, no need to continue.
    if (pressesSoFar >= bestSoFar) {
      return bestSoFar;
    }

    // Base case: all buttons decided
    if (idx == n) {
      if (matches(lights, target)) {
        return Math.min(bestSoFar, pressesSoFar);
      } else {
        return bestSoFar;
      }
    }

    // Option 0: press this button 0 times
    bestSoFar = dfsButtons(idx + 1, buttons, lights, target, pressesSoFar, bestSoFar);

    // Option 1: press this button 1 time
    applyButton(buttons[idx], lights);  // toggle its lights once
    bestSoFar = dfsButtons(idx + 1, buttons, lights, target, pressesSoFar + 1, bestSoFar);
    applyButton(buttons[idx], lights);  // undo (toggle back)

    // Option 2: press this button 2 times
    // Two toggles = original state, but cost +2.
    // This only matters if your puzzle really distinguishes "pressed twice".
    int candidate = pressesSoFar + 2;
    if (candidate < bestSoFar) {
      // pressing twice doesn't change lights, so we don't need to re-apply:
      bestSoFar = dfsButtons(idx + 1, buttons, lights, target, candidate, bestSoFar);
    }

    return bestSoFar;
  }*/



  /*  public static int minPressesForJoltage(List<Integer> targetJoltage, List<Integer>[] buttons) {
    int numButtons = buttons.length;
    int numCounters = targetJoltage.size();

    // Target as int[]
    int[] target = new int[numCounters];
    for (int i = 0; i < numCounters; i++) {
      target[i] = targetJoltage.get(i);
    }

    // Initial state: all counters = 0
    int[] start = new int[numCounters];

    // Quick check: already at target?
    if (Arrays.equals(start, target)) {
      return 0;
    }

    // BFS queue: each node holds (currentLevels, pressesSoFar)
    record State(int[] levels, int presses) {}

    ArrayDeque<State> queue = new ArrayDeque<>();
    Set<String> visited = new HashSet<>();

    queue.add(new State(start, 0));
    visited.add(encodeState(start));

    while (!queue.isEmpty()) {
      State current = queue.poll();
      int[] levels = current.levels;
      int pressesSoFar = current.presses;

      // Try pressing each button once from this state
      for (int b = 0; b < numButtons; b++) {
        int[] next = levels.clone();

        // Apply button b: increment listed counters
        boolean overshoot = false;
        for (int idx : buttons[b]) {
          next[idx]++;
          if (next[idx] > target[idx]) {
            overshoot = true;
            break;
          }
        }
        if (overshoot) {
          continue; // can't exceed target in any counter
        }

        if (Arrays.equals(next, target)) {
          // First time we reach target -> minimal presses
          return pressesSoFar + 1;
        }

        String key = encodeState(next);
        if (!visited.contains(key)) {
          visited.add(key);
          queue.add(new State(next, pressesSoFar + 1));
        }
      }
    }

    // No way to reach target
    return -1;
  }

  // Encode a state (joltage vector) as a string for the visited set.
  private static String encodeState(int[] levels) {
    // You can optimize this if needed, but this is simple and safe.
    StringBuilder sb = new StringBuilder(levels.length * 3);
    for (int v : levels) {
      sb.append(v).append(',');
    }
    return sb.toString();
  }*/

}
