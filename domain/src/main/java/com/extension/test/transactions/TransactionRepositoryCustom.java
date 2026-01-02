package com.extension.test.transactions;

import java.time.LocalDateTime;

public interface TransactionRepositoryCustom {

  long sumTodayWithdrawAmount(Long accountId, LocalDateTime from, LocalDateTime to);
}

