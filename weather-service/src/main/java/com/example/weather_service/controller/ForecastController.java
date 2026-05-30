package com.example.weather_service.controller;

import com.example.weather_service.dto.internal.response.ForecastSummaryResponse;
import com.example.weather_service.service.ForecastService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forecast")
public class ForecastController {

  @Autowired private ForecastService forecastService;

  @GetMapping("/test")
  public String testEndpoint() {
    return forecastService.testEndpoint();
  }

  @GetMapping
  public ResponseEntity<List<ForecastSummaryResponse>> getForecastSummary() {
    List<ForecastSummaryResponse> forecastSummaryResponseList =
        forecastService.getForecastSummary();
    return ResponseEntity.ok(forecastSummaryResponseList);
  }
}
