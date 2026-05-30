package com.example.task_service.dto.internal.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

  private Long taskId;
  private Long listId;
  private String taskName;
  private LocalDate taskDate;
  private String taskStatus;
}
