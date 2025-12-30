package com.extension.test.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {
  private final String accountNumber;

  public AccountNotFoundException(String accountNumber) {
    super("계좌를 찾을 수 없음: " + accountNumber);
    this.accountNumber = accountNumber;
  }
}
