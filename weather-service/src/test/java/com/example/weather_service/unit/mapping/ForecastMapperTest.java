package com.example.weather_service.unit.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.weather_service.dto.external.response.ForecastResponse;
import com.example.weather_service.dto.external.response.Geometry;
import com.example.weather_service.dto.external.response.Period;
import com.example.weather_service.dto.external.response.Properties;
import com.example.weather_service.dto.internal.response.ForecastSummaryResponse;
import com.example.weather_service.mapping.ForecastMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ForecastMapperTest {

  private ForecastMapper forecastMapper;

  private Geometry geometry;
  private Period period;
  private List<Period> periods;
  private Properties properties;
  private ForecastResponse forecastResponse;

  @BeforeEach
  void setUp() {

    forecastMapper = new ForecastMapper();

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
  }

  @Test
  void givenForecastResponse_whenToForecastSummaryResponse_thenReturnsForecastSummaryResponse() {
    List<ForecastSummaryResponse> result =
        forecastMapper.toForecastSummaryResponse(forecastResponse);

    assertNotNull(result);
    assertEquals("Tonight", result.get(0).getName());
    assertEquals(19, result.get(0).getTemperature());
    assertEquals("5 to 15 mph", result.get(0).getWindSpeed());
    assertEquals(
        "Sunny, with a high near 43. Northwest wind around 15 mph.",
        result.get(0).getDetailedForecast());
  }
}
