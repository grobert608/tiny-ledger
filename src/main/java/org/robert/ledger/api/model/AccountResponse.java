package org.robert.ledger.api.model;

import java.math.BigDecimal;

public record AccountResponse(long accountId, BigDecimal balance) {}