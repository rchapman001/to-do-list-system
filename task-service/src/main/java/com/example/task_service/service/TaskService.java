package com.example.task_service.service;

import com.example.task_service.dto.internal.request.TaskCreateRequest;
import com.example.task_service.dto.internal.request.TaskUpdateRequest;
import com.example.task_service.dto.internal.response.TaskResponse;
import com.example.task_service.entity.Task;
import com.example.task_service.entity.TaskStatus;
import com.example.task_service.exception.ResourceNotFoundException;
import com.example.task_service.mapping.TaskMapper;
import com.example.task_service.repository.TaskRepository;
import com.example.task_service.util.CommonUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  @Autowired private TaskRepository taskRepository;

  @Autowired private TaskMapper taskMapper;

  public String testEndpoint() {
    return "testEndpoint TaskService";
  }

  public List<TaskResponse> getAllTasks() {
    List<Task> tasks = taskRepository.findAll();
    return tasks.stream().map(taskMapper::toTaskResponse).collect(Collectors.toList());
  }

  public TaskResponse getTaskById(Long id) {
    Task task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task " + id + " not found"));
    return taskMapper.toTaskResponse(task);
  }

  public List<TaskResponse> getTasksByListId(Long id) {
    List<Task> tasks = taskRepository.findTasksByListId(id);
    if (tasks.isEmpty()) {
      throw new ResourceNotFoundException("No tasks found for list ID: " + id);
    }
    return tasks.stream().map(taskMapper::toTaskResponse).collect(Collectors.toList());
  }

  public List<String> getTaskStatusOptions() {
    return List.of(TaskStatus.values()).stream().map(TaskStatus::name).collect(Collectors.toList());
  }

  public TaskResponse createTask(TaskCreateRequest taskCreateRequest) {
    Task newtask = taskMapper.toTask(taskCreateRequest);
    Task savedTask = taskRepository.save(newtask);
    return taskMapper.toTaskResponse(savedTask);
  }

  public TaskResponse updateTask(Long id, TaskUpdateRequest taskUpdateRequest) {
    Task existingTask =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task " + id + " not found"));
    existingTask.setListId(taskUpdateRequest.getListId());
    existingTask.setTaskName(taskUpdateRequest.getTaskName());
    existingTask.setTaskDate(taskUpdateRequest.getTaskDate());
    existingTask.setTaskStatus(
        CommonUtils.mapStringToTaskStatus(taskUpdateRequest.getTaskStatus()));
    Task updatedTask = taskRepository.save(existingTask);
    return taskMapper.toTaskResponse(updatedTask);
  }

  public void deleteTask(Long id) {
    if (!taskRepository.existsById(id)) {
      throw new ResourceNotFoundException("Task " + id + " not found");
    }
    taskRepository.deleteById(id);
  }

  public void deleteAllTasksByListId(Long id) {
    if (taskRepository.findTasksByListId(id).isEmpty()) {
      throw new ResourceNotFoundException("No tasks found for list ID: " + id);
    }
    List<Task> tasks = taskRepository.findTasksByListId(id);
    taskRepository.deleteAll(tasks);
  }
}
