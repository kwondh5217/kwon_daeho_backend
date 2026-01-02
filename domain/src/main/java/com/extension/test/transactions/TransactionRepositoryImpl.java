package com.extension.test.transactions;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public long sumTodayWithdrawAmount(Long accountId, LocalDateTime from, LocalDateTime to) {
    QTransaction tx = QTransaction.transaction;

    Long sum = queryFactory
        .select(tx.amount.sum())
        .from(tx)
        .where(
            tx.transactionType.eq(TransactionType.WITHDRAW),
            tx.status.eq(TransactionStatusType.SUCCESS),
            tx.fromAccountId.eq(accountId),
            tx.occurredAt.goe(from),
            tx.occurredAt.lt(to)
        )
        .fetchOne();

    return sum == null ? 0L : sum;
  }
}
