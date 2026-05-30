package com.example.list_service.controller;

import com.example.list_service.dto.internal.request.ToDoListCreateRequest;
import com.example.list_service.dto.internal.request.ToDoListUpdateRequest;
import com.example.list_service.dto.internal.response.ToDoListResponse;
import com.example.list_service.service.ToDoListService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lists")
public class ToDoListController {

  @Autowired private ToDoListService toDoListService;

  @GetMapping("/test")
  public String testEndpoint() {
    return toDoListService.testEndpoint();
  }

  @GetMapping
  public ResponseEntity<List<ToDoListResponse>> getAllToDoLists() {
    List<ToDoListResponse> toDoListResponseList = toDoListService.getAllToDoLists();
    return ResponseEntity.ok(toDoListResponseList);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ToDoListResponse> getToDoListById(@PathVariable Long id) {
    ToDoListResponse toDoListResponse = toDoListService.getToDoListById(id);
    return ResponseEntity.ok(toDoListResponse);
  }

  @PostMapping
  public ResponseEntity<ToDoListResponse> createToDoList(
      @RequestBody ToDoListCreateRequest toDoListCreateRequest) {
    ToDoListResponse toDoListResponse = toDoListService.createToDoList(toDoListCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(toDoListResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ToDoListResponse> updateToDoList(
      @PathVariable Long id, @RequestBody ToDoListUpdateRequest toDoListUpdateRequest) {
    ToDoListResponse toDoListResponse = toDoListService.updateToDoList(id, toDoListUpdateRequest);
    return ResponseEntity.ok(toDoListResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteToDoList(@PathVariable Long id) {
    toDoListService.deleteToDoList(id);
    return ResponseEntity.noContent().build();
  }
}
