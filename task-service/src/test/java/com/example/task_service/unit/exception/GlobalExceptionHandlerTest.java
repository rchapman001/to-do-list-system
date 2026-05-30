package com.example.task_service.unit.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.task_service.exception.GlobalExceptionHandler;
import com.example.task_service.exception.ResourceNotFoundException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void handleResourceNotFoundException_returnsNotFound() {
    // Given: a ResourceNotFoundException with a custom message
    String exceptionMessage = "Task 999 not found";
    ResourceNotFoundException ex = new ResourceNotFoundException(exceptionMessage);

    // When: the exception is passed to the exception handler
    ResponseEntity<Map<String, String>> response = handler.handleResourceNotFoundException(ex);

    // Then: assert the correct HTTP status (404) and message in the response
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).containsEntry("message", exceptionMessage);
  }

  @Test
  void handleGeneralException_returnsInternalServerError() {
    // Given: a general exception
    Exception ex = new Exception("java.lang.Exception");

    // When: it is passed to the exception handler
    ResponseEntity<Map<String, String>> response = handler.handleGeneralException(ex);

    // Then: assert the correct HTTP status and message are returned
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody().get("message"))
        .isEqualTo("An unexpected error occurred: java.lang.Exception");
  }
}
