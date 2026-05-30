package com.example.to_do_list_ui.unit.client;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import com.example.to_do_list_ui.client.WeatherServiceClient;
import com.example.to_do_list_ui.config.PactConfig;
import com.example.to_do_list_ui.contract.WeatherServicePact;
import com.example.to_do_list_ui.dto.external.response.ForecastSummaryResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@ActiveProfiles("test")
@Execution(ExecutionMode.SAME_THREAD)
@Import(PactConfig.class)
public class WeatherServiceClientTest implements WeatherServicePact {

  @Autowired private WeatherServiceClient weatherServiceClient;

  @Test
  @PactTestFor(
      providerName = "WeatherService",
      pactMethod = "givenWeatherServiceTest_whenGetTestEndpoint_thenReturnTestEndpointResponsePact",
      port = "8085")
  public void
      givenWeatherServiceTest_whenGetTestEndpoint_thenReturnTestEndpointResponseFromMockedWeatherService(
          MockServer mockServer) {

    System.out.println("Mock Weather Server URL: " + mockServer.getUrl());

    String response = weatherServiceClient.testEndpointWeather(); // Blocking call for testing

    assertThat(response).isNotNull();
    assertThat(response).isEqualTo("testEndpoint WeatherService");
  }

  @Test
  @PactTestFor(
      providerName = "WeatherService",
      pactMethod = "givenForecastExist_whenGetForecastSummary_thenReturnForecastPact",
      port = "8085")
  public void givenForecastExist_whenGetForecastSummary_thenReturnForecastFromMockedWeatherService(
      MockServer mockServer) {

    List<ForecastSummaryResponse> response = weatherServiceClient.getForecastSummary();

    assertThat(response).isNotNull();
    assertThat(response).isNotEmpty();

    for (ForecastSummaryResponse forecastSummaryResponse : response) {
      System.out.println("forecastSummaryResponse " + forecastSummaryResponse.getName());
      System.out.println("forecastSummaryResponse " + forecastSummaryResponse.getTemperature());
      System.out.println("forecastSummaryResponse " + forecastSummaryResponse.getWindSpeed());
      System.out.println(
          "forecastSummaryResponse " + forecastSummaryResponse.getDetailedForecast());
      assertThat(forecastSummaryResponse.getName()).isInstanceOf(String.class);
      assertThat(forecastSummaryResponse.getTemperature()).isInstanceOf(Integer.class);
      assertThat(forecastSummaryResponse.getWindSpeed()).isInstanceOf(String.class);
      assertThat(forecastSummaryResponse.getDetailedForecast()).isInstanceOf(String.class);
    }
  }
}
