package com.example.weather_service.dto.external.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProbabilityOfPrecipitation {

  private String unitCode;
  private Integer value;
}
