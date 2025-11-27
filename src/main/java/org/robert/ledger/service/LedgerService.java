package org.robert.ledger.service;

import org.robert.ledger.model.Account;
import org.robert.ledger.model.MovementType;
import org.robert.ledger.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LedgerService {

    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);
    private final TransactionService transactionService;

    public LedgerService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public long createAccount(BigDecimal initialBalance) {
        long id = nextId.getAndIncrement();
        accounts.put(id, new Account(id, initialBalance));
        return id;
    }

    public Account getAccount(long id) {
        Account acc = accounts.get(id);
        if (acc == null) {
            throw new IllegalArgumentException("Account not found with id = " + id);
        }
        return acc;
    }

    public BigDecimal getBalance(long id) {
        return getAccount(id).getBalance();
    }

    public Transaction recordMovement(long id, MovementType type, BigDecimal amount) {
        Account account = getAccount(id);
        return transactionService.applyMovement(account, type, amount);
    }

    public List<Transaction> getHistory(long id) {
        return getAccount(id).getState().history();
    }
}
