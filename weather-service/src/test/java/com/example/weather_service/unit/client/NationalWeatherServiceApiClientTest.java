package com.example.weather_service.unit.client;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import com.example.weather_service.client.NationalWeatherServiceApiClient;
import com.example.weather_service.config.PactConfig;
import com.example.weather_service.contract.consumer.NationalWeatherServicePact;
import com.example.weather_service.dto.external.response.ForecastResponse;
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
public class NationalWeatherServiceApiClientTest implements NationalWeatherServicePact {

  @Autowired private NationalWeatherServiceApiClient nationalWeatherServiceApiWebClient;

  @Test
  @PactTestFor(
      providerName = "NationalWeatherServiceApi",
      pactMethod =
          "givenMinneapolisForecastExists_whenGetWeatherForecast_thenReturnMinneapolisForecastPact",
      port = "8087")
  public void
      givenMinneapolisForecastExists_whenGetWeatherForecast_thenReturnMinneapolisForecastFromMockedNationalWeatherServiceApi(
          MockServer mockServer) {

    ForecastResponse response = nationalWeatherServiceApiWebClient.getWeatherForecast();

    assertThat(response).isNotNull();
    assertThat(response.getProperties().getPeriods().get(0).getName()).isEqualTo("Tonight");
    assertThat(response.getProperties().getPeriods().get(0).getTemperature()).isEqualTo(19);
    assertThat(response.getProperties().getPeriods().get(0).getWindSpeed())
        .isEqualTo("5 to 15 mph");
    assertThat(response.getProperties().getPeriods().get(0).getDetailedForecast())
        .isEqualTo(
            "Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.");
  }
}
