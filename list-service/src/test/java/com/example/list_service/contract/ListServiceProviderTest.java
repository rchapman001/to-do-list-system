package com.example.list_service.contract;

import au.com.dius.pact.provider.junit5.*;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.list_service.config.TestContainerConfig;
import com.example.list_service.entity.ToDoList;
import com.example.list_service.repository.ToDoListRepository;
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
@Provider("ListService") // This must match the "provider" name in the JSON
@PactFolder("${user.dir}/../contracts") // Specify your Pact contract folder location here
public class ListServiceProviderTest {

  @Autowired private ToDoListRepository toDoListRepository;

  @Autowired private PostgreSQLContainer<?> postgreSQLContainer;

  @LocalServerPort int port;

  @BeforeEach
  void before(PactVerificationContext context) {
    toDoListRepository.deleteAll();

    context.setTarget(new HttpTestTarget("localhost", port));
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void verifyPactContract(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @State("ListServiceTest")
  public void listServiceTest() {}

  @State("ToDoListsExist")
  public void toDoListsExist() {
    ToDoList list1 = new ToDoList();

    list1.setListId(1L);
    list1.setListName("Groceries");

    toDoListRepository.save(list1);
  }

  @State("NoToDoListsExist")
  public void noToDoListsExist() {}

  @State("DatabaseError")
  public void databaseError() {
    postgreSQLContainer.stop();
  }

  @State("ValidToDoListCreateRequest")
  public void validToDoListCreateRequest() {}

  @State("InvalidToDoListCreateRequest")
  public void invalidToDoListCreateRequest() {}

  @State("ToDoListsExistDeleteRequest")
  public void ToDoListsExistDeleteRequest() {
    ToDoList list1 = new ToDoList();

    list1.setListName("List 1");

    toDoListRepository.save(list1);
  }

  @State("NoToDoListsExistDeleteRequest")
  public void NoToDoListsExistDeleteRequest() {}
}
