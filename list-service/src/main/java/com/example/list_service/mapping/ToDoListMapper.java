package com.example.list_service.mapping;

import com.example.list_service.dto.internal.request.ToDoListCreateRequest;
import com.example.list_service.dto.internal.response.ToDoListResponse;
import com.example.list_service.entity.ToDoList;
import org.springframework.stereotype.Service;

@Service
public class ToDoListMapper {

  public ToDoListResponse toToDoListResponse(ToDoList toDoList) {
    return new ToDoListResponse(toDoList.getListId(), toDoList.getListName());
  }

  public ToDoList toToDoList(ToDoListCreateRequest toDoListCreateRequest) {
    return new ToDoList(toDoListCreateRequest.getListName());
  }
}
