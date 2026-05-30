package com.example.weather_service.dto.external.response;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResponse {

  private List<String> context;
  private String type;
  private Geometry geometry;
  @Valid private Properties properties;
}
