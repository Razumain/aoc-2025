package com.aaasec.aoc2025.solve;

/**
 * A utility class for creating and managing logs efficiently. QuickLogger allows
 * appending formatted log messages, retrieving collected logs, and clearing log
 * contents. It is designed to be lightweight and straightforward for simple
 * logging needs.
 */
public class QuickLogger {

  private final StringBuilder sb;
  private final String LF = System.lineSeparator();

  public QuickLogger() {
    this.sb = new StringBuilder();
  }

  public void add(String inp, Object... params) {
    sb.append(String.format(inp, params)).append(LF);
  }

  public void lfadd(String inp, Object... params) {
    sb.append(LF);
    sb.append(String.format(inp, params)).append(LF);
  }

  public String getLog() {
    return sb.toString();
  }

  public void clear() {
    sb.setLength(0);
  }
}
