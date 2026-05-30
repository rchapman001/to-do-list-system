package com.example.weather_service.client;

import com.example.weather_service.dto.external.response.ForecastResponse;
import com.example.weather_service.util.ResponseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NationalWeatherServiceApiClient {

  @Autowired private WebClient nationalWeatherServiceApiWebClient;
  @Autowired private ResponseValidator responseValidator;

  public ForecastResponse getWeatherForecast() {
    return nationalWeatherServiceApiWebClient
        .get()
        .uri("/gridpoints/MPX/108,72/forecast")
        .retrieve()
        .bodyToMono(ForecastResponse.class)
        .filter(responseValidator::isValid)
        .block();
  }
}
