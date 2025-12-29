package com.aaasec.aoc2025.utils.dlx;

import java.util.List;

public class DefaultHandler implements SolutionHandler{
  public void handleSolution(List<DancingLinks.DancingNode> answer){
    for(DancingLinks.DancingNode n : answer){
      String ret = "";
      ret += n.C.name + " ";
      DancingLinks.DancingNode tmp = n.R;
      while (tmp != n){
        ret += tmp.C.name + " ";
        tmp = tmp.R;
      }
      System.out.println(ret);
    }
  }
}
