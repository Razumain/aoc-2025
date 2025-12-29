package com.aaasec.aoc2025.solve;

import com.aaasec.aoc2025.configuration.AocProperties;
import com.aaasec.aoc2025.solutions.Day10Solution;
import com.aaasec.aoc2025.solutions.Day11Solution;
import com.aaasec.aoc2025.solutions.Day12Solution;
import com.aaasec.aoc2025.solutions.Day1Solution;
import com.aaasec.aoc2025.solutions.Day2Solution;
import com.aaasec.aoc2025.solutions.Day3Solution;
import com.aaasec.aoc2025.solutions.Day4Solution;
import com.aaasec.aoc2025.solutions.Day5Solution;
import com.aaasec.aoc2025.solutions.Day6Solution;
import com.aaasec.aoc2025.solutions.Day7Solution;
import com.aaasec.aoc2025.solutions.Day8Solution;
import com.aaasec.aoc2025.solutions.Day9Solution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Configuration
public class Solutions {

  public final List<Solution> solutions;
  public AocProperties aocProperties;

  /**
   * Constructs a Solutions instance to manage and initialize solution instances for each day.
   *
   * @param aocProperties the Advent of Code properties containing configuration such as input file resources
   * @throws IOException if an error occurs while reading input files
   */
  @Autowired
  public Solutions(AocProperties aocProperties) throws IOException {
    this.aocProperties = aocProperties;
    final List<Resource> inputs = aocProperties.getInputs();
    solutions = List.of(
        new Day1Solution(1,
            null,
            inputs.get(0).getFile()),
        new Day2Solution(2,
            null,
            inputs.get(1).getFile()),
        new Day3Solution(3,
            null,
            inputs.get(2).getFile()),
        new Day4Solution(4,
            null,
            inputs.get(3).getFile()),
        new Day5Solution(5,
            null,
            inputs.get(4).getFile()),
        new Day6Solution(6,
            null,
            inputs.get(5).getFile()),
        new Day7Solution(7,
            null,
            inputs.get(6).getFile()),
        new Day8Solution(8,
            null,
            inputs.get(7).getFile()),
        new Day9Solution(9,
            null,
            inputs.get(8).getFile()),
        new Day10Solution(10,
            null,
            inputs.get(9).getFile()),
        new Day11Solution(11,
            null,
            inputs.get(10).getFile()),
        new Day12Solution(12,
            null,
            inputs.get(11).getFile())
    );
  }

  /**
   * Retrieves the solution for a specified day and part of the Advent of Code challenge.
   *
   * @param day the day of the challenge, with valid values starting from 1 up to the maximum available days
   * @param part the part of the challenge to solve, with valid values being 1 or 2
   * @return the solution for the specified day and part as a string, or an error message if the part number is invalid
   */
  public String solve(int day, int part) {
    return switch (part) {
      case 1 -> solutions.get(day - 1).getSolutionPart1();
      case 2 -> solutions.get(day - 1).getSolutionPart2();
      default -> "Invalid part number";
    };
  }

  public String getInput(int day) {
    return String.join(System.lineSeparator(), solutions.get(day - 1).getInput());
  }

  public void saveInput(int day, String input) throws IOException {
    solutions.get(day - 1).setInput(List.of(input));
    Files.write(aocProperties.getInputs().get(day -1).getFilePath(), input.getBytes(StandardCharsets.UTF_8));
  }
}
