package com.example.weather_service.dto.external.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Geometry {

  private String type;
  private List<List<List<Double>>> coordinates;
}
