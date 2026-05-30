package com.example.weather_service.dto.external.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Period {

  private int number;

  @NotNull(message = "name is required.")
  private String name;

  private String startTime;
  private String endTime;
  private boolean isDaytime;

  @NotNull(message = "temperature is required.")
  private int temperature;

  private String temperatureUnit;
  private String temperatureTrend;
  private ProbabilityOfPrecipitation probabilityOfPrecipitation;

  @NotNull(message = "windSpreed is required.")
  private String windSpeed;

  private String windDirection;
  private String icon;
  private String shortForecast;

  @NotNull(message = "detailedForecast is required.")
  private String detailedForecast;
}
