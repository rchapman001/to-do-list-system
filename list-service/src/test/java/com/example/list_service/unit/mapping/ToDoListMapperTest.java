package com.example.list_service.unit.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.list_service.dto.internal.request.ToDoListCreateRequest;
import com.example.list_service.dto.internal.response.ToDoListResponse;
import com.example.list_service.entity.ToDoList;
import com.example.list_service.mapping.ToDoListMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ToDoListMapperTest {

  private ToDoListMapper toDoListMapper;
  private ToDoList mockToDoList;
  private ToDoListCreateRequest mockToDoListCreateRequest;

  @BeforeEach
  void setUp() {
    toDoListMapper = new ToDoListMapper();

    mockToDoList = new ToDoList();
    mockToDoList.setListId(1L);
    mockToDoList.setListName("Test List");

    mockToDoListCreateRequest = new ToDoListCreateRequest();
    mockToDoListCreateRequest.setListName("Test List");
  }

  @Test
  void givenToDoList_whenToToDoListResponse_thenReturnToDoListResponse() {
    ToDoListResponse toDoListResponse = toDoListMapper.toToDoListResponse(mockToDoList);

    assertNotNull(toDoListResponse);
    assertEquals(1L, toDoListResponse.getListId());
    assertEquals("Test List", toDoListResponse.getListName());
  }

  @Test
  void givenToDoListCreateRequest_whenToToDoList_thenReturnToDoList() {
    ToDoList toDoList = toDoListMapper.toToDoList(mockToDoListCreateRequest);

    assertNotNull(toDoList);
    assertEquals("Test List", toDoList.getListName());
  }
}
