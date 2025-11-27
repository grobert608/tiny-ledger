package org.robert.ledger.api.model;

import org.robert.ledger.model.MovementType;

import java.math.BigDecimal;
import java.time.Instant;

public record HistoryResponse(
    MovementType type,
    BigDecimal amount,
    Instant timestamp) {
}