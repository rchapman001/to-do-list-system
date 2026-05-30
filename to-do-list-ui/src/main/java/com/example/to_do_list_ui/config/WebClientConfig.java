package com.example.to_do_list_ui.config;

import com.example.to_do_list_ui.client.TaskServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  private static final Logger logger = LoggerFactory.getLogger(TaskServiceClient.class);

  @Value("${services.list-service.url}")
  private String listServiceUrl;

  @Value("${services.task-service.url}")
  private String taskServiceUrl;

  @Value("${services.weather-service.url}")
  private String weatherServiceUrl;

  @Bean
  public WebClient listServiceWebClient(WebClient.Builder builder) {
    return builder.baseUrl(listServiceUrl).build();
  }

  @Bean
  public WebClient taskServiceWebClient(WebClient.Builder builder) {
    return builder.baseUrl(taskServiceUrl).build();
  }

  @Bean
  public WebClient weatherServiceWebClient(WebClient.Builder builder) {
    return builder.baseUrl(weatherServiceUrl).build();
  }
}
