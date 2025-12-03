package com.aaasec.aoc2025.utils;

public class PatternString {

  private String pattern;
  private static final String INDEX_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzåäö";

  public PatternString(int len) {
    pattern = " ".repeat(len);
  }

  public void setChar(int pos, char c) {
    if (pos > pattern.length() -1) {
      throw new IllegalArgumentException("pos out of bounds: " + pos);
    }
    pattern =  pos == pattern.length() -1
        ? pattern.substring(0, pos) + c
        : pattern.substring(0, pos) + c + pattern.substring(pos + 1);
  }

  public void setIndexChar(int pos, int index) {
    if (index >= 0 && index < INDEX_CHAR.length()) {
      setChar(pos, INDEX_CHAR.charAt(index));
      return;
    }
    setChar(pos, '*');
  }

  public String toString() {
    return pattern;
  }

}
