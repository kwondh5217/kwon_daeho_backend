package com.extension.test.api;

public enum ErrorCode {
  DUPLICATE_ACCOUNT_NUMBER("DUPLICATE_ACCOUNT_NUMBER", "이미 존재하는 계좌번호입니다."),
  INVALID_REQUEST("INVALID_REQUEST", "요청 값이 올바르지 않습니다.");

  private final String code;
  private final String defaultMessage;

  ErrorCode(String code, String defaultMessage) {
    this.code = code;
    this.defaultMessage = defaultMessage;
  }

  public String code() {
    return code;
  }

  public String defaultMessage() {
    return defaultMessage;
  }
}

