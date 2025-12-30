package com.extension.test.api;

import com.extension.test.api.ApiResponse.FieldError;
import com.extension.test.exception.DuplicateAccountNumberException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(DuplicateAccountNumberException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiResponse<Void> duplicateAccount(DuplicateAccountNumberException e) {

    return ApiResponse.fail(
        ErrorCode.DUPLICATE_ACCOUNT_NUMBER.code(),
        ErrorCode.DUPLICATE_ACCOUNT_NUMBER.defaultMessage(),
        List.of(new FieldError("accountNumber", e.getAccountNumber()))
    );
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    BindingResult br = ex.getBindingResult();

    List<FieldError> errors = br.getFieldErrors().stream()
        .map(e -> new ApiResponse.FieldError(e.getField(), e.getDefaultMessage()))
        .collect(Collectors.toList());

    return ApiResponse.fail(
        ErrorCode.INVALID_REQUEST.code(),
        ErrorCode.INVALID_REQUEST.defaultMessage(),
        errors
    );
  }
}
