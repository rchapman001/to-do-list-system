package com.example.task_service.util;

import com.example.task_service.entity.TaskStatus;

public class CommonUtils {

  public static TaskStatus mapStringToTaskStatus(String statusString) {
    if (statusString != null) {
      try {
        return TaskStatus.valueOf(statusString.toUpperCase());
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid Task Status: " + statusString);
      }
    }
    return null;
  }
}
