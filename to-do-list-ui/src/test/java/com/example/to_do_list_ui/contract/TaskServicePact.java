package com.example.to_do_list_ui.contract;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.Map;
import org.junit.jupiter.api.Disabled;

public interface TaskServicePact {

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "TaskService")
  default V4Pact givenTaskServiceTest_whenGetTestEndpoint_thenReturnTestEndpointResponsePact(
      PactDslWithProvider builder) {
    return builder
        .given("TaskServiceTest")
        .uponReceiving("GET /tasks/test: GetTestEndpoint")
        .path("/tasks/test") // Matching the endpoint in the client call
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "text/plain"))
        .body("testEndpoint TaskService") // Expected response
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "TaskService")
  default V4Pact givenTaskWithListIdExist_whenGetTasksByListId_thenReturnTasksPact(
      PactDslWithProvider builder) {

    DslPart responseBody =
        PactDslJsonArray.arrayMinLike(1)
            .integerType("taskId", 1) // Doing an integerType because the ID can be any number
            .equalTo(
                "listId",
                1) // Doing an exact match because the listId should be the same as the request path
            // parameter
            .stringType(
                "taskName", "Buy milk") // Doing a stringType because the name can be any string
            .date("taskDate", "yyyy-MM-dd") // TODO: provide an example date "2000-01-31"
            .stringType(
                "taskStatus",
                "TO_DO") // Doing a stringType even though the status should be a specific value
            // because even if the status options are changed it won't break the
            // consumer because I don't have any logic that depends on the status
            // value.
            .closeObject();

    return builder
        .given("TaskWithListIdExist")
        .uponReceiving("GET /tasks/list/{id}: GetTasksByListId")
        .path("/tasks/list/1")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "TaskService")
  default V4Pact givenNoTaskWithListIdExist_whenGetTasksByListId_thenReturnErrorResponsePact(
      PactDslWithProvider builder) {

    // Create the error response body
    DslPart responseBody =
        new PactDslJsonBody().stringType("message", "No tasks found for list ID: 999");

    return builder
        .given("NoTaskWithListIdExist")
        .uponReceiving("GET /tasks/list/{id}: GetTasksByListId")
        .path("/tasks/list/999")
        .method("GET")
        .willRespondWith()
        .status(404)
        .headers(Map.of("Content-Type", "application/json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "TaskService")
  default V4Pact givenStatusOptionsExist_whenGetTaskStatusOptions_thenReturnTaskStatusOptionsPact(
      PactDslWithProvider builder) {

    DslPart responseBody = PactDslJsonArray.arrayMinLike(3, PactDslJsonRootValue.stringType());

    return builder
        .given("StatusOptionsExist")
        .uponReceiving("GET /tasks/status-options")
        .path("/tasks/status-options")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "TaskService")
  default V4Pact givenValidTaskCreateRequest_whenCreateTask_thenReturnCreatedTaskPact(
      PactDslWithProvider builder) {
    // Create the request body (the data that will be posted to create a new task)
    DslPart requestBody =
        new PactDslJsonBody()
            .numberValue("listId", 2) // Exact match to make sure the same in the response
            .stringValue(
                "taskName", "Test Task") // Exact match to make sure the same in the response
            .stringValue(
                "taskDate", "2024-10-15") // Exact match to make sure the same in the response
            .stringValue(
                "taskStatus", "TO_DO"); // / Exact match to make sure the same in the response

    // Create the response body (what the service should return after the post)
    DslPart responseBody =
        new PactDslJsonBody()
            .integerType(
                "taskId", 5) // The ID of the newly created task (example from your response)
            .numberValue("listId", 2) // Exact match to make sure the same in the request
            .stringValue(
                "taskName", "Test Task") // Exact match to make sure the same in the response
            .stringValue(
                "taskDate", "2024-10-15") // Exact match to make sure the same in the response
            .stringValue(
                "taskStatus", "TO_DO"); // Exact match to make sure the same in the response

    return builder
        .given("ValidTaskCreateRequest")
        .uponReceiving("POST /tasks: ValidTaskCreateRequest")
        .path("/tasks")
        .method("POST")
        .headers(Map.of("Content-Type", "application/json"))
        .body(requestBody)
        .willRespondWith()
        .status(201)
        .headers(Map.of("Content-Type", "application/json")) // Specify content type explicitly
        .body(responseBody)
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "TaskService")
  default V4Pact givenInvalidTaskCreateRequest_whenCreateTask_thenReturnCreatedTaskPact(
      PactDslWithProvider builder) {

    // Create the request body (the data that will be posted to create a new task)
    DslPart requestBody =
        new PactDslJsonBody()
            .numberValue("list", 2) // Incorrect key "list" should be "listId"
            .stringValue("task", "Test Task") // Incorrect key "task" should be "taskName"
            .stringValue("date", "2024-10-15") // Incorrect key "date" should be "taskDate"
            .stringValue("status", "TO_DO"); // Incorrect key "status" should be "taskStatus"

    // Create the error response body
    DslPart responseBody =
        new PactDslJsonBody().stringType("message", "An unexpected error occurred: ");

    return builder
        .given("InvalidTaskCreateRequest")
        .uponReceiving("POST /tasks: InvalidTaskCreateRequest")
        .path("/tasks")
        .method("POST")
        .headers(Map.of("Content-Type", "application/json"))
        .body(requestBody)
        .willRespondWith()
        .status(500)
        .headers(Map.of("Content-Type", "application/json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "TaskService")
  default V4Pact givenValidDeleteTaskRequest_whenDeleteTask_thenReturnNoContentPact(
      PactDslWithProvider builder) {

    return builder
        .given("ValidDeleteTaskRequest") // Define provider state
        .uponReceiving("DELETE /tasks/1: ValidDeleteTaskRequest")
        .path("/tasks/1")
        .method("DELETE")
        .willRespondWith()
        .status(204) // No Content on successful deletion
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "TaskService")
  default V4Pact givenInvalidDeleteTaskRequest_whenDeleteTask_thenReturnErrorResponsePact(
      PactDslWithProvider builder) {

    // Create the error response body
    DslPart responseBody = new PactDslJsonBody().stringType("message", "Task 999 not found");

    return builder
        .given("InvalidDeleteTaskRequest") // Define provider state
        .uponReceiving("DELETE /tasks/999: InvalidDeleteTaskRequest")
        .path("/tasks/999")
        .method("DELETE")
        .willRespondWith()
        .status(404) // No Content on successful deletion
        .headers(Map.of("Content-Type", "application/json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "TaskService")
  default V4Pact
      givenValidDeleteAllTasksByListIdRequest_whenDeleteAllTasksByListId_thenReturnNoContentPact(
          PactDslWithProvider builder) {

    return builder
        .given("ValidDeleteAllTasksByListIdRequest") // Define provider state
        .uponReceiving("DELETE /tasks/list/1: ValidDeleteAllTasksByListIdRequest")
        .path("/tasks/list/1")
        .method("DELETE")
        .willRespondWith()
        .status(204) // No Content on successful deletion
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "TaskService")
  default V4Pact
      givenInvalidDeleteAllTasksByListIdRequest_whenDeleteAllTasksByListId_thenReturnErrorResponsePact(
          PactDslWithProvider builder) {

    // Create the error response body
    DslPart responseBody = new PactDslJsonBody().stringType("message", "ToDoList 999 not found");

    return builder
        .given("InvalidDeleteAllTasksByListIdRequest") // Define provider state
        .uponReceiving("DELETE /tasks/999: InvalidDeleteAllTasksByListIdRequest")
        .path("/tasks/list/999")
        .method("DELETE")
        .willRespondWith()
        .status(404) // No Content on successful deletion
        .headers(Map.of("Content-Type", "application/json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }
}
