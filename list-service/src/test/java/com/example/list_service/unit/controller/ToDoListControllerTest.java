package com.example.list_service.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.example.list_service.controller.ToDoListController;
import com.example.list_service.dto.internal.request.ToDoListCreateRequest;
import com.example.list_service.dto.internal.request.ToDoListUpdateRequest;
import com.example.list_service.dto.internal.response.ToDoListResponse;
import com.example.list_service.service.ToDoListService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ToDoListControllerTest {

  @InjectMocks private ToDoListController toDoListController;

  @Mock private ToDoListService toDoListService;

  private ToDoListResponse toDoListResponse;
  private List<ToDoListResponse> toDoListResponseList;

  @BeforeEach
  void setUp() {
    toDoListResponse = new ToDoListResponse();
    toDoListResponse.setListId((long) 1);
    toDoListResponse.setListName("Test List Name");

    toDoListResponseList = Arrays.asList(toDoListResponse);
  }

  @Test
  void givenTestEndpoint_whenGetTestEndpoint_thenReturnsTestEndpointResponse() {
    when(toDoListService.testEndpoint()).thenReturn("testEndpoint ListService");

    String response = toDoListController.testEndpoint();

    assertNotNull(response);
    assertEquals("testEndpoint ListService", response);
  }

  @Test
  void givenToDoLists_whenGetAllToDoLists_thenReturnsToDoLists() {
    when(toDoListService.getAllToDoLists()).thenReturn(toDoListResponseList);

    ResponseEntity<List<ToDoListResponse>> response = toDoListController.getAllToDoLists();

    assertEquals(HttpStatus.OK, response.getStatusCode());

    List<ToDoListResponse> responseBody = response.getBody();

    assertNotNull(responseBody);
    assertEquals(1, responseBody.size());
    assertEquals(1L, responseBody.get(0).getListId());
    assertEquals("Test List Name", responseBody.get(0).getListName());
  }

  @Test
  void givenToDoListIdExists_whenGetToDoListById_thenReturnsToDoList() {
    when(toDoListService.getToDoListById(any(Long.class))).thenReturn(toDoListResponse);

    ResponseEntity<ToDoListResponse> response = toDoListController.getToDoListById(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    ToDoListResponse responseBody = response.getBody();

    assertNotNull(responseBody);
    assertEquals(1L, responseBody.getListId());
    assertEquals("Test List Name", responseBody.getListName());
  }

  @Test
  void givenToDoListCreateRequest_whenCreateToDoList_thenReturnsCreatedToDoList() {
    when(toDoListService.createToDoList(any(ToDoListCreateRequest.class)))
        .thenReturn(toDoListResponse);

    ResponseEntity<ToDoListResponse> response =
        toDoListController.createToDoList(new ToDoListCreateRequest("Test List Name"));

    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    ToDoListResponse responseBody = response.getBody();

    assertNotNull(response);
    assertEquals(1L, responseBody.getListId());
    assertEquals("Test List Name", responseBody.getListName());
  }

  @Test
  void
      givenToDoListIdExistsAndValidToDoListUpdateRequest_whenUpdateToDoList_thenReturnsUpdatedToDoList() {
    ToDoListResponse toDoListUpdateResponse = new ToDoListResponse();
    toDoListUpdateResponse.setListId((long) 1);
    toDoListUpdateResponse.setListName("Updated List Name");

    when(toDoListService.updateToDoList(any(Long.class), any(ToDoListUpdateRequest.class)))
        .thenReturn(toDoListUpdateResponse);

    ResponseEntity<ToDoListResponse> response =
        toDoListController.updateToDoList(1L, new ToDoListUpdateRequest("Updated List Name"));

    assertEquals(HttpStatus.OK, response.getStatusCode());

    ToDoListResponse responseBody = response.getBody();

    assertNotNull(responseBody);
    assertEquals(1L, responseBody.getListId());
    assertEquals("Updated List Name", responseBody.getListName());
  }

  @Test
  void givenToDoListIdExists_whenDeleteToDoList_thenReturnsNoContent() {
    doNothing().when(toDoListService).deleteToDoList(any(Long.class));

    ResponseEntity<Void> response = toDoListController.deleteToDoList(1L);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
}
