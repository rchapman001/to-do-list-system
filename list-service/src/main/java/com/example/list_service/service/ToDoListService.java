package com.example.list_service.service;

import com.example.list_service.dto.internal.request.ToDoListCreateRequest;
import com.example.list_service.dto.internal.request.ToDoListUpdateRequest;
import com.example.list_service.dto.internal.response.ToDoListResponse;
import com.example.list_service.entity.ToDoList;
import com.example.list_service.exception.ResourceNotFoundException;
import com.example.list_service.mapping.ToDoListMapper;
import com.example.list_service.repository.ToDoListRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToDoListService {

  @Autowired private ToDoListRepository toDoListRepository;

  @Autowired private ToDoListMapper toDoListMapper;

  public String testEndpoint() {
    return "testEndpoint ListService";
  }

  public List<ToDoListResponse> getAllToDoLists() {
    List<ToDoList> toDoLists = toDoListRepository.findAll();
    return toDoLists.stream().map(toDoListMapper::toToDoListResponse).collect(Collectors.toList());
  }

  public ToDoListResponse getToDoListById(Long id) {
    ToDoList toDoList =
        toDoListRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ToDoList " + id + " not found"));
    return toDoListMapper.toToDoListResponse(toDoList);
  }

  public ToDoListResponse createToDoList(ToDoListCreateRequest toDoListCreateRequest) {
    ToDoList newToDoList = toDoListMapper.toToDoList(toDoListCreateRequest);
    ToDoList savedToDoList = toDoListRepository.save(newToDoList);
    System.out.println("newToDoList: " + newToDoList.getListName());
    System.out.println("savedToDoList: " + savedToDoList.getListId() + savedToDoList.getListName());
    return toDoListMapper.toToDoListResponse(savedToDoList);
  }

  public ToDoListResponse updateToDoList(Long id, ToDoListUpdateRequest toDoListUpdateRequest) {
    ToDoList existingToDoList =
        toDoListRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ToDoList " + id + " not found"));
    existingToDoList.setListName(toDoListUpdateRequest.getListName());
    ToDoList updatedToDoList = toDoListRepository.save(existingToDoList);
    return toDoListMapper.toToDoListResponse(updatedToDoList);
  }

  public void deleteToDoList(Long id) {
    if (!toDoListRepository.existsById(id)) {
      throw new ResourceNotFoundException("ToDoList " + id + " not found");
    }
    toDoListRepository.deleteById(id);
  }
}
