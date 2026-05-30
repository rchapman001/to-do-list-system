package com.example.weather_service.service;

import com.example.weather_service.client.NationalWeatherServiceApiClient;
import com.example.weather_service.dto.external.response.ForecastResponse;
import com.example.weather_service.dto.internal.response.ForecastSummaryResponse;
import com.example.weather_service.mapping.ForecastMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForecastService {

  @Autowired private NationalWeatherServiceApiClient nationalWeatherServiceApiClient;

  @Autowired private ForecastMapper forecastMapper;

  public String testEndpoint() {
    return "testEndpoint WeatherService";
  }

  public List<ForecastSummaryResponse> getForecastSummary() {
    ForecastResponse forecastResponse = nationalWeatherServiceApiClient.getWeatherForecast();
    return forecastMapper.toForecastSummaryResponse(forecastResponse);
  }
}
