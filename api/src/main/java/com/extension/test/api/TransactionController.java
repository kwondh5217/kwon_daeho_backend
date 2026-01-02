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
    record WithdrawRequest(long amount) {}
    record WithdrawResponse(Long transactionId) {}

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

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<ApiResponse<WithdrawResponse>> withdraw(
        @PathVariable String accountNumber,
        @Valid @RequestBody WithdrawRequest req
    ) {
        Long txId = transactionService.withdraw(accountNumber, req.amount());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(new WithdrawResponse(txId)));
    }
}