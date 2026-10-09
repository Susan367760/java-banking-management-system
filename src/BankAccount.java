import java.math.BigDecimal;

/**
 * Represents a customer's account in the educational banking application.
 */
public class BankAccount {
    private final String accountNumber;
    private final String accountHolder;
    private BigDecimal balance;

    public BankAccount(String accountNumber, String accountHolder, BigDecimal balance) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number is required.");
        }
        if (accountHolder == null || accountHolder.isBlank()) {
            throw new IllegalArgumentException("Account holder name is required.");
        }
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder.trim();
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount);
    }

    public boolean withdraw(BigDecimal amount) {
        validateAmount(amount);
        if (amount.compareTo(balance) > 0) {
            return false;
        }
        balance = balance.subtract(amount);
        return true;
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException(
                    "Amount must be positive and have at most two decimal places.");
        }
    }
}
