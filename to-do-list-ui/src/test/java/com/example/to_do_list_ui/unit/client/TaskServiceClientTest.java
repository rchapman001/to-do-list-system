package com.example.to_do_list_ui.unit.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import com.example.to_do_list_ui.client.TaskServiceClient;
import com.example.to_do_list_ui.config.PactConfig;
import com.example.to_do_list_ui.contract.TaskServicePact;
import com.example.to_do_list_ui.dto.external.response.TaskApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@ActiveProfiles("test")
@Execution(ExecutionMode.SAME_THREAD)
@Import(PactConfig.class)
public class TaskServiceClientTest implements TaskServicePact {

  @Autowired private TaskServiceClient taskServiceClient;

  @Test
  @PactTestFor(
      providerName = "TaskService",
      pactMethod = "givenTaskServiceTest_whenGetTestEndpoint_thenReturnTestEndpointResponsePact",
      port = "8083")
  public void
      givenTaskServiceTest_whenGetTestEndpoint_thenReturnTestEndpointResponseFromMockedTaskService(
          MockServer mockServer) {
    String response = taskServiceClient.testEndpointTask(); // Blocking call for testing

    assertThat(response).isNotNull();
    assertThat(response).isEqualTo("testEndpoint TaskService");
  }

  @Test
  @PactTestFor(
      providerName = "TaskService",
      pactMethod = "givenTaskWithListIdExist_whenGetTasksByListId_thenReturnTasksPact",
      port = "8083")
  public void givenTaskWithListIdExist_whenGetTasksByListId_thenReturnTasksFromMockedTaskService(
      MockServer mockServer) throws Exception {
    List<TaskApiResponse> response = taskServiceClient.getTasksByListId(1L);

    for (TaskApiResponse task : response) {
      assertThat(task.getTaskId()).isInstanceOf(Long.class);
      assertThat(task.getListId()).isInstanceOf(Long.class);
      assertThat(task.getTaskName()).isInstanceOf(String.class);
      assertThat(task.getTaskDate()).isInstanceOf(LocalDate.class);
      assertThat(task.getTaskStatus()).isInstanceOf(String.class);
    }
  }

  @Test
  @PactTestFor(
      providerName = "TaskService",
      pactMethod = "givenNoTaskWithListIdExist_whenGetTasksByListId_thenReturnErrorResponsePact",
      port = "8083")
  public void
      givenNoTaskWithListIdExist_whenGetTasksByListId_thenReturnErrorResponseFromMockedTaskService(
          MockServer mockServer) {

    WebClientResponseException ex =
        assertThrows(
            WebClientResponseException.class,
            () -> {
              taskServiceClient.getTasksByListId(999L);
            });

    assertThat(ex.getStatusCode().is4xxClientError());

    // Verify that the error message matches the expected message
    assertThat(ex.getResponseBodyAsString()).contains("No tasks found for list ID: 999");
  }

  @Test
  @PactTestFor(
      providerName = "TaskService",
      pactMethod =
          "givenStatusOptionsExist_whenGetTaskStatusOptions_thenReturnTaskStatusOptionsPact",
      port = "8083")
  public void
      givenStatusOptionsExist_whenGetTaskStatusOptions_thenReturnTaskStatusOptionsPactFromMockedTaskService(
          MockServer mockServer) {
    // Create WebClient with mock server URL
    WebClient webClient = WebClient.builder().baseUrl(mockServer.getUrl()).build();

    // Call the endpoint
    List<String> response =
        webClient
            .get()
            .uri("/tasks/status-options")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
            .block();

    assertNotNull(response);
    assertEquals(3, response.size());

    for (String option : response) {
      assertThat(option).isInstanceOf(String.class);
    }
  }

  @Test
  @PactTestFor(
      providerName = "TaskService",
      pactMethod = "givenValidTaskCreateRequest_whenCreateTask_thenReturnCreatedTaskPact",
      port = "8083")
  public void givenValidTaskCreateRequest_whenCreateTask_thenReturnCreatedTaskFromMockedTaskService(
      MockServer mockServer) throws Exception {

    // Create WebClient
    WebClient webClient =
        WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    // Create the request body
    String body =
        """
              {
                  "listId": 2,
                  "taskName": "Test Task",
                  "taskDate": "2024-10-15",
                  "taskStatus": "TO_DO"
              }
          """;

    // Perform the POST request using WebClient
    String actualResponse =
        webClient
            .post()
            .uri("/tasks")
            .bodyValue(body)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response ->
                    Mono.error(
                        new RuntimeException(
                            "Request failed with status: " + response.statusCode())))
            .bodyToMono(String.class)
            .block();

    // Assert response is not null
    assertNotNull(actualResponse);

