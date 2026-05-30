package com.example.weather_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Value("${services.national-weather-service-api.url}")
  private String nationalWeatherServiceApiUrl;

  @Bean
  public WebClient nationalWeatherServiceApiWebClient(WebClient.Builder builder) {
    return builder.baseUrl(nationalWeatherServiceApiUrl).build();
  }
}
