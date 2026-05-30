package com.example.to_do_list_ui.unit.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import com.example.to_do_list_ui.client.ListServiceClient;
import com.example.to_do_list_ui.config.PactConfig;
import com.example.to_do_list_ui.contract.ListServicePact;
import com.example.to_do_list_ui.dto.external.response.ToDoListApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@ActiveProfiles("test")
@Execution(ExecutionMode.SAME_THREAD)
@Import(PactConfig.class)
public class ListServiceClientTest implements ListServicePact {

  @Autowired private ListServiceClient listServiceClient;

  @Test
  @PactTestFor(
      providerName = "ListService",
      pactMethod = "givenListServiceTest_whenGetTestEndpoint_thenReturnTestEndpointResponsePact",
      port = "8082")
  public void
      givenListServiceTest_whenGetTestEndpoint_thenReturnTestEndpointResponseFromMockedListService(
          MockServer mockServer) {
    String response = listServiceClient.testEndpointList(); // Blocking call for testing

    assertThat(response).isNotNull();
    assertThat(response).isEqualTo("testEndpoint ListService");
  }

  @Test
  @PactTestFor(
      providerName = "ListService",
      pactMethod = "givenToDoListsExist_whenGetAllToDoLists_thenReturnToDoListsPact",
      port = "8082")
  public void givenToDoListsExist_whenGetAllToDoLists_thenReturnToDoListsFromMockedListService(
      MockServer mockServer) throws Exception {

    List<ToDoListApiResponse> response = listServiceClient.getAllToDoLists();

    assertThat(response).isNotNull();
    assertThat(response).isNotEmpty();
    assertThat(response).hasSize(1);

    for (ToDoListApiResponse list : response) {
      assertThat(list.getListId()).isInstanceOf(Long.class);
      assertThat(list.getListName()).isInstanceOf(String.class);
    }

    assertThat(response.get(0).getListId()).isEqualTo(1);
    assertThat(response.get(0).getListName()).isEqualTo("Groceries");
  }

  @Test
  @PactTestFor(
      providerName = "ListService",
      pactMethod = "givenNoToDoListsExist_whenGetAllToDoLists_thenReturnEmptyListPact",
      port = "8082")
  public void givenNoToDoListsExist_whenGetAllToDoLists_thenReturnEmptyListFromMockedListService(
      MockServer mockServer) throws Exception {
    List<ToDoListApiResponse> response = listServiceClient.getAllToDoLists();

    assertNotNull(response, "Response should not be null");
    assertTrue(response.isEmpty(), "Expected empty list, but found: " + response.size());
  }

  @Test
  @PactTestFor(
      providerName = "ListService",
      pactMethod = "givenDatabaseError_whenGetAllToDoLists_thenReturnErrorResponsePact",
      port = "8082")
  public void givenDatabaseError_whenGetAllToDoLists_thenReturnErrorResponseFromMockedListService(
      MockServer mockServer) {

    WebClientResponseException ex =
        assertThrows(
            WebClientResponseException.class,
            () -> {
              listServiceClient.getAllToDoLists();
            });

    assertThat(ex.getStatusCode().is5xxServerError());

    // Verify that the error message matches the expected message
    assertThat(ex.getResponseBodyAsString())
        .contains("An unexpected error occurred: java.lang.NullPointerException");
  }

  @Test
  @PactTestFor(
      providerName = "ListService",
      pactMethod = "givenValidToDoListCreateRequest_whenCreateList_thenReturnCreatedListPact",
      port = "8083")
  public void
      givenValidToDoListCreateRequest_whenCreateList_thenReturnCreatedListFromMockedListService(
          MockServer mockServer) throws Exception {

    // Create the request body
    String body =
        """
            {
                "listName": "Test List 1"
            }
        """;

    WebClient webClient =
        WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    // Send a POST request and retrieve the response
    ResponseEntity<String> response =
        webClient
            .post()
            .uri("/lists")
            .bodyValue(body)
            .retrieve()
            .toEntity(String.class)
            .block(); // Blocking to wait for the response

    // Assert that the response status code is 201 Created
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    // Parse the response body as JSON
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonResponse = objectMapper.readTree(response.getBody());

    // Assert that "listId" exists and is an integer
    assertThat(jsonResponse.has("listId")).isTrue();
    assertThat(jsonResponse.get("listId").isInt()).isTrue();

    // Assert that "listName" matches
    assertThat(jsonResponse.get("listName").asText()).isEqualTo("Test List 1");
  }

  @Test
  @PactTestFor(
      providerName = "ListService",
      pactMethod = "givenInvalidToDoListCreateRequest_whenCreateList_thenReturnErrorResponsePact",
      port = "8083")
  public void
      givenInvalidToDoListCreateRequest_whenCreateList_thenReturnErrorResponseFromMockedListService(
          MockServer mockServer) throws Exception {

    // Create the request body with invalid data
    String body =
        """
            {
                "list": "Test List 1"
            }
        """;

    WebClient webClient =
        WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    // Expecting an exception when calling the endpoint
    WebClientResponseException ex =
        assertThrows(
            WebClientResponseException.class,
            () -> {
              webClient
                  .post()
                  .uri("/lists")
                  .bodyValue(body)
                  .retrieve()
                  .bodyToMono(String.class)
                  .block(); // Blocking to trigger exception
            });

    assertThat(ex.getStatusCode().is5xxServerError());
    assertThat(ex.getResponseBodyAsString()).contains("An unexpected error occurred:");
  }

  @Test
  @PactTestFor(
      providerName = "ListService",
      pactMethod = "givenToDoListsExist_whenDeleteToDoListsExist_thenReturnNoContentPact",
      port = "8083")
  public void givenToDoListsExist_whenDeleteToDoListsExist_thenReturnNoContentFromMockedListService(
      MockServer mockServer) throws Exception {
    WebClient webClient = WebClient.builder().baseUrl(mockServer.getUrl()).build();

    // Perform DELETE request
    ResponseEntity<Void> response =
        webClient.delete().uri("/lists/1").retrieve().toBodilessEntity().block();

    // Assert the response is 204 No Content
    assertThat(response.getStatusCode().value()).isEqualTo(204);
  }

  @Test
  @PactTestFor(
      providerName = "ListService",
      pactMethod = "givenNoToDoListsExist_whenDeleteToDoListsExist_thenReturnNoContentPact",
      port = "8083")
  public void
      givenNoToDoListsExist_whenDeleteToDoListsExist_thenReturnNoContentFromMockedListService(
          MockServer mockServer) throws Exception {
    WebClient webClient = WebClient.builder().baseUrl(mockServer.getUrl()).build();

    // Expecting an exception when calling the endpoint
    WebClientResponseException ex =
        assertThrows(
            WebClientResponseException.class,
            () -> {
              webClient
                  .delete()
                  .uri("/lists/4")
                  .retrieve()
                  .bodyToMono(String.class)
                  .block(); // Blocking to trigger exception
            });

    assertThat(ex.getStatusCode().is4xxClientError());
    assertThat(ex.getResponseBodyAsString()).contains("ToDoList 4 not found");
  }
}
