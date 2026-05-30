package com.example.task_service.controller;

import com.example.task_service.dto.internal.request.TaskCreateRequest;
import com.example.task_service.dto.internal.request.TaskUpdateRequest;
import com.example.task_service.dto.internal.response.TaskResponse;
import com.example.task_service.service.TaskService;
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
@RequestMapping("/tasks")
public class TaskController {

  @Autowired private TaskService taskService;

  @GetMapping("/test")
  public String testEndpoint() {
    return taskService.testEndpoint();
  }

  @GetMapping
  public ResponseEntity<List<TaskResponse>> getAllTasks() {
    List<TaskResponse> taskResponseList = taskService.getAllTasks();
    return ResponseEntity.ok(taskResponseList);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
    TaskResponse taskResponse = taskService.getTaskById(id);
    return ResponseEntity.ok(taskResponse);
  }

  @GetMapping("/list/{id}")
  public ResponseEntity<List<TaskResponse>> getTasksByListId(@PathVariable Long id) {
    List<TaskResponse> taskResponseList = taskService.getTasksByListId(id);
    return ResponseEntity.ok(taskResponseList);
  }

  @GetMapping("/status-options")
  public ResponseEntity<List<String>> getTaskStatusOptions() {
    List<String> statusOptions = taskService.getTaskStatusOptions();
    return ResponseEntity.ok(statusOptions);
  }

  @PostMapping
  public ResponseEntity<TaskResponse> createTask(@RequestBody TaskCreateRequest taskCreateRequest) {
    TaskResponse taskResponse = taskService.createTask(taskCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TaskResponse> updateTask(
      @PathVariable Long id, @RequestBody TaskUpdateRequest taskUpdateRequest) {
    TaskResponse taskResponse = taskService.updateTask(id, taskUpdateRequest);
    return ResponseEntity.ok(taskResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/list/{id}")
  public ResponseEntity<Void> deleteAllTasksByListId(@PathVariable Long id) {
    taskService.deleteAllTasksByListId(id);
    return ResponseEntity.noContent().build();
  }
}
