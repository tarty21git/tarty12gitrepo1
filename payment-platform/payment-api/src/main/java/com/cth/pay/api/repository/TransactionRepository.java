package com.cth.pay.api.repository;

import com.cth.pay.api.entity.TransactionEntity;
import com.cth.pay.core.enums.PaymentProvider;
import com.cth.pay.core.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByTransactionId(String transactionId);
    List<TransactionEntity> findByProvider(PaymentProvider provider);
    List<TransactionEntity> findByStatus(TransactionStatus status);
}
