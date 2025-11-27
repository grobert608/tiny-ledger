package org.robert.ledger.api.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.robert.ledger.model.MovementType;

import java.math.BigDecimal;

public class MovementRequest {

  @NotNull
  private MovementType type;

  @NotNull
  @DecimalMin("0.01")
  private BigDecimal amount;

  public MovementType getType() {
    return type;
  }

  public void setType(MovementType type) {
    this.type = type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}