package com.extension.test.api.exception;

import com.extension.test.api.dto.ApiResponse;
import com.extension.test.api.dto.ApiResponse.FieldError;
import com.extension.test.exception.AccountNotFoundException;
import com.extension.test.exception.DailyWithdrawLimitExceededException;
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

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> illegalArgument(IllegalArgumentException e) {
    return ApiResponse.fail(
        ErrorCode.INVALID_REQUEST.code(),
        e.getMessage()
    );
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> illegalState(IllegalStateException e) {
    return ApiResponse.fail(
        ErrorCode.INVALID_REQUEST.code(),
        e.getMessage()
    );
  }

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

  @ExceptionHandler(AccountNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiResponse<Void> accountNotFound(AccountNotFoundException e) {
    return ApiResponse.fail(
        ErrorCode.ACCOUNT_NOT_FOUND.code(),
        ErrorCode.ACCOUNT_NOT_FOUND.defaultMessage()
    );
  }

  @ExceptionHandler(DailyWithdrawLimitExceededException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> dailyLimitExceeded(DailyWithdrawLimitExceededException e) {
    return ApiResponse.fail(
        ErrorCode.DAILY_WITHDRAW_LIMIT_EXCEEDED.code(),
        ErrorCode.DAILY_WITHDRAW_LIMIT_EXCEEDED.defaultMessage()
    );
  }
}
