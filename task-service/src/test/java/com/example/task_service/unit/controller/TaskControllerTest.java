package com.example.task_service.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.example.task_service.controller.TaskController;
import com.example.task_service.dto.internal.request.TaskCreateRequest;
import com.example.task_service.dto.internal.request.TaskUpdateRequest;
import com.example.task_service.dto.internal.response.TaskResponse;
import com.example.task_service.service.TaskService;
import java.time.LocalDate;
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
public class TaskControllerTest {

  @InjectMocks private TaskController taskController;

  @Mock private TaskService taskService;

  private TaskResponse mockTaskResponse;
  private List<TaskResponse> mockTaskResponseList;
  private List<String> mockTaskStatusOptions;

  @BeforeEach
  void setUp() {
    mockTaskResponse = new TaskResponse();
    mockTaskResponse.setTaskId(1L);
    mockTaskResponse.setListId(1L);
    mockTaskResponse.setTaskDate(LocalDate.of(2021, 1, 1));
    mockTaskResponse.setTaskStatus("TO_DO");
    mockTaskResponse.setTaskName("Task 1");

    mockTaskResponseList = Arrays.asList(mockTaskResponse);

    mockTaskStatusOptions = List.of("TO_DO", "IN_PROGRESS", "DONE");
  }

  @Test
  void givenTestEndpoint_whenGetTestEndpoint_thenReturnsTestEndpointResponse() {
    when(taskService.testEndpoint()).thenReturn("testEndpoint TaskService");

    String response = taskController.testEndpoint();

    assertNotNull(response);
    assertEquals("testEndpoint TaskService", response);
  }

  @Test
  void givenTasks_whenGetAllTasks_thenReturnsTasks() {
    when(taskService.getAllTasks()).thenReturn(mockTaskResponseList);

    ResponseEntity<List<TaskResponse>> response = taskController.getAllTasks();

    assertEquals(HttpStatus.OK, response.getStatusCode());

    List<TaskResponse> responseBody = response.getBody();

    assertNotNull(responseBody);
    assertEquals(1, responseBody.size());
    assertEquals(1L, responseBody.get(0).getTaskId());
    assertEquals(1L, responseBody.get(0).getListId());
    assertEquals("2021-01-01", responseBody.get(0).getTaskDate().toString());
    assertEquals("TO_DO", responseBody.get(0).getTaskStatus());
    assertEquals("Task 1", responseBody.get(0).getTaskName());
  }

  @Test
  void givenTaskExists_whenGetTaskById_thenReturnsTask() {
    when(taskService.getTaskById(any(Long.class))).thenReturn(mockTaskResponse);

    ResponseEntity<TaskResponse> response = taskController.getTaskById(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    TaskResponse responseBody = response.getBody();

    assertNotNull(responseBody);
    assertEquals(1L, responseBody.getTaskId());
    assertEquals(1L, responseBody.getListId());
    assertEquals("2021-01-01", responseBody.getTaskDate().toString());
    assertEquals("TO_DO", responseBody.getTaskStatus());
    assertEquals("Task 1", responseBody.getTaskName());
  }

  @Test
  void givenListAndTaskExist_whenGetTasksByListId_thenReturnsTasks() {
    when(taskService.getTasksByListId(any(Long.class))).thenReturn(mockTaskResponseList);

    ResponseEntity<List<TaskResponse>> response = taskController.getTasksByListId(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    List<TaskResponse> responseBody = response.getBody();

    assertNotNull(responseBody);
    assertEquals(1, responseBody.size());
    assertEquals(1L, responseBody.get(0).getTaskId());
    assertEquals(1L, responseBody.get(0).getListId());
    assertEquals("2021-01-01", responseBody.get(0).getTaskDate().toString());
    assertEquals("TO_DO", responseBody.get(0).getTaskStatus());
    assertEquals("Task 1", responseBody.get(0).getTaskName());
  }

  @Test
  void givenTaskStatus_whenGetTaskStatusOptions_thenReturnsTaskStatusOptions() {
    when(taskService.getTaskStatusOptions()).thenReturn(mockTaskStatusOptions);

    ResponseEntity<List<String>> response = taskController.getTaskStatusOptions();

    assertEquals(HttpStatus.OK, response.getStatusCode());

    List<String> responseBody = response.getBody();

    assertNotNull(responseBody);
    assertEquals(3, responseBody.size());
    assertEquals("TO_DO", responseBody.get(0));
    assertEquals("IN_PROGRESS", responseBody.get(1));
    assertEquals("DONE", responseBody.get(2));
  }

  @Test
  void givenTaskCreateRequest_whenCreateTask_thenReturnsCreatedTask() {
    when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(mockTaskResponse);

    ResponseEntity<TaskResponse> response =
        taskController.createTask(
            new TaskCreateRequest(1L, "Task 1", LocalDate.of(2021, 1, 1), "TO_DO"));

    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    TaskResponse responseBody = response.getBody();

    assertNotNull(responseBody);
    assertEquals(1L, responseBody.getTaskId());
    assertEquals(1L, responseBody.getListId());
    assertEquals("2021-01-01", responseBody.getTaskDate().toString());
    assertEquals("TO_DO", responseBody.getTaskStatus());
    assertEquals("Task 1", responseBody.getTaskName());
  }

  @Test
  void givenTaskIdExistsAndValidTaskUpdateRequest_whenUpdateTask_thenReturnsUpdatedTask() {
    TaskResponse mockTaskUpdatedResponse = new TaskResponse();
    mockTaskUpdatedResponse.setTaskId(1L);
    mockTaskUpdatedResponse.setListId(1L);
    mockTaskUpdatedResponse.setTaskDate(LocalDate.of(2021, 1, 1));
    mockTaskUpdatedResponse.setTaskStatus("IN_PROGRESS");
    mockTaskUpdatedResponse.setTaskName("Updated Task 1");

    when(taskService.updateTask(any(Long.class), any(TaskUpdateRequest.class)))
        .thenReturn(mockTaskUpdatedResponse);

    ResponseEntity<TaskResponse> response =
        taskController.updateTask(
            1L,
            new TaskUpdateRequest(1L, "Updated Task 1", LocalDate.of(2021, 1, 1), "IN_PROGRESS"));

    assertEquals(HttpStatus.OK, response.getStatusCode());

    TaskResponse responseBody = response.getBody();

    assertNotNull(responseBody);
    assertEquals(1L, responseBody.getTaskId());
    assertEquals(1L, responseBody.getListId());
    assertEquals("2021-01-01", responseBody.getTaskDate().toString());
    assertEquals("IN_PROGRESS", responseBody.getTaskStatus());
    assertEquals("Updated Task 1", responseBody.getTaskName());
  }

  @Test
  void givenTaskIdExists_whenDeleteTask_thenReturnsNoContent() {
    doNothing().when(taskService).deleteTask(any(Long.class));

    ResponseEntity<Void> response = taskController.deleteTask(1L);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void givenListIdExists_whenDeleteAllTasksByListId_thenReturnsNoContent() {
    doNothing().when(taskService).deleteAllTasksByListId(any(Long.class));

    ResponseEntity<Void> response = taskController.deleteAllTasksByListId(1L);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
}
