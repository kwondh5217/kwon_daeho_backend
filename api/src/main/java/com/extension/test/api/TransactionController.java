package com.extension.test.api;

import com.extension.test.transactions.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    record DepositRequest(long amount) {}
    record DepositResponse(Long transactionId) {}

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<ApiResponse<DepositResponse>> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest req
    ) {
        Long txId = transactionService.deposit(accountNumber, req.amount());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(new DepositResponse(txId)));
    }
}