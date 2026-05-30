package com.example.weather_service.unit.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.example.weather_service.controller.ForecastController;
import com.example.weather_service.dto.internal.response.ForecastSummaryResponse;
import com.example.weather_service.service.ForecastService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ForecastControllerTest {

  @InjectMocks private ForecastController forecastController; // Inject the controller we're testing

  @Mock private ForecastService forecastService; // Mock the service layer

  private ForecastSummaryResponse forecastSummaryResponse;
  private List<ForecastSummaryResponse> forecastSummaryResponseList;

  @BeforeEach
  void setUp() {
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
    // Given a mocked response from the service
    when(forecastService.testEndpoint()).thenReturn("testEndpoint WeatherService");

    // When calling the controller method
    String response = forecastController.testEndpoint();

    // Then the response should be as expected
    assertEquals("testEndpoint WeatherService", response);
  }

  @Test
  void givenForecastSummary_whenGetForecastSummary_thenReturnsForecastSummary() {
    // Given a mocked response from the service
    when(forecastService.getForecastSummary()).thenReturn(forecastSummaryResponseList);

    // When calling the controller method
    ResponseEntity<List<ForecastSummaryResponse>> response =
        forecastController.getForecastSummary();

    // Then the response should not be null
    assertNotNull(response);

    // And the status code should be 200 OK (since it's a successful response)
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // And the response body should contain the expected values
    List<ForecastSummaryResponse> responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals(1, responseBody.size());
    assertEquals("Tonight", responseBody.get(0).getName());
    assertEquals(19, responseBody.get(0).getTemperature());
    assertEquals("5 to 15 mph", responseBody.get(0).getWindSpeed());
    assertEquals(
        "Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.",
        responseBody.get(0).getDetailedForecast());
  }
}
