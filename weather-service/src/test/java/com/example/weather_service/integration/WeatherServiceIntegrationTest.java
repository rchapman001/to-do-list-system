package com.example.weather_service.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.weather_service.config.WireMockConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = "server.port=8085")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Execution(ExecutionMode.SAME_THREAD)
@Import({WireMockConfig.class})
public class WeatherServiceIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private WireMockServer wireMockNationalWeatherServiceApi;

  @BeforeEach
  void setUp() {
    wireMockNationalWeatherServiceApi.resetAll();
  }

  @Test
  void givenForecast_whenGetForecastSummary_thenReturnsForecastSummary() throws Exception {
    String mockedForecastResponse =
        """
        {
          "@context": [
              "https://geojson.org/geojson-ld/geojson-context.jsonld",
              {
                  "@version": "1.1",
                  "wx": "https://api.weather.gov/ontology#",
                  "geo": "http://www.opengis.net/ont/geosparql#",
                  "unit": "http://codes.wmo.int/common/unit/",
                  "@vocab": "https://api.weather.gov/ontology#"
              }
          ],
          "type": "Feature",
          "geometry": {
              "type": "Polygon",
              "coordinates": [
                  [
                      [
                          -93.2627,
                          44.9653
                      ],
                      [
                          -93.2623,
                          44.9867
                      ],
                      [
                          -93.2925999,
                          44.9869
                      ],
                      [
                          -93.2929,
                          44.9655
                      ],
                      [
                          -93.2627,
                          44.9653
                      ]
                  ]
              ]
          },
          "properties": {
              "units": "us",
              "forecastGenerator": "BaselineForecastGenerator",
              "generatedAt": "2025-04-06T21:32:07+00:00",
              "updateTime": "2025-04-06T19:48:16+00:00",
              "validTimes": "2025-04-06T13:00:00+00:00/P7DT12H",
              "elevation": {
                  "unitCode": "wmoUnit:m",
                  "value": 259.08
              },
              "periods": [
                  {
                      "number": 1,
                      "name": "Tonight",
                      "startTime": "2025-04-06T16:00:00-05:00",
                      "endTime": "2025-04-06T18:00:00-05:00",
                      "isDaytime": true,
                      "temperature": 19,
                      "temperatureUnit": "F",
                      "temperatureTrend": "",
                      "probabilityOfPrecipitation": {
                          "unitCode": "wmoUnit:percent",
                          "value": null
                      },
                      "windSpeed": "5 to 15 mph",
                      "windDirection": "W",
                      "icon": "https://api.weather.gov/icons/land/day/sct?size=medium",
                      "shortForecast": "Mostly Sunny",
                      "detailedForecast": "Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph."
                  }
                ]
            }
        }
        """;

    wireMockNationalWeatherServiceApi.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/gridpoints/MPX/108,72/forecast"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockedForecastResponse)
                    .withStatus(200)));

    // Add your test logic here to call the endpoint and verify the response

    mockMvc
        .perform(get("/forecast").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Tonight"))
        .andExpect(jsonPath("$[0].temperature").value(19))
        .andExpect(jsonPath("$[0].windSpeed").value("5 to 15 mph"))
        .andExpect(
            jsonPath("$[0].detailedForecast")
                .value(
                    "Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph."));
  }

  @Test
  void givenInvalidForecast_whenGetForecastSummary_thenReturnsErrorResponse() throws Exception {
    String mockedForecastResponseWithoutName =
        """
        {
          "@context": [
              "https://geojson.org/geojson-ld/geojson-context.jsonld",
              {
                  "@version": "1.1",
                  "wx": "https://api.weather.gov/ontology#",
                  "geo": "http://www.opengis.net/ont/geosparql#",
                  "unit": "http://codes.wmo.int/common/unit/",
                  "@vocab": "https://api.weather.gov/ontology#"
              }
          ],
          "type": "Feature",
          "geometry": {
              "type": "Polygon",
              "coordinates": [
                  [
                      [
                          -93.2627,
                          44.9653
                      ],
                      [
                          -93.2623,
                          44.9867
                      ],
                      [
                          -93.2925999,
                          44.9869
                      ],
                      [
                          -93.2929,
                          44.9655
                      ],
                      [
                          -93.2627,
                          44.9653
                      ]
                  ]
              ]
          },
          "properties": {
              "units": "us",
              "forecastGenerator": "BaselineForecastGenerator",
              "generatedAt": "2025-04-06T21:32:07+00:00",
              "updateTime": "2025-04-06T19:48:16+00:00",
              "validTimes": "2025-04-06T13:00:00+00:00/P7DT12H",
              "elevation": {
                  "unitCode": "wmoUnit:m",
                  "value": 259.08
              },
              "periods": [
                  {
                      "number": 1,
                      "startTime": "2025-04-06T16:00:00-05:00",
                      "endTime": "2025-04-06T18:00:00-05:00",
                      "isDaytime": true,
                      "temperature": 19,
                      "temperatureUnit": "F",
                      "temperatureTrend": "",
                      "probabilityOfPrecipitation": {
                          "unitCode": "wmoUnit:percent",
                          "value": null
                      },
                      "windSpeed": "5 to 15 mph",
                      "windDirection": "W",
                      "icon": "https://api.weather.gov/icons/land/day/sct?size=medium",
                      "shortForecast": "Mostly Sunny",
                      "detailedForecast": "Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph."
                  }
                ]
            }
        }
        """;

    wireMockNationalWeatherServiceApi.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/gridpoints/MPX/108,72/forecast"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockedForecastResponseWithoutName)
                    .withStatus(200)));

    // Add your test logic here to call the endpoint and verify the response

    mockMvc
        .perform(get("/forecast").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorType").value("Constraint Validation Exceptions"))
        .andExpect(jsonPath("$.errors[0].field").value("properties.periods[0].name"))
        .andExpect(
            jsonPath("$.errors[0].constraint")
                .value(
                    "@jakarta.validation.constraints.NotNull(message=\"name is required.\", payload={}, groups={})"))
        .andExpect(jsonPath("$.errors[0].message").value("name is required."));
  }
}
