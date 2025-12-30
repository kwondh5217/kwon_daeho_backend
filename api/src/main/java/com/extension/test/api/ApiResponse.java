package com.extension.test.api;

import java.util.List;

public record ApiResponse<T>(
    boolean success,
    String code,
    String message,
    T data,
    List<FieldError> errors
) {
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, "OK", null, data, null);
  }

  public static <T> ApiResponse<T> fail(String code, String message) {
    return new ApiResponse<>(false, code, message, null, null);
  }

  public static <T> ApiResponse<T> fail(String code, String message, List<FieldError> errors) {
    return new ApiResponse<>(false, code, message, null, errors);
  }

  public record FieldError(String field, String message) {}
}
