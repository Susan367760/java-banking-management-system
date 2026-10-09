import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * An immutable record of a banking operation.
 */
public class Transaction {
    private final LocalDateTime timestamp;
    private final String type;
    private final String accountNumber;
    private final BigDecimal amount;
    private final String details;

    public Transaction(LocalDateTime timestamp, String type, String accountNumber,
                       BigDecimal amount, String details) {
        this.timestamp = timestamp;
        this.type = type;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }
}
