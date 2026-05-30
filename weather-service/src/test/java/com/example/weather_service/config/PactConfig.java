package com.example.weather_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@TestConfiguration
public class PactConfig {

  @Value("${pact.rootDir:#{systemProperties['user.dir']}/../contracts}")
  private String pactRootDir;

  @Bean
  public ApplicationRunner configurePactRootDir() {
    return args -> {
      System.setProperty("pact.rootDir", pactRootDir);
      System.setProperty("pact.writer.overwrite", "true");
      System.out.println("✅ Pact Root Directory set to: " + System.getProperty("pact.rootDir"));
    };
  }

  // TODO: This isn't working it is just using the value in the application.yml
  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("services.national-weather-service-api.url", () -> "http://localhost:8087");
  }
}
