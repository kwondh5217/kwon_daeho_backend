package com.extension.test.accounts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "accounts")
@SQLRestriction("deleted = false")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "account_number", nullable = false)
  private String accountNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private AccountStatusType status;

  @Column(name = "balance", nullable = false)
  private long balance;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public Account(String accountNumber) {
    this.accountNumber = accountNumber;
    this.status = AccountStatusType.ACTIVE;
    this.balance = 0L;
    this.deleted = false;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
  }

  public void delete() {
    if (!this.deleted) {
      this.status = AccountStatusType.CLOSED;
      this.deleted = true;
      this.deletedAt = LocalDateTime.now();
    }
  }
}
