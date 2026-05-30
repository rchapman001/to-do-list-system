package com.example.weather_service.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.example.weather_service.client.NationalWeatherServiceApiClient;
import com.example.weather_service.dto.external.response.ForecastResponse;
import com.example.weather_service.dto.external.response.Geometry;
import com.example.weather_service.dto.external.response.Period;
import com.example.weather_service.dto.external.response.Properties;
import com.example.weather_service.dto.internal.response.ForecastSummaryResponse;
import com.example.weather_service.mapping.ForecastMapper;
import com.example.weather_service.service.ForecastService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ForecastServiceTest {

  @Mock private NationalWeatherServiceApiClient nationalWeatherServiceApiClient;
  @Mock private ForecastMapper forecastMapper;

  @InjectMocks private ForecastService forecastService;

  private Geometry geometry;
  private Period period;
  private List<Period> periods;
  private Properties properties;
  private ForecastResponse forecastResponse;

  private ForecastSummaryResponse forecastSummaryResponse;
  private List<ForecastSummaryResponse> forecastSummaryResponseList;

  @BeforeEach
  void setUp() {
    // Mocking Geometry Object
    List<List<Double>> polygonRing =
        Arrays.asList(
            Arrays.asList(-93.2627, 44.9653),
            Arrays.asList(-93.2623, 44.9867),
            Arrays.asList(-93.2925999, 44.9869),
            Arrays.asList(-93.2929, 44.9655),
            Arrays.asList(-93.2627, 44.9653));

    List<List<List<Double>>> coordinates = Collections.singletonList(polygonRing);
    geometry = new Geometry();
    geometry.setType("Polygon");
    geometry.setCoordinates(coordinates);

    // Mocking Properties Object
    period = new Period();
    period.setNumber(1);
    period.setName("Tonight");
    period.setStartTime("2025-04-05T10:00:00-05:00");
    period.setEndTime("2025-04-05T18:00:00-05:00");
    period.setDaytime(true);
    period.setTemperature(19);
    period.setTemperatureUnit("F");
    period.setTemperatureTrend("");
    period.setWindSpeed("5 to 15 mph");
    period.setWindDirection("NW");
    period.setIcon("https://api.weather.gov/icons/land/day/skc?size=medium");
    period.setShortForecast("Sunny");
    period.setDetailedForecast("Sunny, with a high near 43. Northwest wind around 15 mph.");

    periods = Collections.singletonList(period);

    properties = new Properties();
    properties.setUnits("us");
    properties.setForecastGenerator("BaselineForecastGenerator");
    properties.setGeneratedAt("2025-04-05T14:09:07+00:00");
    properties.setUpdateTime("2025-04-05T08:00:00+00:00/P7DT17H");
    properties.setPeriods(periods);

    // Mocking ForecastResponse Object
    forecastResponse = new ForecastResponse();
    forecastResponse.setContext(
        Arrays.asList(
            "https://geojson.org/geojson-ld/geojson-context.jsonld",
            "{\"@version\": \"1.1\", \"wx\": \"https://api.weather.gov/ontology#\", \"geo\": \"http://www.opengis.net/ont/geosparql#\", \"unit\": \"http://codes.wmo.int/common/unit/\", \"@vocab\": \"https://api.weather.gov/ontology#\"}"));
    forecastResponse.setType("Feature");
    forecastResponse.setGeometry(geometry);
    forecastResponse.setProperties(properties);

    // Mock ForecastSummaryResponse
    forecastSummaryResponse = new ForecastSummaryResponse();
    forecastSummaryResponse.setName("Tonight");
    forecastSummaryResponse.setTemperature(19);
    forecastSummaryResponse.setWindSpeed("5 to 15 mph");
    forecastSummaryResponse.setDetailedForecast(
        "Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.");

    forecastSummaryResponseList = Arrays.asList(forecastSummaryResponse);
  }

  @Test
  void givenTestEndpoint_whenGetTestEndpoint_thenReturnsTestEndpointResponse() {
    String response = forecastService.testEndpoint();

    assertNotNull(response);
    assertEquals("testEndpoint WeatherService", response);
  }

  @Test
  void givenForecastSummary_whenGetForecastSummary_thenReturnsForecastSummary() {
    when(nationalWeatherServiceApiClient.getWeatherForecast()).thenReturn(forecastResponse);
    when(forecastMapper.toForecastSummaryResponse(forecastResponse))
        .thenReturn(forecastSummaryResponseList);

    List<ForecastSummaryResponse> response = forecastService.getForecastSummary();

    assertNotNull(response);
    assertEquals(1, response.size());
    ForecastSummaryResponse actualForecast = response.get(0);
    assertEquals("Tonight", actualForecast.getName());
    assertEquals(19, actualForecast.getTemperature());
    assertEquals("5 to 15 mph", actualForecast.getWindSpeed());
    assertEquals(
        "Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.",
        actualForecast.getDetailedForecast());
  }
}
