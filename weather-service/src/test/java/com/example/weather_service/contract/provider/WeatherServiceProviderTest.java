package com.example.weather_service.contract.provider;

import au.com.dius.pact.provider.junit5.HttpsTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Execution(ExecutionMode.SAME_THREAD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Provider("NationalWeatherServiceApi") // This must match the "provider" name in the JSON
@PactFolder("${user.dir}/../contracts") // Specify your Pact contract folder location here
public class WeatherServiceProviderTest {

  @BeforeEach
  void before(PactVerificationContext context) {
    context.setTarget(new HttpsTestTarget("api.weather.gov", 443));
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void verifyPactContract(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @State("MinneapolisForecastExists")
  public void minneapolisForecastExists() {}
}
