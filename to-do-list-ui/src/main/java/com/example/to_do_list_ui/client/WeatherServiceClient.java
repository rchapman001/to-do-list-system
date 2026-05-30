package com.example.to_do_list_ui.client;

import com.example.to_do_list_ui.dto.external.response.ForecastSummaryResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WeatherServiceClient {

  @Autowired private WebClient weatherServiceWebClient;

  public String testEndpointWeather() {
    return weatherServiceWebClient
        .get()
        .uri("/forecast/test")
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  public List<ForecastSummaryResponse> getForecastSummary() {
    return weatherServiceWebClient
        .get()
        .uri("/forecast")
        .retrieve()
        .bodyToFlux(ForecastSummaryResponse.class)
        .collectList()
        .block();
  }
}
