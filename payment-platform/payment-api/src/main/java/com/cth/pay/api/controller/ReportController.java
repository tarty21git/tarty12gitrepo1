package com.cth.pay.api.controller;

import com.cth.pay.api.entity.TransactionEntity;
import com.cth.pay.api.repository.TransactionRepository;
import com.cth.pay.core.enums.PaymentProvider;
import com.cth.pay.core.enums.TransactionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Online Reports", description = "Endpoints for online configurable reporting and analytical metrics")
public class ReportController {

    private final TransactionRepository transactionRepository;

    public ReportController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get transaction reports filtered by provider or status")
    public ResponseEntity<List<TransactionEntity>> getTransactions(
            @RequestParam(required = false) PaymentProvider provider,
            @RequestParam(required = false) TransactionStatus status
    ) {
        if (provider != null) {
            return ResponseEntity.ok(transactionRepository.findByProvider(provider));
        }
        if (status != null) {
            return ResponseEntity.ok(transactionRepository.findByStatus(status));
        }
        return ResponseEntity.ok(transactionRepository.findAll());
    }

    @GetMapping("/summary")
    @Operation(summary = "Get aggregated payment summary metrics")
    public ResponseEntity<?> getSummary() {
        List<TransactionEntity> all = transactionRepository.findAll();

        long totalTransactions = all.size();
        BigDecimal totalVolume = all.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
                .map(TransactionEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<PaymentProvider, Long> providerCounts = new EnumMap<>(PaymentProvider.class);
        for (PaymentProvider p : PaymentProvider.values()) {
            providerCounts.put(p, all.stream().filter(t -> t.getProvider() == p).count());
        }

        Map<TransactionStatus, Long> statusCounts = new EnumMap<>(TransactionStatus.class);
        for (TransactionStatus s : TransactionStatus.values()) {
            statusCounts.put(s, all.stream().filter(t -> t.getStatus() == s).count());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalTransactions", totalTransactions);
        response.put("totalVolumeUSD", totalVolume);
        response.put("providerCounts", providerCounts);
        response.put("statusCounts", statusCounts);

        return ResponseEntity.ok(response);
    }
}
