package com.aaasec.aoc2025.solutions;

import com.aaasec.aoc2025.solve.Solution;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day10Solution extends Solution {
  public Day10Solution(final int day, final String title, final File inputFile) throws IOException {
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
      int[] target = setup.joltages.stream().mapToInt(Integer::intValue).toArray();
      List<JoltageILPSolver.Button> buttons = new ArrayList<>();
      for (int i = 0; i < setup.buttons.length; i++) {
        buttons.add(new JoltageILPSolver.Button(setup.buttons[i]));
      }
      int presses = JoltageILPSolver.solveMachineWithOrTools(buttons, target);
      System.out.println(String.format("Setup %d: presses = %d", idx++, presses));
      log.add("Setup %d: presses = %d", idx, presses);
      if (presses != -1) {
        totalPressed += presses;
      } else {
        log.add("No solution for setup %d", idx);
      }
    }

    String result = "";
    log.add("Result part 2: %s", totalPressed);
  }


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

  private static boolean matches(boolean[] lights, boolean[] target) {
    if (lights.length != target.length) return false;
    for (int i = 0; i < lights.length; i++) {
      if (lights[i] != target[i]) {
        return false;
      }
    }
    return true;
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

  /**
   * ILP (Integer Linear Programming solver) provided by Google OR tools.
   * This is provided by their CP-SAT solver (Constratints Programming)
   * The modern flagship of Google OR-Tools
   * <p>
   * ILP is suitable here because it defines a problem solution model based on:
   * - Integer variables (buttons, joltage, increase counts)
   * - Linear contraints (matching joltage)
   * - Linear objective (minimize button push)
   * <p>
   * The code below sets up the solution model and feeds it into a CpSolver.
   * Then the solver does the rest. Nicely and timely, optimized on least button push.
   * <p>
   * An important strength of this ILP processor is that it prunes a large volume of meaningless test combinations
   */
  public class JoltageILPSolver {

    /**
     * The Button object holding the indexes of the joltage values that are increased on button push
     */
    public static final class Button {
      final int[] indices;

      public Button(List<Integer> indices) {
        this.indices = indices.stream().mapToInt(Integer::intValue).toArray();
      }
    }

    /**
     * Get the lowest number of button pushes that solves the problem
     * @param buttons buttons to use
     * @param target target joltage values
     * @return the lowest necessary button pushes
     */
    public static int solveMachineWithOrTools(List<Button> buttons, int[] target) {
      Loader.loadNativeLibraries();

      int nCounters = target.length;
      int nButtons = buttons.size();

      CpModel model = new CpModel();

      // x[j] is the decision variable per button used to track progress per button
      IntVar[] x = new IntVar[nButtons];
      int maxTarget = 0;
      for (int t : target) {
        if (t > maxTarget) maxTarget = t;
      }
      // bind the button decision variables to boundaries and set inspection variables
      for (int j = 0; j < nButtons; j++) {
        x[j] = model.newIntVar(0, maxTarget, "x_" + j);
      }

      // Constraints: for each counter i, sum_j A[i][j] * x_j == target[i]
      for (int i = 0; i < nCounters; i++) {
        List<IntVar> varsList = new ArrayList<>();
        List<Long> coeffsList = new ArrayList<>();

        for (int j = 0; j < nButtons; j++) {
          Button b = buttons.get(j);
          // Check if this button affects counter i
          boolean affects = false;
          for (int idx : b.indices) {
            if (idx == i) {
              // It does
              affects = true;
              break;
            }
          }

          if (affects) {
            // Add buttons affecting this counter to the varlist of this counter
            varsList.add(x[j]);
            // Increment per button push is always 1
            coeffsList.add(1L);   // coefficient is 1 for each press
          }
        }

        IntVar[] vars = varsList.toArray(new IntVar[0]);
        long[] coeffs = coeffsList.stream().mapToLong(Long::longValue).toArray();

        // complete decision model
        model.addEquality(LinearExpr.weightedSum(vars, coeffs), target[i]);
      }

      // Objective: minimize total presses sum_j x_j
      model.minimize(LinearExpr.sum(x));

      // Create solver
      CpSolver solver = new CpSolver();
      CpSolverStatus status = solver.solve(model);

      if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
        int total = 0;
        for (int j = 0; j < nButtons; j++) {
          total += (int) solver.value(x[j]);
        }
        // Return the total number of button presses (minimal required)
        return total;
      } else {
        // No solution
        return -1;
      }
    }
  }

}
