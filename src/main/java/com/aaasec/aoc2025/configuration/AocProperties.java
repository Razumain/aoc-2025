package com.aaasec.aoc2025.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "aoc")
public class AocProperties {

  String myLeaderboard;
  String baseDirectory;
  List<Resource> inputs;

}
