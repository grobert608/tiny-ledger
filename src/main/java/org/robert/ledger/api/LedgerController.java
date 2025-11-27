package org.robert.ledger.api;

import jakarta.validation.Valid;
import org.robert.ledger.api.model.AccountRequest;
import org.robert.ledger.api.model.AccountResponse;
import org.robert.ledger.api.model.HistoryResponse;
import org.robert.ledger.api.model.MovementRequest;
import org.robert.ledger.model.Transaction;
import org.robert.ledger.service.LedgerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class LedgerController {

  private final LedgerService service;

  public LedgerController(LedgerService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AccountResponse createAccount(@Valid @RequestBody AccountRequest request) {
    long id = service.createAccount(request.getInitialBalance());
    return new AccountResponse(id, service.getBalance(id));
  }

  @PostMapping("/{accountId}/movements")
  @ResponseStatus(HttpStatus.CREATED)
  public HistoryResponse recordMovement(
      @PathVariable long accountId,
      @Valid @RequestBody MovementRequest request
  ) {
    Transaction tx = service.recordMovement(accountId, request.getType(), request.getAmount());
    return new HistoryResponse(
        tx.type(),
        tx.amount(),
        tx.timestamp()
    );
  }

  @GetMapping("/{accountId}/balance")
  public AccountResponse getBalance(@PathVariable long accountId) {
    return new AccountResponse(accountId, service.getBalance(accountId));
  }

  @GetMapping("/{accountId}/history")
  public List<HistoryResponse> getHistory(@PathVariable long accountId) {
    return service.getHistory(accountId).stream()
        .map(tx -> new HistoryResponse(
            tx.type(),
            tx.amount(),
            tx.timestamp()
        ))
        .toList();
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleIllegalArgument(IllegalArgumentException ex) {
    return ex.getMessage();
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleIllegalState(IllegalStateException ex) {
    return ex.getMessage();
  }
}
