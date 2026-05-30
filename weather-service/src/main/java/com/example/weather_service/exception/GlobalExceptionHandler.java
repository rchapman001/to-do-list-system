package com.example.weather_service.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      ConstraintViolationException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    List<Map<String, String>> errors = new ArrayList<>();

    // Iterate over each constraint violation to build a more descriptive error response
    ex.getConstraintViolations()
        .forEach(
            violation -> {
              Map<String, String> error = new HashMap<>();
              error.put("field", violation.getPropertyPath().toString()); // Field name
              error.put("message", violation.getMessage()); // Validation message
              error.put(
                  "constraint",
                  violation
                      .getConstraintDescriptor()
                      .getAnnotation()
                      .toString()); // Constraint type (e.g., @NotNull, @Size)
              errors.add(error);
            });

    errorDetails.put("errorType", "Constraint Validation Exceptions");
    errorDetails.put("errors", errors);

    return ResponseEntity.badRequest().body(errorDetails);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
    Map<String, String> response = new HashMap<>();
    response.put("message", "An unexpected error occurred: " + ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(response); // Return 500 Internal Server Error
  }
}
