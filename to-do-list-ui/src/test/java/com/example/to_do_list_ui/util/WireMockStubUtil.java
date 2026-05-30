package com.example.to_do_list_ui.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class WireMockStubUtil {

  public static void addCorsHeadersForPreflight(WireMockServer wireMockServer) {
    wireMockServer.stubFor(
        WireMock.options(WireMock.urlMatching(".*")) // Match the URL you want to handle
            .willReturn(
                WireMock.aResponse()
                    .withHeader(
                        "Access-Control-Allow-Origin",
                        "http://localhost:8080") // Allow only the specific origin
                    .withHeader(
                        "Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS") // Allow these methods
                    .withHeader(
                        "Access-Control-Allow-Headers",
                        "Content-Type, Authorization") // Allow these headers
                    .withHeader("Access-Control-Allow-Credentials", "true") // Allow credentials
                    .withStatus(200))); // Preflight response status OK
  }
}
