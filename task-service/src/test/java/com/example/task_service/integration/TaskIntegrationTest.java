package com.example.task_service.integration;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.task_service.config.TestContainerConfig;
import com.example.task_service.entity.Task;
import com.example.task_service.entity.TaskStatus;
import com.example.task_service.repository.TaskRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Execution(ExecutionMode.SAME_THREAD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private TaskRepository taskRepository;

  @Autowired private PostgreSQLContainer<?> postgreSQLContainer;

  Task task1 = new Task();
  Task task2 = new Task();
  Task task3 = new Task();
  Task task4 = new Task();

  @BeforeEach
  void setUp() {
    taskRepository.deleteAll();

    task1.setListId(1l);
    task1.setTaskName("Buy milk");
    task1.setTaskDate(LocalDate.of(2021, 1, 1));
    task1.setTaskStatus(TaskStatus.TO_DO);
    taskRepository.save(task1);

    task2.setListId(1l);
    task2.setTaskName("Buy bread");
    task2.setTaskDate(LocalDate.of(2021, 1, 1));
    task2.setTaskStatus(TaskStatus.TO_DO);
    taskRepository.save(task2);

    task3.setListId(2l);
    task3.setTaskName("Finish report");
    task3.setTaskDate(LocalDate.of(2021, 1, 1));
    task3.setTaskStatus(TaskStatus.TO_DO);
    taskRepository.save(task3);

    task4.setListId(3l);
    task4.setTaskName("Plan vacation");
    task4.setTaskDate(LocalDate.of(2021, 1, 1));
    task4.setTaskStatus(TaskStatus.TO_DO);
    taskRepository.save(task4);
  }

  @Test
  void givenTasks_whenGetAllTasks_thenReturnsTasks() throws Exception {
    mockMvc
        .perform(get("/tasks").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0].taskName", is("Buy milk")))
        .andExpect(jsonPath("$[0].taskDate", is("2021-01-01")))
        .andExpect(jsonPath("$[0].taskStatus", is("TO_DO")))
        .andExpect(jsonPath("$[0].listId", is(1)))
        .andExpect(jsonPath("$[1].taskName", is("Buy bread")))
        .andExpect(jsonPath("$[1].taskDate", is("2021-01-01")))
        .andExpect(jsonPath("$[1].taskStatus", is("TO_DO")))
        .andExpect(jsonPath("$[1].listId", is(1)))
        .andExpect(jsonPath("$[2].taskName", is("Finish report")))
        .andExpect(jsonPath("$[2].taskDate", is("2021-01-01")))
        .andExpect(jsonPath("$[2].taskStatus", is("TO_DO")))
        .andExpect(jsonPath("$[2].listId", is(2)))
        .andExpect(jsonPath("$[3].taskName", is("Plan vacation")))
        .andExpect(jsonPath("$[3].taskDate", is("2021-01-01")))
        .andExpect(jsonPath("$[3].taskStatus", is("TO_DO")))
        .andExpect(jsonPath("$[3].listId", is(3)));
  }

  @Test
  void givenDatabaseError_whenGetAllTasks_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(get("/tasks").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }

  @Test
  void givenTaskId_whenGetTaskById_thenReturnsTask() throws Exception {
    Task task = taskRepository.findAll().get(0);

    mockMvc
        .perform(get("/tasks/" + task.getTaskId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.taskName").value(task.getTaskName()))
        .andExpect(jsonPath("$.taskDate").value("2021-01-01"))
        .andExpect(jsonPath("$.taskStatus").value("TO_DO"))
        .andExpect(jsonPath("$.listId").value(1));
  }

  @Test
  void givenInvalidTaskId_whenGetTaskById_thenReturnsNotFound() throws Exception {
    mockMvc
        .perform(get("/tasks/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenDatabaseError_whenGetTaskById_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(get("/tasks/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }

  @Test
  void givenListAndTaskExist_whenGetTasksByListId_thenReturnsTasks() throws Exception {
    mockMvc
        .perform(get("/tasks/list/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].taskName", is("Buy milk")))
        .andExpect(jsonPath("$[0].taskDate", is("2021-01-01")))
        .andExpect(jsonPath("$[0].taskStatus", is("TO_DO")))
        .andExpect(jsonPath("$[0].listId", is(1)))
        .andExpect(jsonPath("$[1].taskName", is("Buy bread")))
        .andExpect(jsonPath("$[1].taskDate", is("2021-01-01")))
        .andExpect(jsonPath("$[1].taskStatus", is("TO_DO")))
        .andExpect(jsonPath("$[1].listId", is(1)));
  }

  @Test
  void givenListDoesNotExist_whenGetTasksByListId_thenReturnsNotFound() throws Exception {
    mockMvc
        .perform(get("/tasks/list/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenDatabaseError_whenGetTasksByListId_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(get("/tasks/list/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }

  @Test
  void givenTaskStatus_whenGetTaskStatusOptions_thenReturnsTaskStatusOptions() throws Exception {
    mockMvc
        .perform(get("/tasks/status-options").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", is("TO_DO")))
        .andExpect(jsonPath("$[1]", is("IN_PROGRESS")))
        .andExpect(jsonPath("$[2]", is("DONE")));
  }

  @Test
  void givenTaskCreateRequest_whenCreateTask_thenReturnsCreatedTask() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "listId": 3,
                            "taskName": "Wash car",
                            "taskDate": "2021-01-01",
                            "taskStatus": "TO_DO"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.taskId").exists())
            .andExpect(jsonPath("$.listId").value(3))
            .andExpect(jsonPath("$.taskName").value("Wash car"))
            .andExpect(jsonPath("$.taskDate").value("2021-01-01"))
            .andExpect(jsonPath("$.taskStatus").value("TO_DO"))
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    JSONObject jsonResponse = new JSONObject(responseContent);
    Long createdTaskId = jsonResponse.getLong("taskId");

    Optional<Task> createdTask = taskRepository.findById(createdTaskId);
    assertTrue(createdTask.isPresent());
    assertEquals("Wash car", createdTask.get().getTaskName());
  }

  @Test
  void givenDatabaseError_whenCreateTask_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(
            post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "listId": 3,
                            "taskName": "Wash car",
                            "taskDate": "2021-01-01",
                            "taskStatus": "TO_DO"
                        }
                        """))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }

  @Test
  void givenTaskIdExistsAndValidTaskUpdateRequest_whenUpdateTask_thenReturnsUpdatedTask()
      throws Exception {
    Task task = taskRepository.findAll().get(0);

    mockMvc
        .perform(
            put("/tasks/" + task.getTaskId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "listId": 1,
                            "taskName": "Updated task",
                            "taskDate": "2021-01-01",
                            "taskStatus": "IN_PROGRESS"
                        }
                        """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.taskId").value(task.getTaskId()))
        .andExpect(jsonPath("$.listId").value(1))
        .andExpect(jsonPath("$.taskName").value("Updated task"))
        .andExpect(jsonPath("$.taskDate").value("2021-01-01"))
        .andExpect(jsonPath("$.taskStatus").value("IN_PROGRESS"));

    Optional<Task> updatedTask = taskRepository.findById(task.getTaskId());
    assertTrue(updatedTask.isPresent());
    assertEquals("Updated task", updatedTask.get().getTaskName());
    assertEquals(TaskStatus.IN_PROGRESS, updatedTask.get().getTaskStatus());
  }

  @Test
  void givenTaskIdDoesNotExistAndValidTaskUpdateRequest_whenUpdateTask_thenReturnsResourceNotFound()
      throws Exception {
    mockMvc
        .perform(
            put("/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "listId": 1,
                            "taskName": "Updated task",
                            "taskDate": "2021-01-01",
                            "taskStatus": "IN_PROGRESS"
                        }
                        """))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenDatabaseError_whenUpdateTask_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(
            put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "listId": 1,
                            "taskName": "Updated task",
                            "taskDate": "2021-01-01",
                            "taskStatus": "IN_PROGRESS"
                        }
                        """))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }

  @Test
  void givenTaskIdExists_whenDeleteTask_thenReturnsNoContent() throws Exception {
    Task task = taskRepository.findAll().get(0);

    mockMvc
        .perform(delete("/tasks/" + task.getTaskId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    Optional<Task> deletedTask = taskRepository.findById(task.getTaskId());
    assertFalse(deletedTask.isPresent());
  }

  @Test
  void givenTaskIdDoesNotExist_whenDeleteTask_thenReturnsResourceNotFound() throws Exception {
    mockMvc
        .perform(delete("/tasks/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenDatabaseError_whenDeleteTask_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(delete("/tasks/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }

  @Test
  void givenListIdExists_whenDeleteAllTasksByListId_thenReturnsNoContent() throws Exception {
    mockMvc
        .perform(delete("/tasks/list/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    assertEquals(0, taskRepository.findTasksByListId(1l).size());
  }

  @Test
  void givenListIdDoesNotExist_whenDeleteAllTasksByListId_thenReturnsResourceNotFound()
      throws Exception {
    mockMvc
        .perform(delete("/tasks/list/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenDatabaseError_whenDeleteAllTasksByListId_thenReturnsInternalServerError()
      throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(delete("/tasks/list/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }
}
