package com.example.weather_service.unit.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.weather_service.util.ResponseValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ResponseValidatorTest {

  @Mock private Validator mockValidator; // Mock the Validator

  @InjectMocks
  private ResponseValidator
      responseValidator; // Automatically inject mocks into the ResponseValidator

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this); // Initialize mocks
  }

  @Test
  void testIsValid_whenNoViolations_shouldReturnTrue() {
    // Given a response with no violations
    Object validResponse = new Object();
    when(mockValidator.validate(validResponse)).thenReturn(Collections.emptySet());

    // When calling isValid()
    boolean result = responseValidator.isValid(validResponse);

    // Then it should return true
    assertTrue(result);
  }

  @Test
  void testIsValid_whenViolations_shouldThrowConstraintViolationException() {
    // Given a response with violations
    Object invalidResponse = new Object();
    Set<ConstraintViolation<Object>> violations = new HashSet<>();
    ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
    violations.add(violation);
    when(mockValidator.validate(invalidResponse)).thenReturn(violations);

    // When calling isValid(), it should throw a ConstraintViolationException
    ConstraintViolationException exception =
        assertThrows(
            ConstraintViolationException.class,
            () -> {
              responseValidator.isValid(invalidResponse);
            });

    // Then the exception message should contain "Invalid response"
    assertTrue(exception.getMessage().contains("Invalid response"));
    assertEquals(violations, exception.getConstraintViolations());
  }
}
