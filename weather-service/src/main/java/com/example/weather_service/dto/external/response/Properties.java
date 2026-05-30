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
public class Properties {

  private String units;
  private String forecastGenerator;
  private String generatedAt;
  private String updateTime;
  private String validTimes;
  private Elevation elevation;
  @Valid private List<Period> periods;
}
