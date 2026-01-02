package com.extension.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.extension.test.accounts.Account;
import com.extension.test.accounts.AccountRepository;
import com.extension.test.exception.DailyTransferLimitExceededException;
import com.extension.test.exception.DailyWithdrawLimitExceededException;
import com.extension.test.transactions.TransactionRepository;
import com.extension.test.transactions.TransactionService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AccountIntegrationTest extends AbstractIntegrationTest {

  private final TransactionService transactionService;
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  AccountIntegrationTest(
      TransactionService transactionService,
      AccountRepository accountRepository,
      TransactionRepository transactionRepository
  ) {
    this.transactionService = transactionService;
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
  }

  @BeforeEach
  void setUp() {
    transactionRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @DisplayName("동시에 여러 번 입금 요청해도 모든 금액이 정확히 반영된다")
  @Test
  void concurrent_deposits_are_applied_correctly() throws Exception {
    // given
    String accountNumber = "12345678";
    accountRepository.save(new Account(accountNumber));

    int threads = 20;
    long amount = 1000L;

    ExecutorService pool = Executors.newFixedThreadPool(threads);
    // when
    try {
      List<Future<Void>> futures = submitConcurrently(pool, threads,
          () -> {
            transactionService.deposit(accountNumber, amount);
            return null;
          });

      for (Future<Void> f : futures) {
        f.get();
      }

      Account reloaded = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
      assertThat(reloaded.getBalance()).isEqualTo(threads * amount);
    } finally {
      pool.shutdownNow();
    }
  }

  @DisplayName("잔액이 충분할 때 동시 출금 요청이 모두 정상 처리된다")
  @Test
  void concurrent_withdraws_are_applied_correctly_when_balance_is_enough() throws Exception {
    String accountNumber = "12345678";
    accountRepository.save(new Account(accountNumber));

    int threads = 20;
    long amount = 1_000L;

    transactionService.deposit(accountNumber, threads * amount);

    ExecutorService pool = Executors.newFixedThreadPool(threads);
    try {
      //when
      List<Future<Void>> futures = submitConcurrently(pool, threads,
          () -> {
            transactionService.withdraw(accountNumber, amount);
            return null;
          });

      for (Future<Void> f : futures) {
        f.get();
      }

      // then
      Account reloaded = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
      assertThat(reloaded.getBalance()).isEqualTo(0L);
    } finally {
      pool.shutdownNow();
    }
  }

  @DisplayName("동시 출금 요청 시 일 출금 한도를 초과하지 않는다")
  @Test
  void concurrent_withdraws_should_not_exceed_daily_limit() throws Exception {
    // given
    String accountNumber = "12345678";
    accountRepository.save(new Account(accountNumber));

    int threads = 20;
    long amount = 100_000L;

    transactionService.deposit(accountNumber, 10_000_000L);

    ExecutorService pool = Executors.newFixedThreadPool(threads);
    try {
      // when
      List<Future<Void>> futures = submitConcurrently(pool, threads,
          () -> {
            transactionService.withdraw(accountNumber, amount);
            return null;
          });

      int success = 0;
      int failedByLimit = 0;
      List<Throwable> unexpected = new ArrayList<>();

      for (Future<Void> f : futures) {
        try {
          f.get();
          success++;
        } catch (ExecutionException ee) {
          Throwable cause = ee.getCause();
          if (cause instanceof DailyWithdrawLimitExceededException) {
            failedByLimit++;
          } else {
            unexpected.add(cause);
          }
        }
      }

      // then
      assertThat(unexpected).isEmpty();
      assertThat(failedByLimit).isEqualTo(10);

      long limit = TransactionService.DAILY_WITHDRAW_LIMIT;
      long maxSuccess = limit / amount;
      assertThat(success).isEqualTo((int) maxSuccess);

      Account reloaded = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
      long expectedBalance = 10_000_000L - (success * amount);
      assertThat(reloaded.getBalance()).isEqualTo(expectedBalance);
    } finally {
      pool.shutdownNow();
    }
  }

  @DisplayName("동시 이체 요청 시 수수료를 포함해 잔액과 수취 계좌가 정확히 반영된다")
  @Test
  void concurrent_transfers_are_applied_correctly_when_balance_is_enough() throws Exception {
    // given
    String from = "11111111";
    String to = "22222222";
    accountRepository.save(new Account(from));
    accountRepository.save(new Account(to));

    int threads = 20;
    long amount = 100_000L;
    long fee = amount / 100; // 1%
    long totalDebitPerTransfer = amount + fee;

    transactionService.deposit(from, threads * totalDebitPerTransfer);

    ExecutorService pool = Executors.newFixedThreadPool(threads);
    try {
      // when
      List<Future<Void>> futures = submitConcurrently(pool, threads,
          () -> {
            transactionService.transfer(from, to, amount);
            return null;
          });

      for (Future<Void> f : futures) {
        f.get();
      }

      Account fromReloaded = accountRepository.findByAccountNumber(from).orElseThrow();
      Account toReloaded = accountRepository.findByAccountNumber(to).orElseThrow();

      // then
      assertThat(fromReloaded.getBalance()).isEqualTo(0L);
      assertThat(toReloaded.getBalance()).isEqualTo(threads * amount);
    } finally {
      pool.shutdownNow();
    }
  }

  @DisplayName("동시 이체 요청 시 일 이체 한도를 초과하지 않는다")
  @Test
  void concurrent_transfers_should_not_exceed_daily_transfer_limit() throws Exception {
    // given
    String from = "11111111";
    String to = "22222222";
    accountRepository.save(new Account(from));
    accountRepository.save(new Account(to));

    int threads = 60;
    long amount = 100_000L;

    long fee = amount / 100;
    long totalDebitPerTransfer = amount + fee;

    transactionService.deposit(from, 50_000_000L);

    ExecutorService pool = Executors.newFixedThreadPool(threads);
    try {
      // when
      List<Future<Void>> futures = submitConcurrently(pool, threads,
          () -> {
            transactionService.transfer(from, to, amount);
            return null;
          });

      int success = 0;
      int failedByLimit = 0;
      List<Throwable> unexpected = new ArrayList<>();

      for (Future<Void> f : futures) {
        try {
          f.get();
          success++;
        } catch (ExecutionException ee) {
          Throwable cause = ee.getCause();
          if (cause instanceof DailyTransferLimitExceededException) {
            failedByLimit++;
          } else {
            unexpected.add(cause);
          }
        }
      }

      // then
      assertThat(unexpected).isEmpty();

      long transferLimit = 3_000_000L;
      int maxSuccess = (int) (transferLimit / amount); // 30
      assertThat(success).isEqualTo(maxSuccess);
      assertThat(failedByLimit).isEqualTo(threads - maxSuccess);

      Account fromReloaded = accountRepository.findByAccountNumber(from).orElseThrow();
      Account toReloaded = accountRepository.findByAccountNumber(to).orElseThrow();

      long expectedFromBalance = 50_000_000L - ((long) success * totalDebitPerTransfer);
      long expectedToBalance = (long) success * amount;

      assertThat(fromReloaded.getBalance()).isEqualTo(expectedFromBalance);
      assertThat(toReloaded.getBalance()).isEqualTo(expectedToBalance);
    } finally {
      pool.shutdownNow();
    }
  }

  private static List<Future<Void>> submitConcurrently(
      ExecutorService pool,
      int threads,
      Callable<Void> task
  ) throws InterruptedException {
    CountDownLatch ready = new CountDownLatch(threads);
    CountDownLatch start = new CountDownLatch(1);

    List<Future<Void>> futures = new ArrayList<>(threads);

    for (int i = 0; i < threads; i++) {
      futures.add(pool.submit(() -> {
        ready.countDown();
        start.await();
        return task.call();
      }));
    }

    ready.await();
    start.countDown();
    return futures;
  }
}
