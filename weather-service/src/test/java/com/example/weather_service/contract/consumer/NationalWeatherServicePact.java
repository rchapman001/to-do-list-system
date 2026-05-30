package com.example.weather_service.contract.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.Map;
import org.junit.jupiter.api.Disabled;

public interface NationalWeatherServicePact {

  @Disabled
  @Pact(consumer = "WeatherService", provider = "NationalWeatherServiceApi")
  default V4Pact
      givenMinneapolisForecastExists_whenGetWeatherForecast_thenReturnMinneapolisForecastPact(
          PactDslWithProvider builder) {

    // There is more data in the response, but I expect the following fields to exist and not be
    // null.
    DslPart responseBody =
        new PactDslJsonBody()
            .object("properties")
            .minArrayLike("periods", 1)
            .stringType("name", "Tonight")
            .numberType("temperature", 19)
            .stringType("windSpeed", "5 to 15 mph")
            .stringType(
                "detailedForecast",
                "Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.")
            .closeObject()
            .closeArray()
            .closeObject();

    return builder
        .given("MinneapolisForecastExists")
        .uponReceiving("GET /gridpoints/MPX/108,72/forecast: Get weather forecast")
        .path("/gridpoints/MPX/108,72/forecast")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/geo+json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }
}
