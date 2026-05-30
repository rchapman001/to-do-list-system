package com.example.task_service.unit.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.task_service.dto.internal.request.TaskCreateRequest;
import com.example.task_service.dto.internal.request.TaskUpdateRequest;
import com.example.task_service.dto.internal.response.TaskResponse;
import com.example.task_service.entity.Task;
import com.example.task_service.entity.TaskStatus;
import com.example.task_service.exception.ResourceNotFoundException;
import com.example.task_service.mapping.TaskMapper;
import com.example.task_service.repository.TaskRepository;
import com.example.task_service.service.TaskService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

  @Mock private TaskRepository taskRepository;

  @Mock private TaskMapper taskMapper;

  @InjectMocks private TaskService taskService;

  Task mockTask = new Task();
  List<Task> mockTasks = new ArrayList<>();
  TaskStatus mockTaskStatus;
  TaskResponse mockTaskResponse = new TaskResponse();
  TaskCreateRequest mockTaskCreateRequest = new TaskCreateRequest();
  TaskUpdateRequest mockTaskUpdateRequest = new TaskUpdateRequest();

  @BeforeEach
  void setUp() {
    mockTaskStatus = TaskStatus.TO_DO;

    mockTask.setTaskId(1L);
    mockTask.setListId(1L);
    mockTask.setTaskName("Test Task");
    mockTask.setTaskDate(LocalDate.of(2021, 1, 1));
    mockTask.setTaskStatus(mockTaskStatus);

    mockTasks.add(mockTask);

    mockTaskResponse.setTaskId(1L);
    mockTaskResponse.setListId(1L);
    mockTaskResponse.setTaskName("Test Task");
    mockTaskResponse.setTaskDate(LocalDate.of(2021, 1, 1));
    mockTaskResponse.setTaskStatus("TO_DO");

    mockTaskCreateRequest.setListId(1L);
    mockTaskCreateRequest.setTaskName("Test Task");
    mockTaskCreateRequest.setTaskDate(LocalDate.of(2021, 1, 1));
    mockTaskCreateRequest.setTaskStatus("TO_DO");

    mockTaskUpdateRequest.setListId(1L);
    mockTaskUpdateRequest.setTaskName("Updated Task");
    mockTaskUpdateRequest.setTaskDate(LocalDate.of(2021, 1, 1));
    mockTaskUpdateRequest.setTaskStatus("TO_DO");
  }

  @Test
  void givenTestEndpoint_whenGetTestEndpoint_thenReturnsTestEndpointResponse() {
    String response = taskService.testEndpoint();

    assertNotNull(response);
    assertEquals("testEndpoint TaskService", response);
  }

  @Test
  void givenTasks_whenGetAllTasks_thenReturnTaskResponses() {
    when(taskRepository.findAll()).thenReturn(mockTasks);
    when(taskMapper.toTaskResponse(any(Task.class))).thenReturn(mockTaskResponse);

    List<TaskResponse> taskResponses = taskService.getAllTasks();

    assertNotNull(taskResponses);
    assertEquals(1, taskResponses.size());
    assertEquals(1L, taskResponses.get(0).getTaskId());
    assertEquals(1L, taskResponses.get(0).getListId());
    assertEquals("Test Task", taskResponses.get(0).getTaskName());
    assertEquals(LocalDate.of(2021, 1, 1), taskResponses.get(0).getTaskDate());
    assertEquals("TO_DO", taskResponses.get(0).getTaskStatus());
  }

  @Test
  void givenTaskIdExists_whenGetTaskById_thenReturnTaskResponse() {
    when(taskRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockTask));
    when(taskMapper.toTaskResponse(any(Task.class))).thenReturn(mockTaskResponse);

    TaskResponse taskResponse = taskService.getTaskById(1L);

    assertNotNull(taskResponse);
    assertEquals(1L, taskResponse.getTaskId());
    assertEquals(1L, taskResponse.getListId());
    assertEquals("Test Task", taskResponse.getTaskName());
    assertEquals(LocalDate.of(2021, 1, 1), taskResponse.getTaskDate());
    assertEquals("TO_DO", taskResponse.getTaskStatus());
  }

  @Test
  void givenTaskIdDoesNotExist_whenGetTaskById_thenThrowResourceNotFoundException() {
    when(taskRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L));
  }

  @Test
  void givenListAndTaskExist_whenGetTasksByListId_thenReturnsTasks() {
    when(taskRepository.findTasksByListId(anyLong())).thenReturn(mockTasks);
    when(taskMapper.toTaskResponse(any(Task.class))).thenReturn(mockTaskResponse);

    List<TaskResponse> taskResponses = taskService.getTasksByListId(1L);

    assertNotNull(taskResponses);
    assertEquals(1, taskResponses.size());
    assertEquals(1L, taskResponses.get(0).getTaskId());
    assertEquals(1L, taskResponses.get(0).getListId());
    assertEquals("Test Task", taskResponses.get(0).getTaskName());
    assertEquals(LocalDate.of(2021, 1, 1), taskResponses.get(0).getTaskDate());
    assertEquals("TO_DO", taskResponses.get(0).getTaskStatus());
  }

  @Test
  void givenListDoesNotExist_whenGetTasksByListId_thenThrowResourceNotFoundException() {
    when(taskRepository.findTasksByListId(anyLong())).thenReturn(new ArrayList<>());

    assertThrows(ResourceNotFoundException.class, () -> taskService.getTasksByListId(1L));
  }

  @Test
  void givenTaskStatus_whenGetTaskStatusOptions_thenReturnsTaskStatusOptions() {
    List<String> taskStatusOptions = taskService.getTaskStatusOptions();

    assertNotNull(taskStatusOptions);
    assertEquals(3, taskStatusOptions.size());
    assertEquals("TO_DO", taskStatusOptions.get(0));
    assertEquals("IN_PROGRESS", taskStatusOptions.get(1));
    assertEquals("DONE", taskStatusOptions.get(2));
  }

  @Test
  void givenTaskCreateRequest_whenCreateTask_thenReturnsCreatedTask() {
    when(taskMapper.toTask(any(TaskCreateRequest.class))).thenReturn(mockTask);
    when(taskRepository.save(any(Task.class))).thenReturn(mockTask);
    when(taskMapper.toTaskResponse(any(Task.class))).thenReturn(mockTaskResponse);

    TaskResponse taskResponse = taskService.createTask(mockTaskCreateRequest);

    assertNotNull(taskResponse);
    assertEquals(1L, taskResponse.getTaskId());
    assertEquals(1L, taskResponse.getListId());
    assertEquals("Test Task", taskResponse.getTaskName());
    assertEquals(LocalDate.of(2021, 1, 1), taskResponse.getTaskDate());
    assertEquals("TO_DO", taskResponse.getTaskStatus());
  }

  @Test
  void givenTaskIdExistsAndValidTaskUpdateRequest_whenUpdateTask_thenReturnsUpdatedTask() {
    when(taskRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockTask));
    when(taskRepository.save(any(Task.class))).thenReturn(mockTask);
    when(taskMapper.toTaskResponse(any(Task.class))).thenReturn(mockTaskResponse);

    TaskResponse taskResponse = taskService.updateTask(1L, mockTaskUpdateRequest);

    assertNotNull(taskResponse);
    assertEquals(1L, taskResponse.getTaskId());
    assertEquals(1L, taskResponse.getListId());
    assertEquals("Test Task", taskResponse.getTaskName());
    assertEquals(LocalDate.of(2021, 1, 1), taskResponse.getTaskDate());
    assertEquals("TO_DO", taskResponse.getTaskStatus());
  }

  @Test
  void
      givenTaskIdDoesNotExistAndValidTaskUpdateRequest_whenUpdateTask_thenReturnsResourceNotFound() {
    when(taskRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> taskService.updateTask(1L, mockTaskUpdateRequest));
  }

  @Test
  void givenTaskIdExists_whenDeleteTask_thenDoesNotThrowException() {
    when(taskRepository.existsById(anyLong())).thenReturn(true);

    assertDoesNotThrow(() -> taskService.deleteTask(1L));

    verify(taskRepository, times(1)).deleteById(1L);
  }

  @Test
  void givenInvalidId_whenDeleteTask_thenThrowResourceNotFoundException() {
    when(taskRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L));
  }

  @Test
  void givenListIdExists_whenDeleteAllTasksByListId_thenReturnsNoContent() {
    when(taskRepository.findTasksByListId(anyLong())).thenReturn(mockTasks);

    assertDoesNotThrow(() -> taskService.deleteAllTasksByListId(1L));

    verify(taskRepository, times(1)).deleteAll(mockTasks);
  }

  @Test
  void givenListIdDoesNotExist_whenDeleteAllTasksByListId_thenReturnsResourceNotFound() {
    when(taskRepository.findTasksByListId(anyLong())).thenReturn(new ArrayList<>());

    assertThrows(ResourceNotFoundException.class, () -> taskService.deleteAllTasksByListId(1L));
  }
}
