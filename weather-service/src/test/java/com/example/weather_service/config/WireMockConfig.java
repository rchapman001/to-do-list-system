package com.example.weather_service.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {

  // WireMockServer for NationalWeatherServiceApi on port 8087
  @Bean(initMethod = "start", destroyMethod = "stop")
  public WireMockServer wireMockNationalWeatherServiceApi() {
    WireMockServer wireMockServer =
        new WireMockServer(new WireMockConfiguration().stubCorsEnabled(true).port(8087));
    return wireMockServer;
  }
}
