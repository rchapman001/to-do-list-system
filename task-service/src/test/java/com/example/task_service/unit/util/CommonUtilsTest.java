package com.example.task_service.unit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.task_service.entity.TaskStatus;
import com.example.task_service.util.CommonUtils;
import org.junit.Test;

public class CommonUtilsTest {

  @Test
  public void givenValidStatusString_whenMapStringToTaskStatus_thenReturnsCorrectTaskStatus() {
    // Given a valid status string "DONE"
    String statusString = "DONE";

    // When calling mapStringToTaskStatus() with the given status string
    TaskStatus result = CommonUtils.mapStringToTaskStatus(statusString);

    // Then it should return the corresponding TaskStatus enum (TaskStatus.COMPLETED)
    assertEquals(TaskStatus.DONE, result);
  }

  @Test
  public void
      givenInvalidStatusString_whenMapStringToTaskStatus_thenThrowsIllegalArgumentException() {
    // Given an invalid status string "not_a_status"
    String statusString = "not_a_status";

    // When calling mapStringToTaskStatus() with the invalid status string
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              CommonUtils.mapStringToTaskStatus(statusString);
            });

    // Then it should throw an IllegalArgumentException with a message containing "Invalid Task
    // Status"
    assertTrue(exception.getMessage().contains("Invalid Task Status"));
  }
}
