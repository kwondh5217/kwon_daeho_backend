package com.extension.test.transactions;

import com.extension.test.accounts.Account;
import com.extension.test.accounts.AccountRepository;
import com.extension.test.exception.AccountNotFoundException;
import com.extension.test.exception.DailyWithdrawLimitExceededException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TransactionService {

    public static final long LIMIT = 1_000_000L;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Long deposit(String accountNumber, long amount) {
        Account account = accountRepository.findByAccountNumberWithLock(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        account.deposit(amount);

        Transaction tx = Transaction.depositSuccess(account.getId(), amount);
        return transactionRepository.save(tx).getId();
    }

    @Transactional
    public Long withdraw(String accountNumber, long amount) {
        Account account = accountRepository.findByAccountNumberWithLock(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();

        long todayWithdrawSum =
            transactionRepository.sumTodayWithdrawAmount(account.getId(), from, to);


        if (todayWithdrawSum + amount > LIMIT) {
            throw new DailyWithdrawLimitExceededException(
                LIMIT,
                todayWithdrawSum,
                amount
            );
        }

        account.withdraw(amount);

        Transaction success = Transaction.withdrawSuccess(account.getId(), amount);
        return transactionRepository.save(success).getId();
    }
}
