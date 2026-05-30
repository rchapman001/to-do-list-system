package com.example.to_do_list_ui.dto.external.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskApiResponse {

  private Long taskId;
  private Long listId;
  private String taskName;
  private LocalDate taskDate;
  private String taskStatus;
}
