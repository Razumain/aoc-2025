package com.aaasec.aoc2025.controller;

import com.aaasec.aoc2025.configuration.AocProperties;
import com.aaasec.aoc2025.solve.Solutions;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

  private final Solutions solutions;
  private final AocProperties properties;

  @Autowired
  public MainController(final Solutions solutions, AocProperties properties) {
    this.solutions = solutions;
    this.properties = properties;
  }

  /**
   * Handles the root endpoint ("/") and initializes the view with the selected day.
   * The selected day is determined based on the cookies present in the HTTP request.
   *
   * @param model the {@link Model} object used to populate data for the view
   * @param request the {@link HttpServletRequest} object containing details of the request, including cookies
   * @return the name of the view to be rendered, specifically "start"
   */
  @RequestMapping("/")
  public String start(Model model, HttpServletRequest request) {
    model.addAttribute("selectedDay", getDay(request));
    model.addAttribute("leaderboard", properties.getMyLeaderboard());
    model.addAttribute("input", solutions.getInput(getDay(request)));
    return "start";
  }

  /**
   * Processes the solution request for a specific day and part of the challenge.
   * Adds the result and the selected day to the model for rendering in the view,
   * and updates the response with a cookie containing the selected day.
   *
   * @param day the day of the challenge for which the solution is requested
   * @param part the part of the challenge (1 or 2) for which the solution is requested
   * @param model the {@link Model} object used to populate data for the view
   * @param response the {@link HttpServletResponse} object used to set a cookie for the selected day
   * @return the name of the view to be rendered, specifically "start"
   */
  @RequestMapping("/solve-{part}")
  public String getSolution(@RequestParam int day, @PathVariable int part, Model model, HttpServletResponse response) {
    String result = solutions.solve(day, part);
    model.addAttribute("result", result);
    model.addAttribute("selectedDay", day);
    model.addAttribute("leaderboard", properties.getMyLeaderboard());
    model.addAttribute("input", solutions.getInput(day));
    setDay(response, day);
    return "start";
  }

  @RequestMapping("/save-input")
  public String saveInput(@RequestParam int day, @RequestParam String input, HttpServletResponse response) throws Exception {
    solutions.saveInput(day, input);
    return "redirect:/";
  }

  @RequestMapping("/day-change")
  public String dayChange(@RequestParam int day, HttpServletResponse response) throws Exception {
    setDay(response, day);
    return "redirect:/";
  }

  /**
   * Sets a cookie to store the selected day value. The cookie will be accessible
   * throughout the entire application and will expire after 30 days.
   *
   * @param response the {@link HttpServletResponse} object used to add the cookie
   * @param day the selected day value to be stored in the cookie
   */
  private void  setDay(HttpServletResponse response, int day) {
    Cookie cookie = new Cookie("selectedDay", String.valueOf(day));
    cookie.setPath("/");          // make it available to the whole app
    cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
    response.addCookie(cookie);
  }

  /**
   * Extracts and returns the selected day from the cookies in the provided HTTP request.
   * If no valid "selectedDay" cookie is found, the default day value of 1 is returned.
   *
   * @param request the {@link HttpServletRequest} object containing the cookies
   * @return an integer representing the selected day, which is between 1 and 12 (inclusive)
   */
  private int getDay(final HttpServletRequest request) {
    int day = 1;

    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("selectedDay".equals(cookie.getName())) {
          try {
            int value = Integer.parseInt(cookie.getValue());
            if (value >= 1 && value <= 12) {
              day = value;
            }
          }
          catch (NumberFormatException ignored) {
            // Ignore invalid cookie value
          }
          break;
        }
      }
    }
    return day;
  }

}
