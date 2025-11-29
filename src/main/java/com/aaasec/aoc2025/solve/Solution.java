package com.aaasec.aoc2025.solve;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Represents an abstract solution template for a problem. Each solution corresponds to a particular day, title, and input file.
 * It provides the framework for implementing solutions for Part 1 and Part 2 of the problem.
 * Concrete implementations must override the methods for solving both parts of the problem.
 *
 * The class also includes mechanisms for logging actions and results during the problem-solving process.
 */
public abstract class Solution {

  private final int day;
  private final String title;
  private final File inputFile;
  protected QuickLogger log;
  @Getter private List<String> input;

  /**
   * Constructs a new Solution instance.
   *
   * @param day the day number corresponding to the problem being solved
   * @param title the title or name associated with the problem, can be null
   * @param inputFile the input file containing data necessary for solving the problem
   * @throws IOException if an error occurs while reading the input file
   */
  public Solution(int day, String title, File inputFile) throws IOException {
    this.day = day;
    this.title = title;
    this.inputFile = inputFile;
    this.log = new QuickLogger();
    readInput();
  }

  public void readInput() throws IOException {
    this.input = Files.readAllLines(inputFile.toPath());
  }

  public void setInput(List<String> input) throws IOException {
    setInput(String.join(System.lineSeparator(), input));
  }

  public void setInput(String input) throws IOException {
    Files.write(inputFile.toPath(), input.getBytes(StandardCharsets.UTF_8));
    readInput();
  }

  /**
   * Solves the first part of the problem for the given day's solution.
   * This method must be implemented by subclasses to define the specific logic for solving Part 1.
   *
   * Implementations of this method typically make use of the input data and logger provided by the
   * base Solution class to process the problem and log the results.
   */
  public abstract void solvePart1(List<String> input);
  public abstract void solvePart2(List<String> input);


  public String getSolutionPart1() {
    log.clear();

    log.add("Solving part 1 of day %d", day);
    if (title != null) {
      log.add("--- %s ---", title);
    }
    solvePart1(input);
    return log.getLog();
  }
  public String getSolutionPart2() {
    log.clear();

    log.add("Solving part 2 of day %d", day);
    if (title != null) {
      log.add("--- %s ---", title);
    }
    solvePart2(input);
    return log.getLog();
  }

}
