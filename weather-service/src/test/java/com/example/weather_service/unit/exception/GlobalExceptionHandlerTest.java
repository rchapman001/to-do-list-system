package com.example.weather_service.unit.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.weather_service.dto.external.response.ForecastResponse;
import com.example.weather_service.dto.external.response.Geometry;
import com.example.weather_service.dto.external.response.Period;
import com.example.weather_service.dto.external.response.Properties;
import com.example.weather_service.exception.GlobalExceptionHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;
  private Validator validator;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void handleGeneralException_returnsInternalServerError() {
    // Given: a general exception
    Exception ex = new Exception("java.lang.Exception");

    // When: it is passed to the exception handler
    ResponseEntity<Map<String, String>> response = handler.handleGeneralException(ex);

    // Then: assert the correct HTTP status and message are returned
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody().get("message"))
        .isEqualTo("An unexpected error occurred: java.lang.Exception");
  }

  @Test
  void forecastResponseValidationFailure_triggersConstraintViolationException() {
    // Mocking Geometry Object
    List<List<Double>> polygonRing =
        Arrays.asList(
            Arrays.asList(-93.2627, 44.9653),
            Arrays.asList(-93.2623, 44.9867),
            Arrays.asList(-93.2925999, 44.9869),
            Arrays.asList(-93.2929, 44.9655),
            Arrays.asList(-93.2627, 44.9653));

    List<List<List<Double>>> coordinates = Collections.singletonList(polygonRing);

    Geometry geometry = new Geometry();
    geometry.setType("Polygon");
    geometry.setCoordinates(coordinates);

    // Mocking Properties Object
    Period invalidPeriod = new Period();
    invalidPeriod.setNumber(1);
    invalidPeriod.setName(null); // Required field
    invalidPeriod.setStartTime("2025-04-05T10:00:00-05:00");
    invalidPeriod.setEndTime("2025-04-05T18:00:00-05:00");
    invalidPeriod.setDaytime(true);
    invalidPeriod.setTemperature(19);
    invalidPeriod.setTemperatureUnit("F");
    invalidPeriod.setTemperatureTrend("");
    invalidPeriod.setWindSpeed("15 mph");
    invalidPeriod.setWindDirection("NW");
    invalidPeriod.setIcon("https://api.weather.gov/icons/land/day/skc?size=medium");
    invalidPeriod.setShortForecast("Sunny");
    invalidPeriod.setDetailedForecast("Sunny, with a high near 43. Northwest wind around 15 mph.");

    List<Period> periods = Collections.singletonList(invalidPeriod);

    Properties properties = new Properties();
    properties.setUnits("us");
    properties.setForecastGenerator("BaselineForecastGenerator");
    properties.setGeneratedAt("2025-04-05T14:09:07+00:00");
    properties.setUpdateTime("2025-04-05T08:00:00+00:00/P7DT17H");
    properties.setPeriods(periods);

    // Mocking ForecastResponse Object
    ForecastResponse forecastResponse = new ForecastResponse();
    forecastResponse.setContext(
        Arrays.asList(
            "https://geojson.org/geojson-ld/geojson-context.jsonld",
            "{\"@version\": \"1.1\", \"wx\": \"https://api.weather.gov/ontology#\", \"geo\": \"http://www.opengis.net/ont/geosparql#\", \"unit\": \"http://codes.wmo.int/common/unit/\", \"@vocab\": \"https://api.weather.gov/ontology#\"}"));
    forecastResponse.setType("Feature");
    forecastResponse.setGeometry(geometry);
    forecastResponse.setProperties(properties);

    // Validate the ForecastResponse, which should trigger a violation for the missing 'name' in
    // Period.
    Set<ConstraintViolation<ForecastResponse>> violations = validator.validate(forecastResponse);
    // Confirm that there is at least one violation.
    assertThat(violations).isNotEmpty();

    ConstraintViolationException ex =
        new ConstraintViolationException("Invalid response: " + violations, violations);

    // Pass the exception to your GlobalExceptionHandler.
    ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

    // Then: assert status and general error type
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Map<String, Object> body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.get("errorType")).isEqualTo("Constraint Validation Exceptions");

    List<Map<String, String>> errors = (List<Map<String, String>>) body.get("errors");

    Map<String, String> firstError = errors.get(0);
    assertThat(firstError.get("field")).isEqualTo("properties.periods[0].name");
    assertThat(firstError.get("message")).isEqualTo("name is required.");
    assertThat(firstError.get("constraint"))
        .isEqualTo(
            "@jakarta.validation.constraints.NotNull(message=\"name is required.\", payload={}, groups={})");
  }
}
