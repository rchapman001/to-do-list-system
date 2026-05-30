package com.example.to_do_list_ui.contract;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.Map;
import org.junit.jupiter.api.Disabled;

public interface WeatherServicePact {

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "WeatherService")
  default V4Pact givenWeatherServiceTest_whenGetTestEndpoint_thenReturnTestEndpointResponsePact(
      PactDslWithProvider builder) {
    return builder
        .given("WeatherServiceTest")
        .uponReceiving("GET /forecast/test: GetTestEndpoint")
        .path("/forecast/test") // Matching the endpoint in the client call
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "text/plain"))
        .body("testEndpoint WeatherService") // Expected response
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "WeatherService")
  default V4Pact givenForecastExist_whenGetForecastSummary_thenReturnForecastPact(
      PactDslWithProvider builder) {

    DslPart responseBody =
        PactDslJsonArray.arrayEachLike()
            .stringType("name", "Tonight")
            .integerType("temperature", 19)
            .stringType("windSpeed", "5 to 15 mph")
            .stringType(
                "detailedForecast",
                "Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.")
            .closeObject();

    return builder
        .given("ForecastExist")
        .uponReceiving("GET /forecast: GetForecastSummary")
        .path("/forecast")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }
}
