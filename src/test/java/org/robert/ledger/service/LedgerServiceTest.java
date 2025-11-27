package org.robert.ledger.service;

import org.junit.jupiter.api.Test;
import org.robert.ledger.model.MovementType;
import org.robert.ledger.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LedgerServiceTest {
    TransactionService transactionService = new TransactionService();
    LedgerService ledger = new LedgerService(transactionService);
    BigDecimal balance = new BigDecimal("100.00");

    @Test
    void testCreateAccountAndInitialBalance() {
        long id = ledger.createAccount(balance);

        assertEquals(balance, ledger.getBalance(id));
    }

    @Test
    void testDeposit() {
        long id = ledger.createAccount(balance);

        BigDecimal deposit = new BigDecimal("100.00");

        ledger.recordMovement(id, MovementType.DEPOSIT, deposit);

        BigDecimal expectedBalance = balance.add(deposit);

        assertEquals(expectedBalance, ledger.getBalance(id));
    }

    @Test
    void testWithdraw() {
        long id = ledger.createAccount(balance);

        BigDecimal withdraw = new BigDecimal("30.00");

        ledger.recordMovement(id, MovementType.WITHDRAWAL, withdraw);

        BigDecimal expectedBalance = balance.subtract(withdraw);

        assertEquals(expectedBalance, ledger.getBalance(id));
    }

    @Test
    void testWithdrawInsufficientFunds() {
        long id = ledger.createAccount(balance);

        BigDecimal withdraw = balance.add(new BigDecimal("300.00"));

        assertThrows(IllegalStateException.class,
                () -> ledger.recordMovement(id, MovementType.WITHDRAWAL, withdraw)
        );

        assertEquals(balance, ledger.getBalance(id));
    }

    @Test
    void testTransactionHistory() {
        long id = ledger.createAccount(balance);

        BigDecimal deposit = new BigDecimal("100.00");
        BigDecimal withdraw = new BigDecimal("30.00");

        ledger.recordMovement(id, MovementType.DEPOSIT, deposit);
        ledger.recordMovement(id, MovementType.DEPOSIT, deposit);
        ledger.recordMovement(id, MovementType.WITHDRAWAL, withdraw);

        List<Transaction> history = ledger.getHistory(id);

        BigDecimal expectedBalance = balance.add(deposit).add(deposit).subtract(withdraw);

        assertEquals(3, history.size());
        assertEquals(expectedBalance, ledger.getBalance(id));
        assertEquals(MovementType.DEPOSIT, history.get(0).type());
        assertEquals(MovementType.DEPOSIT, history.get(1).type());
        assertEquals(MovementType.WITHDRAWAL, history.get(2).type());
    }

    @Test
    void testConcurrentDepositsCAS() throws InterruptedException {
        long id = ledger.createAccount(balance);
        BigDecimal deposit = new BigDecimal("100.00");

        int threads = 10;
        int operationsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                for (int i = 0; i < operationsPerThread; i++) {
                    ledger.recordMovement(id, MovementType.DEPOSIT, deposit);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        BigDecimal expectedBalance = balance.add(deposit.multiply(BigDecimal.valueOf(threads * operationsPerThread)));

        assertEquals(expectedBalance, ledger.getBalance(id));
    }
}
