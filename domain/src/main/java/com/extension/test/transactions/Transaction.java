package com.extension.test.transactions;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false)
  private TransactionType transactionType;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TransactionStatusType status;

  @Column(name = "from_account_id")
  private Long fromAccountId;

  @Column(name = "to_account_id")
  private Long toAccountId;

  @Column(name = "amount", nullable = false)
  private long amount;

  @Column(name = "fee", nullable = false)
  private long fee;

  @Column(name = "occurred_at", nullable = false)
  private LocalDateTime occurredAt;

  @Column(name = "failure_reason")
  private String failureReason;

  public static Transaction depositSuccess(Long accountId, long amount) {
    Transaction tx = new Transaction();
    tx.transactionType = TransactionType.DEPOSIT;
    tx.status = TransactionStatusType.SUCCESS;
    tx.toAccountId = accountId;
    tx.amount = amount;
    tx.fee = 0L;
    tx.occurredAt = LocalDateTime.now();
    return tx;
  }

  public static Transaction withdrawSuccess(Long accountId, long amount) {
    Transaction tx = new Transaction();
    tx.transactionType = TransactionType.WITHDRAW;
    tx.status = TransactionStatusType.SUCCESS;
    tx.fromAccountId = accountId;
    tx.amount = amount;
    tx.fee = 0L;
    tx.occurredAt = LocalDateTime.now();
    return tx;
  }
}
