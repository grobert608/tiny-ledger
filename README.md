# tiny-ledger

A minimal Spring Boot application offering in-memory ledger APIs for recording deposits and withdrawals, viewing current balance,
and listing transaction history per account.

## Requirements
- Java 21+
- Maven 3.9+

## Running the application

```bash
mvn spring-boot:run
```

The server starts on `http://localhost:8080`.

## API Usage Examples

### Create an account

```bash
curl -X POST http://localhost:8080/accounts \
  -H 'Content-Type: application/json' \
  -d '{"initialBalance": 1000.00}'
```

### Record a transaction

Type might be DEPOSIT or WITHDRAWAL

```bash
curl -X POST http://localhost:8080/api/accounts/1/movements \
  -H 'Content-Type: application/json' \
  -d '{"type": "DEPOSIT", "amount": 100.00}'
```

### View current balance

```bash
curl http://localhost:8080/accounts/1/balance
```

### History of transactions

```bash
curl http://localhost:8080/accounts/1/history
```

## Running tests

```bash
mvn test
```