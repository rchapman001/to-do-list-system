package com.example.list_service.integration;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.list_service.config.TestContainerConfig;
import com.example.list_service.entity.ToDoList;
import com.example.list_service.repository.ToDoListRepository;
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
public class ToDoListIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ToDoListRepository toDoListRepository;

  @Autowired private PostgreSQLContainer<?> postgreSQLContainer;

  ToDoList list1 = new ToDoList();
  ToDoList list2 = new ToDoList();
  ToDoList list3 = new ToDoList();

  @BeforeEach
  void setUp() {
    toDoListRepository.deleteAll();

    list1.setListName("Groceries");
    toDoListRepository.save(list1);

    list2.setListName("Work Tasks");
    toDoListRepository.save(list2);

    list3.setListName("Personal Projects");
    toDoListRepository.save(list3);
  }

  @Test
  void givenToDoLists_whenGetAllToDoLists_thenReturnsToDoLists() throws Exception {
    mockMvc
        .perform(get("/lists").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[?(@.listName == 'Groceries')]").exists())
        .andExpect(jsonPath("$[?(@.listName == 'Work Tasks')]").exists())
        .andExpect(jsonPath("$[?(@.listName == 'Personal Projects')]").exists());
  }

  @Test
  void givenDatabaseError_whenGetAllToDoLists_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(get("/lists").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError()) // Expecting 500 error
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }

  @Test
  void givenToDoListId_whenGetToDoListById_thenReturnsToDoList() throws Exception {
    ToDoList list = toDoListRepository.findAll().get(0);

    mockMvc
        .perform(get("/lists/" + list.getListId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.listId").value(list.getListId()))
        .andExpect(jsonPath("$.listName").value(list.getListName()));
  }

  @Test
  void givenInvalidToDoListId_whenGetToDoListById_thenReturnsNotFound() throws Exception {
    mockMvc
        .perform(get("/lists/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenDatabaseError_whenGetToDoListById_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(get("/lists/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError()) // Expecting 500 error
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }

  @Test
  void givenValidToDoList_whenCreateToDoList_thenReturnsCreatedToDoList() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/lists")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "listName": "New List"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.listId").exists())
            .andExpect(jsonPath("$.listName").value("New List"))
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    JSONObject jsonResponse = new JSONObject(responseContent);
    Long createdListId = jsonResponse.getLong("listId");

    Optional<ToDoList> createdList = toDoListRepository.findById(createdListId);
    assertTrue(createdList.isPresent());
    assertEquals("New List", createdList.get().getListName());
  }

  @Test
  void givenDatabaseError_whenCreateToDoList_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(
            post("/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "listName": "New List"
                        }
                        """))
        .andExpect(status().isInternalServerError()) // Expecting 500 error
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }

  @Test
  void givenValidToDoList_whenUpdateToDoList_thenReturnsUpdatedToDoList() throws Exception {
    ToDoList list = toDoListRepository.findAll().get(0);

    mockMvc
        .perform(
            put("/lists/" + list.getListId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "listName": "Updated List"
                        }
                        """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.listId").value(list.getListId()))
        .andExpect(jsonPath("$.listName").value("Updated List"));

    Optional<ToDoList> updatedList = toDoListRepository.findById(list.getListId());
    assertTrue(updatedList.isPresent());
    assertEquals("Updated List", updatedList.get().getListName());
  }

  @Test
  void givenInvalidToDoListId_whenUpdateToDoList_thenReturnsNotFound() throws Exception {
    mockMvc
        .perform(
            put("/lists/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "listName": "Updated List"
                        }
                        """))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenDatabaseError_whenUpdateToDoList_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(
            put("/lists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "listName": "Updated List"
                        }
                        """))
        .andExpect(status().isInternalServerError()) // Expecting 500 error
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }

  @Test
  void givenToDoListId_whenDeleteToDoList_thenReturnsNoContent() throws Exception {
    ToDoList list = toDoListRepository.findAll().get(0);

    mockMvc
        .perform(delete("/lists/" + list.getListId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    Optional<ToDoList> deletedList = toDoListRepository.findById(list.getListId());
    assertFalse(deletedList.isPresent());
  }

  @Test
  void givenInvalidToDoListId_whenDeleteToDoList_thenReturnsNotFound() throws Exception {
    mockMvc
        .perform(delete("/lists/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenDatabaseError_whenDeleteToDoList_thenReturnsInternalServerError() throws Exception {
    postgreSQLContainer.stop();

    mockMvc
        .perform(delete("/lists/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError()) // Expecting 500 error
        .andExpect(jsonPath("$.message", startsWith("An unexpected error occurred")));

    postgreSQLContainer.start();
  }
}
