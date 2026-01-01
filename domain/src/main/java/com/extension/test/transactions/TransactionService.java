package com.extension.test.transactions;

import com.extension.test.accounts.Account;
import com.extension.test.accounts.AccountRepository;
import com.extension.test.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TransactionService {

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
}
