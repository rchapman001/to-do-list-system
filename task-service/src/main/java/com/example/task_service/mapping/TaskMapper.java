package com.example.task_service.mapping;

import com.example.task_service.dto.internal.request.TaskCreateRequest;
import com.example.task_service.dto.internal.response.TaskResponse;
import com.example.task_service.entity.Task;
import com.example.task_service.util.CommonUtils;
import org.springframework.stereotype.Service;

@Service
public class TaskMapper {

  public TaskResponse toTaskResponse(Task task) {
    return new TaskResponse(
        task.getTaskId(),
        task.getListId(),
        task.getTaskName(),
        task.getTaskDate(),
        task.getTaskStatus().toString());
  }

  public Task toTask(TaskCreateRequest taskCreateRequest) {
    return new Task(
        taskCreateRequest.getListId(),
        taskCreateRequest.getTaskName(),
        taskCreateRequest.getTaskDate(),
        CommonUtils.mapStringToTaskStatus(taskCreateRequest.getTaskStatus()));
  }
}
