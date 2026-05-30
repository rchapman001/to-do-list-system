package com.example.to_do_list_ui.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {

  // WireMockServer for ListService on port 8082
  @Bean(initMethod = "start", destroyMethod = "stop")
  public WireMockServer wireMockListService() {
    WireMockServer wireMockServer =
        new WireMockServer(new WireMockConfiguration().stubCorsEnabled(true).port(8082));
    return wireMockServer;
  }

  // WireMockServer for TaskService on port 8083
  @Bean(initMethod = "start", destroyMethod = "stop")
  public WireMockServer wireMockTaskService() {
    WireMockServer wireMockServer =
        new WireMockServer(new WireMockConfiguration().stubCorsEnabled(true).port(8083));
    return wireMockServer;
  }

  // WireMockServer for WeatherService on port 8085
  @Bean(initMethod = "start", destroyMethod = "stop")
  public WireMockServer wireMockWeatherService() {
    WireMockServer wireMockServer =
        new WireMockServer(new WireMockConfiguration().stubCorsEnabled(true).port(8085));
    return wireMockServer;
  }
}
