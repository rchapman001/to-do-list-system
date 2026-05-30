package com.example.task_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  @Value("${spring.web.cors.allowed-origins}")
  private String allowedOrigins;

  @Value("${spring.web.cors.allowed-methods}")
  private String allowedMethods;

  @Value("${spring.web.cors.allowed-headers}")
  private String allowedHeaders;

  @Value("${spring.web.cors.allow-credentials}")
  private boolean allowCredentials;

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOrigins(allowedOrigins.split(","))
            .allowedMethods(allowedMethods.split(","))
            .allowedHeaders(allowedHeaders.split(","))
            .allowCredentials(allowCredentials);
      }
    };
  }
}
