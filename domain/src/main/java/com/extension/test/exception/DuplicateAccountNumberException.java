package com.extension.test.exception;

import lombok.Getter;

@Getter
public class DuplicateAccountNumberException extends RuntimeException {
  private final String accountNumber;

  public DuplicateAccountNumberException(String accountNumber, Throwable cause) {
    super("중복된 계좌번호: " + accountNumber, cause);
    this.accountNumber = accountNumber;
  }
}
