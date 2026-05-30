package com.example.task_service.unit.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.task_service.dto.internal.request.TaskCreateRequest;
import com.example.task_service.dto.internal.response.TaskResponse;
import com.example.task_service.entity.Task;
import com.example.task_service.entity.TaskStatus;
import com.example.task_service.mapping.TaskMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskMapperTest {

  private TaskMapper taskMapper;

  Task mockTask = new Task();
  TaskCreateRequest mockTaskCreateRequest = new TaskCreateRequest();

  @BeforeEach
  void setUp() {
    taskMapper = new TaskMapper();

    mockTask.setTaskId(1L);
    mockTask.setListId(1L);
    mockTask.setTaskName("Test Task");
    mockTask.setTaskDate(LocalDate.of(2021, 1, 1));
    mockTask.setTaskStatus(TaskStatus.TO_DO);

    mockTaskCreateRequest.setListId(1L);
    mockTaskCreateRequest.setTaskName("Test Task");
    mockTaskCreateRequest.setTaskDate(LocalDate.of(2021, 1, 1));
    mockTaskCreateRequest.setTaskStatus("TO_DO");
  }

  @Test
  void givenTask_whenToTaskResponse_thenReturnTaskResponse() {
    TaskResponse taskResponse = taskMapper.toTaskResponse(mockTask);

    assertNotNull(taskResponse);
    assertEquals(1L, taskResponse.getTaskId());
    assertEquals(1L, taskResponse.getListId());
    assertEquals("Test Task", taskResponse.getTaskName());
    assertEquals(LocalDate.of(2021, 1, 1), taskResponse.getTaskDate());
    assertEquals("TO_DO", taskResponse.getTaskStatus());
  }

  @Test
  void givenTaskCreateRequest_whenToTask_thenReturnTask() {
    Task task = taskMapper.toTask(mockTaskCreateRequest);

    assertNotNull(task);
    assertEquals(1L, task.getListId());
    assertEquals("Test Task", task.getTaskName());
    assertEquals(LocalDate.of(2021, 1, 1), task.getTaskDate());
    assertEquals(TaskStatus.TO_DO, task.getTaskStatus());
  }
}
