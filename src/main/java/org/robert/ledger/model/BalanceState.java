package org.robert.ledger.model;

import java.math.BigDecimal;
import java.util.List;

public record BalanceState(
    BigDecimal balance,
    List<Transaction> history
) {
}
