package com.example.weather_service.dto.internal.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastSummaryResponse {

  private String name;
  private int temperature;
  private String windSpeed;
  private String detailedForecast;
}
