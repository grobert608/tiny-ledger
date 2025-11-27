package org.robert.ledger.service;

import org.robert.ledger.model.Account;
import org.robert.ledger.model.BalanceState;
import org.robert.ledger.model.MovementType;
import org.robert.ledger.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class TransactionService {

    public Transaction applyMovement(Account account, MovementType type, BigDecimal amount) {

        while (true) {
            BalanceState oldState = account.getState();
            long id = account.getId();

            if (type == MovementType.WITHDRAWAL &&
                    oldState.balance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient funds for account: " + id);
            }

            BigDecimal newBalance = (type == MovementType.DEPOSIT)
                    ? oldState.balance().add(amount)
                    : oldState.balance().subtract(amount);

            Transaction tx = new Transaction(
                    java.util.UUID.randomUUID(),
                    id,
                    type,
                    amount,
                    java.time.Instant.now()
            );

            var newHistory = new ArrayList<>(oldState.history());
            newHistory.add(tx);

            BalanceState newState = new BalanceState(
                    newBalance,
                    Collections.unmodifiableList(newHistory)
            );

            if (account.getARState().compareAndSet(oldState, newState)) {
                return tx;
            }
        }
    }
}
