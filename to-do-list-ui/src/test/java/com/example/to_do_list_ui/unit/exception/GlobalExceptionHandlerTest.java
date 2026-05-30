package com.example.to_do_list_ui.unit.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.to_do_list_ui.exception.GlobalExceptionHandler;
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
  void handleGeneralException_returnsInternalServerError() {
    // Given: a general exception
    Exception ex = new Exception("java.lang.Exception");

    // When: it is passed to the exception handler
    ResponseEntity<String> response = handler.handleGeneralException(ex);

    // // Then: assert the correct HTTP status and message are returned
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo("An unexpected error occurred: java.lang.Exception");
  }
}
