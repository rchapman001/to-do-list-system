package com.example.task_service.dto.internal.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {

  private Long listId;
  private String taskName;
  private LocalDate taskDate;
  private String taskStatus;
}
