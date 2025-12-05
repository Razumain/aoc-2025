package com.aaasec.aoc2025.utils;

import com.aaasec.aoc2025.solutions.Day5Solution;
import lombok.Getter;

@Getter
public class Range {

  private long min;
  private long max;

  public Range(String rString) {
    String[] split = rString.split("-");
    this.min = Long.parseLong(split[0]);
    this.max = Long.parseLong(split[1]);
  }

  public Range(final long min, final long max) {
    this.min = min;
    this.max = max;
  }

  public long size() {
    return max - min + 1;
  }
  public boolean contains(long n) {
    return n >= min && n <= max;
  }

  public boolean overlaps(final Range tested) {
    /*
    Space infiltration starts when the current range is the lesser one, but infiltrates the tested range (max >= tested.min)
    Space infiltration persists until the current range is the greater one, but tested.max till infiltrates current min
     */
    return max >= tested.getMin() && tested.getMax() >= min;
  }

  @Override
  public String toString() {
    return min + "-" + max;
  }

}
