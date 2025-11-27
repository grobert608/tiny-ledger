package org.robert.ledger.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Account {
    private final long id;
    private final AtomicReference<BalanceState> state;

    public Account(long id, BigDecimal initialBalance) {
        this.id = id;
        this.state = new AtomicReference<>(
                new BalanceState(initialBalance, List.of())
        );
    }

    public long getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return state.get().balance();
    }

    public BalanceState getState() {
        return state.get();
    }

    public AtomicReference<BalanceState> getARState() {
        return state;
    }

}
