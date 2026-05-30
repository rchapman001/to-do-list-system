package com.example.to_do_list_ui.contract;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.Map;
import org.junit.jupiter.api.Disabled;

public interface ListServicePact {

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "ListService")
  default V4Pact givenListServiceTest_whenGetTestEndpoint_thenReturnTestEndpointResponsePact(
      PactDslWithProvider builder) {
    return builder
        .given("ListServiceTest")
        .uponReceiving("GET /lists/test: GetTestEndpoint")
        .path("/lists/test") // Matching the endpoint in the client call
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "text/plain"))
        .body("testEndpoint ListService") // Expected response
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "ListService")
  default V4Pact givenToDoListsExist_whenGetAllToDoLists_thenReturnToDoListsPact(
      PactDslWithProvider builder) {

    DslPart body =
        PactDslJsonArray.arrayMinLike(1)
            .integerType("listId", 1)
            .stringType("listName", "Groceries")
            .closeObject();

    return builder
        .given("ToDoListsExist")
        .uponReceiving("GET /lists: GetAllToDoLists")
        .path("/lists")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json")) // Specify content type explicitly
        .body(body)
        .toPact(V4Pact.class);
  }

  /**
   * Creating a separate pact for when the provider does not have any ToDoLists. Doing this to
   * ensure the provider returns an empty list when there are no ToDoLists.
   */
  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "ListService")
  default V4Pact givenNoToDoListsExist_whenGetAllToDoLists_thenReturnEmptyListPact(
      PactDslWithProvider builder) {
    DslPart body = new PactDslJsonArray();

    return builder
        .given("NoToDoListsExist")
        .uponReceiving("GET /lists: GetAllToDoLists")
        .path("/lists")
        .method("GET")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json")) // Specify content type explicitly
        .body(body)
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "ListService")
  default V4Pact givenDatabaseError_whenGetAllToDoLists_thenReturnErrorResponsePact(
      PactDslWithProvider builder) {

    // Create the error response body
    DslPart errorResponseBody =
        new PactDslJsonBody()
            .stringType("message", "An unexpected error occurred: java.lang.NullPointerException");

    return builder
        .given("DatabaseError")
        .uponReceiving("GET /lists: GetAllToDoLists")
        .path("/lists")
        .method("GET")
        .willRespondWith()
        .status(500) // Internal Server Error
        .headers(Map.of("Content-Type", "application/json"))
        .body(errorResponseBody)
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "ListService")
  default V4Pact givenValidToDoListCreateRequest_whenCreateList_thenReturnCreatedListPact(
      PactDslWithProvider builder) {

    // Define the request body
    DslPart requestBody =
        new PactDslJsonBody()
            .stringValue(
                "listName",
                "Test List 1"); // Doing an exact match because I expect the listName in the request
    // and response to be the same

    DslPart responseBody =
        new PactDslJsonBody()
            .integerType("listId", 2)
            .stringValue(
                "listName",
                "Test List 1"); // Doing an exact match because I expect the listName in the request
    // and response to be the same

    return builder
        .given("ValidToDoListCreateRequest") // Define provider state with listName
        .uponReceiving("POST /lists: Create a new list")
        .path("/lists")
        .method("POST")
        .headers(Map.of("Content-Type", "application/json"))
        .body(requestBody)
        .willRespondWith()
        .status(201)
        .headers(Map.of("Content-Type", "application/json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "ListService")
  default V4Pact givenInvalidToDoListCreateRequest_whenCreateList_thenReturnErrorResponsePact(
      PactDslWithProvider builder) {

    // Define the request body
    DslPart requestBody =
        new PactDslJsonBody()
            .stringType(
                "list",
                "Test List 1"); // Key is incorrect. Doing type match because the value does not
    // matter for this test.

    // Create the error response body
    DslPart responseBody =
        new PactDslJsonBody().stringType("message", "An unexpected error occurred: ");

    return builder
        .given("InvalidToDoListCreateRequest") // Define provider state with listName
        .uponReceiving("POST /lists: Invalid request to create a new list")
        .path("/lists")
        .method("POST")
        .headers(Map.of("Content-Type", "application/json"))
        .body(requestBody)
        .willRespondWith()
        .status(500) // Should probably change to be a bad request not an internal server error
        .headers(Map.of("Content-Type", "application/json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "ListService")
  default V4Pact givenToDoListsExist_whenDeleteToDoListsExist_thenReturnNoContentPact(
      PactDslWithProvider builder) {

    return builder
        .given("ToDoListsExistDeleteRequest") // Define provider state
        .uponReceiving("DELETE /lists/4: Request to delete a list")
        .path("/lists/1")
        .method("DELETE")
        .willRespondWith()
        .status(204) // No Content on successful deletion
        .toPact(V4Pact.class);
  }

  @Disabled
  @Pact(consumer = "ToDoListUi", provider = "ListService")
  default V4Pact givenNoToDoListsExist_whenDeleteToDoListsExist_thenReturnNoContentPact(
      PactDslWithProvider builder) {

    // Create the error response body
    DslPart responseBody = new PactDslJsonBody().stringType("message", "ToDoList 4 not found");

    return builder
        .given("NoToDoListsExistDeleteRequest") // Define provider state
        .uponReceiving("DELETE /lists/4: Request to delete a list that doesn't exist")
        .path("/lists/4")
        .method("DELETE")
        .willRespondWith()
        .status(404) // No Content on successful deletion
        .headers(Map.of("Content-Type", "application/json"))
        .body(responseBody)
        .toPact(V4Pact.class);
  }
}
