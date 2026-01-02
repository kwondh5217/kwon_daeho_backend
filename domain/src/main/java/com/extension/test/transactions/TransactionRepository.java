package com.extension.test.transactions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
    extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {

}
