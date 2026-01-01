package com.extension.test.accounts;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

  Optional<Account> findByAccountNumber(String accountNumber);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from Account a where a.accountNumber = :accountNumber")
  Optional<Account> findByAccountNumberWithLock(String accountNumber);

}
