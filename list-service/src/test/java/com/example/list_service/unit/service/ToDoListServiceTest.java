package com.example.list_service.unit.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.list_service.dto.internal.request.ToDoListCreateRequest;
import com.example.list_service.dto.internal.request.ToDoListUpdateRequest;
import com.example.list_service.dto.internal.response.ToDoListResponse;
import com.example.list_service.entity.ToDoList;
import com.example.list_service.exception.ResourceNotFoundException;
import com.example.list_service.mapping.ToDoListMapper;
import com.example.list_service.repository.ToDoListRepository;
import com.example.list_service.service.ToDoListService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ToDoListServiceTest {

  @Mock private ToDoListRepository toDoListRepository;

  @Mock private ToDoListMapper toDoListMapper;

  @InjectMocks private ToDoListService toDoListService;

  ToDoList mockToDoList = new ToDoList();
  List<ToDoList> mockToDoLists = new ArrayList<>();
  ToDoListResponse mockToDoListResponse = new ToDoListResponse();
  ToDoListCreateRequest mockToDoListCreateRequest = new ToDoListCreateRequest();
  ToDoListUpdateRequest mockToDoListUpdateRequest = new ToDoListUpdateRequest();

  ToDoListResponse expectedToDoListResponse = new ToDoListResponse();

  @BeforeEach
  void setUp() {
    mockToDoList.setListId(1L);
    mockToDoList.setListName("Test List");

    mockToDoLists.add(mockToDoList);

    mockToDoListResponse.setListId(1L);
    mockToDoListResponse.setListName("Test List");

    mockToDoListCreateRequest.setListName("Test List");

    mockToDoListUpdateRequest.setListName("Updated List");
  }

  @Test
  void givenTestEndpoint_whenGetTestEndpoint_thenReturnsTestEndpointResponse() {
    String response = toDoListService.testEndpoint();

    assertNotNull(response);
    assertEquals("testEndpoint ListService", response);
  }

  @Test
  void givenToDoLists_whenGetAllToDoLists_thenReturnsToDoLists() throws Exception {
    when(toDoListRepository.findAll()).thenReturn(mockToDoLists);
    when(toDoListMapper.toToDoListResponse(any(ToDoList.class))).thenReturn(mockToDoListResponse);

    List<ToDoListResponse> actualToDoListResponses = toDoListService.getAllToDoLists();

    assertEquals(1L, actualToDoListResponses.get(0).getListId());
    assertEquals("Test List", actualToDoListResponses.get(0).getListName());
  }

  @Test
  void givenToDoListIdExists_whenGetToDoListById_thenReturnsToDoList() {
    when(toDoListRepository.findById(anyLong())).thenReturn(Optional.of(mockToDoList));
    when(toDoListMapper.toToDoListResponse(any(ToDoList.class))).thenReturn(mockToDoListResponse);

    ToDoListResponse actualToDoListResponse = toDoListService.getToDoListById(1L);

    assertNotNull(actualToDoListResponse);
    assertEquals(1L, actualToDoListResponse.getListId());
    assertEquals("Test List", actualToDoListResponse.getListName());
  }

  @Test
  void givenToDoListIdDoesNotExist_whenGetToDoListById_thenReturnsResourceNotFound() {
    when(toDoListRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> toDoListService.getToDoListById(1L));
  }

  @Test
  void givenToDoListCreateRequest_whenCreateToDoList_thenReturnsCreatedToDoList() {
    when(toDoListMapper.toToDoList(any(ToDoListCreateRequest.class))).thenReturn(mockToDoList);
    when(toDoListRepository.save(any(ToDoList.class))).thenReturn(mockToDoList);
    when(toDoListMapper.toToDoListResponse(any(ToDoList.class))).thenReturn(mockToDoListResponse);

    ToDoListResponse response = toDoListService.createToDoList(mockToDoListCreateRequest);

    assertNotNull(response);
    assertEquals(1L, response.getListId());
    assertEquals("Test List", response.getListName());
  }

  @Test
  void
      givenToDoListIdExistsAndValidToDoListUpdateRequest_whenUpdateToDoList_thenReturnsUpdatedToDoList() {
    when(toDoListRepository.findById(anyLong())).thenReturn(Optional.of(mockToDoList));
    when(toDoListRepository.save(any(ToDoList.class))).thenReturn(mockToDoList);
    when(toDoListMapper.toToDoListResponse(any(ToDoList.class))).thenReturn(mockToDoListResponse);

    ToDoListResponse response = toDoListService.updateToDoList(1L, mockToDoListUpdateRequest);

    assertNotNull(response);
    assertEquals(1L, response.getListId());
    assertEquals("Test List", response.getListName());
  }

  @Test
  void
      givenToDoListIdDoesNotExistAndValidToDoListUpdateRequest_whenUpdateToDoList_thenReturnsNotFound() {
    when(toDoListRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> toDoListService.updateToDoList(1L, mockToDoListUpdateRequest));
  }

  @Test
  void givenToDoListIdExists_whenDeleteToDoList_thenReturnsNoContent() {
    when(toDoListRepository.existsById(anyLong())).thenReturn(true);

    assertDoesNotThrow(() -> toDoListService.deleteToDoList(1L));
    verify(toDoListRepository, times(1)).deleteById(1L);
  }

  @Test
  void givenInvalidId_whenDeleteToDoList_thenThrowResourceNotFoundException() {
    when(toDoListRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> toDoListService.deleteToDoList(1L));
  }
}
