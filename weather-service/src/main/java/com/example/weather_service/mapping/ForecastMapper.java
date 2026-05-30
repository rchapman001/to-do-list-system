package com.example.weather_service.mapping;

import com.example.weather_service.dto.external.response.ForecastResponse;
import com.example.weather_service.dto.external.response.Period;
import com.example.weather_service.dto.internal.response.ForecastSummaryResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ForecastMapper {

  public List<ForecastSummaryResponse> toForecastSummaryResponse(
      ForecastResponse forecastResponse) {
    List<ForecastSummaryResponse> forecastSummaryResponseList = new ArrayList<>();
    for (Period period : forecastResponse.getProperties().getPeriods()) {
      ForecastSummaryResponse forecastSummaryResponse = new ForecastSummaryResponse();
      forecastSummaryResponse.setName(period.getName());
      forecastSummaryResponse.setTemperature(period.getTemperature());
      forecastSummaryResponse.setWindSpeed(period.getWindSpeed());
      forecastSummaryResponse.setDetailedForecast(period.getDetailedForecast());
      forecastSummaryResponseList.add(forecastSummaryResponse);
    }
    return forecastSummaryResponseList;
  }
}
