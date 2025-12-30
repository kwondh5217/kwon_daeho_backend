package com.extension.test.accounts;

import com.extension.test.exception.AccountNotFoundException;
import com.extension.test.exception.DuplicateAccountNumberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {

  private final AccountRepository accountRepository;

  @Transactional
  public Account createAccount(String accountNumber) {
    try {
      return accountRepository.save(new Account(accountNumber));
    } catch (DataIntegrityViolationException e) {
      log.warn("중복된 계좌번호: {}", accountNumber, e);
      throw new DuplicateAccountNumberException(accountNumber, e);
    }
  }

  @Transactional
  public void deleteAccount(String accountNumber) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
        .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    account.delete();
  }
}
