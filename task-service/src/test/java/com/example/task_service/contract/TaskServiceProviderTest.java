package com.example.task_service.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.task_service.config.TestContainerConfig;
import com.example.task_service.entity.Task;
import com.example.task_service.entity.TaskStatus;
import com.example.task_service.repository.TaskRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Execution(ExecutionMode.SAME_THREAD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Provider("TaskService") // This must match the "provider" name in the JSON
@PactFolder("${user.dir}/../contracts")
public class TaskServiceProviderTest {

  @Autowired private TaskRepository taskRepository;

  @Autowired private PostgreSQLContainer<?> postgreSQLContainer;

  @LocalServerPort int port;

  @BeforeEach
  void before(PactVerificationContext context) {
    taskRepository.deleteAll();

    context.setTarget(new HttpTestTarget("localhost", port));
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void verifyPactContract(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @State("TaskServiceTest")
  public void taskServiceTest() {}

  @State("TaskWithListIdExist")
  public void tasksExistWithListId() {
    Task task = new Task();
    task.setListId(1L);
    task.setTaskId(1L);
    task.setTaskName("Buy milk");
    task.setTaskDate(LocalDate.of(2021, 1, 1));
    task.setTaskStatus(TaskStatus.TO_DO);
    taskRepository.save(task);
  }

  @State("NoTaskWithListIdExist")
  public void noTasksExistWithListId() {}

  @State("StatusOptionsExist")
  public void statusOptionsExist() {}

  @State("ValidTaskCreateRequest")
  public void validTaskCreateRequest() {}

  @State("InvalidTaskCreateRequest")
  public void invalidTaskCreateRequest() {}

  @State("ValidDeleteTaskRequest")
  public void validDeleteTaskRequest() {
    Task task = new Task();
    task.setListId(1L);
    task.setTaskId(1L);
    task.setTaskName("Buy milk");
    task.setTaskDate(LocalDate.of(2021, 1, 1));
    task.setTaskStatus(TaskStatus.TO_DO);
    taskRepository.save(task);
  }

  @State("InvalidDeleteTaskRequest")
  public void invalidDeleteTaskRequest() {}

  @State("ValidDeleteAllTasksByListIdRequest")
  public void validDeleteAllTasksByListIdRequest() {
    Task task = new Task();
    task.setListId(1L);
    task.setTaskId(1L);
    task.setTaskName("Buy milk");
    task.setTaskDate(LocalDate.of(2021, 1, 1));
    task.setTaskStatus(TaskStatus.TO_DO);
    taskRepository.save(task);
  }

  @State("InvalidDeleteAllTasksByListIdRequest")
  public void InvalidDeleteAllTasksByListIdRequest() {}
}
