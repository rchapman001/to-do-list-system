package com.example.weather_service.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResponseValidator {

  @Autowired private Validator validator;

  public <T> boolean isValid(T response) {
    Set<ConstraintViolation<T>> violations = validator.validate(response);
    if (!violations.isEmpty()) {
      log.warn("Validation failed: {}", violations);
      throw new ConstraintViolationException("Invalid response: " + violations, violations);
    }
    return true;
  }
}
