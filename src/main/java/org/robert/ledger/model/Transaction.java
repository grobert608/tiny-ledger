package org.robert.ledger.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Transaction(
    UUID id,
    long accountId,
    MovementType type,
    BigDecimal amount,
    Instant timestamp
) {}