    // Parse the response JSON
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualJsonNode = objectMapper.readTree(actualResponse);

    // Assert that the values match expected data
    assertEquals(
        5, actualJsonNode.path("taskId").asInt()); // Assuming this should be the taskId returned
    assertEquals(
        2,
        actualJsonNode.path("listId").asInt()); // Assuming this should be the listId we passed in
    assertEquals("Test Task", actualJsonNode.path("taskName").asText()); // Check taskName matches
    assertEquals("2024-10-15", actualJsonNode.path("taskDate").asText()); // Check taskDate matches
    assertEquals("TO_DO", actualJsonNode.path("taskStatus").asText()); // Check taskStatus matches
  }

  @Test
  @PactTestFor(
      providerName = "TaskService",
      pactMethod = "givenInvalidTaskCreateRequest_whenCreateTask_thenReturnCreatedTaskPact",
      port = "8083")
  public void
      givenInvalidTaskCreateRequest_whenCreateTask_thenReturnCreatedTaskFromMockedTaskService(
          MockServer mockServer) throws Exception {

    // Create the request body with invalid data
    String body =
        """
              {
                  "list": 2,
                  "task": "Test Task",
                  "date": "2024-10-15",
                  "status": "TO_DO"
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
                  .uri("/tasks")
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
      providerName = "TaskService",
      pactMethod = "givenValidDeleteTaskRequest_whenDeleteTask_thenReturnNoContentPact",
      port = "8083")
  public void givenValidDeleteTaskRequest_whenDeleteTask_thenReturnNoContentFromMockedTaskService(
      MockServer mockServer) throws Exception {
    WebClient webClient = WebClient.builder().baseUrl(mockServer.getUrl()).build();

    // Perform DELETE request
    ResponseEntity<Void> response =
        webClient.delete().uri("/tasks/1").retrieve().toBodilessEntity().block();

    // Assert the response is 204 No Content
    assertThat(response.getStatusCode().value()).isEqualTo(204);
  }

  @Test
  @PactTestFor(
      providerName = "TaskService",
      pactMethod = "givenInvalidDeleteTaskRequest_whenDeleteTask_thenReturnErrorResponsePact",
      port = "8083")
  public void givenInvalidDeleteTaskRequest_whenDeleteTask_thenReturnNoContentFromMockedService(
      MockServer mockServer) throws Exception {
    WebClient webClient = WebClient.builder().baseUrl(mockServer.getUrl()).build();

    // Expecting an exception when calling the endpoint
    WebClientResponseException ex =
        assertThrows(
            WebClientResponseException.class,
            () -> {
              webClient
                  .delete()
                  .uri("/tasks/999")
                  .retrieve()
                  .bodyToMono(String.class)
                  .block(); // Blocking to trigger exception
            });

    assertThat(ex.getStatusCode().is4xxClientError());
    assertThat(ex.getResponseBodyAsString()).contains("Task 999 not found");
  }

  @Test
  @PactTestFor(
      providerName = "TaskService",
      pactMethod =
          "givenValidDeleteAllTasksByListIdRequest_whenDeleteAllTasksByListId_thenReturnNoContentPact",
      port = "8083")
  public void
      givenValidDeleteAllTasksByListIdRequest_whenDeleteAllTasksByListId_thenReturnNoContentFromMockedTaskService(
          MockServer mockServer) throws Exception {
    WebClient webClient = WebClient.builder().baseUrl(mockServer.getUrl()).build();

    // Perform DELETE request
    ResponseEntity<Void> response =
        webClient.delete().uri("/tasks/list/1").retrieve().toBodilessEntity().block();

    // Assert the response is 204 No Content
    assertThat(response.getStatusCode().value()).isEqualTo(204);
  }

  @Test
  @PactTestFor(
      providerName = "TaskService",
      pactMethod =
          "givenInvalidDeleteAllTasksByListIdRequest_whenDeleteAllTasksByListId_thenReturnErrorResponsePact",
      port = "8083")
  public void
      givenInvalidDeleteAllTasksByListIdRequest_whenDeleteAllTasksByListId_thenReturnErrorResponseFromMockedTaskService(
          MockServer mockServer) throws Exception {
    WebClient webClient = WebClient.builder().baseUrl(mockServer.getUrl()).build();

    // Expecting an exception when calling the endpoint
    WebClientResponseException ex =
        assertThrows(
            WebClientResponseException.class,
            () -> {
              webClient
                  .delete()
                  .uri("/tasks/list/999")
                  .retrieve()
                  .bodyToMono(String.class)
                  .block(); // Blocking to trigger exception
            });

    assertThat(ex.getStatusCode().is4xxClientError());
    assertThat(ex.getResponseBodyAsString()).contains("ToDoList 999 not found");
  }
}
