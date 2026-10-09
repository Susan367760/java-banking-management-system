import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds banking business logic and persists demo data in local text files.
 *
 * This is an educational project, not a secure or production banking system.
 */
public class Bank {
    private final Map<String, BankAccount> accounts = new LinkedHashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final Path dataDirectory;
    private long nextAccountNumber = 10000001L;

    public Bank(Path dataDirectory) throws IOException {
        this.dataDirectory = dataDirectory;
        Files.createDirectories(dataDirectory);
        loadData();
    }

    public BankAccount createAccount(String name) throws IOException {
        if (name == null || name.isBlank() || name.contains("|")) {
            throw new IllegalArgumentException("Enter a valid account holder name.");
        }

        String number = String.valueOf(nextAccountNumber++);
        BankAccount account = new BankAccount(number, name.trim(), BigDecimal.ZERO);
        accounts.put(number, account);
        transactions.add(new Transaction(LocalDateTime.now(), "ACCOUNT_CREATED",
                number, BigDecimal.ZERO, "Account opened"));
        saveData();
        return account;
    }

    public BankAccount findAccount(String number) {
        return accounts.get(number);
    }

    public List<BankAccount> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public List<Transaction> getTransactions(String accountNumber) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getAccountNumber().equals(accountNumber)) {
                result.add(transaction);
            }
        }
        return result;
    }

    public void deposit(String number, BigDecimal amount) throws IOException {
        validateAmount(amount);
        BankAccount account = requireAccount(number);
        account.deposit(amount);
        transactions.add(new Transaction(LocalDateTime.now(), "DEPOSIT",
                number, amount, "Cash deposit"));
        saveData();
    }

    public void withdraw(String number, BigDecimal amount) throws IOException {
        validateAmount(amount);
        BankAccount account = requireAccount(number);
        if (!account.withdraw(amount)) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        transactions.add(new Transaction(LocalDateTime.now(), "WITHDRAWAL",
                number, amount, "Cash withdrawal"));
        saveData();
    }

    public void transfer(String fromNumber, String toNumber, BigDecimal amount)
            throws IOException {
        validateAmount(amount);
        if (fromNumber == null || fromNumber.equals(toNumber)) {
            throw new IllegalArgumentException("Choose two different accounts.");
        }

        BankAccount sender = requireAccount(fromNumber);
        BankAccount receiver = requireAccount(toNumber);

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        // Both accounts are validated before balances are changed.
        sender.withdraw(amount);
        receiver.deposit(amount);

        transactions.add(new Transaction(LocalDateTime.now(), "TRANSFER_OUT",
                fromNumber, amount, "Transfer to " + toNumber));
        transactions.add(new Transaction(LocalDateTime.now(), "TRANSFER_IN",
                toNumber, amount, "Transfer from " + fromNumber));
        saveData();
    }

    private BankAccount requireAccount(String number) {
        BankAccount account = accounts.get(number);
        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }
        return account;
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException(
                    "Amount must be positive and have at most two decimal places.");
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private void loadData() throws IOException {
        Path accountFile = dataDirectory.resolve("accounts.txt");
        Path transactionFile = dataDirectory.resolve("transactions.txt");

        if (Files.exists(accountFile)) {
            for (String line : Files.readAllLines(accountFile, StandardCharsets.UTF_8)) {
                try {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length != 3) {
                        continue;
                    }
                    String number = parts[0];
                    String name = decode(parts[1]);
                    BigDecimal balance = new BigDecimal(parts[2]);
                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        continue;
                    }
                    accounts.put(number, new BankAccount(number, name, balance));
                    nextAccountNumber = Math.max(nextAccountNumber, Long.parseLong(number) + 1);
                } catch (RuntimeException exception) {
                    System.err.println("Skipping an invalid account record.");
                }
            }
        }

        if (Files.exists(transactionFile)) {
            for (String line : Files.readAllLines(transactionFile, StandardCharsets.UTF_8)) {
                try {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length != 5) {
                        continue;
                    }
                    transactions.add(new Transaction(
                            LocalDateTime.parse(parts[0]), parts[1], parts[2],
                            new BigDecimal(parts[3]), decode(parts[4])));
                } catch (RuntimeException exception) {
                    System.err.println("Skipping an invalid transaction record.");
                }
            }
        }
    }

    private void saveData() throws IOException {
        List<String> accountLines = new ArrayList<>();
        for (BankAccount account : accounts.values()) {
            accountLines.add(account.getAccountNumber() + "|"
                    + encode(account.getAccountHolder()) + "|"
                    + account.getBalance().toPlainString());
        }

        List<String> transactionLines = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionLines.add(transaction.getTimestamp() + "|"
                    + transaction.getType() + "|"
                    + transaction.getAccountNumber() + "|"
                    + transaction.getAmount().toPlainString() + "|"
                    + encode(transaction.getDetails()));
        }

        Files.write(dataDirectory.resolve("accounts.txt"), accountLines,
                StandardCharsets.UTF_8);
        Files.write(dataDirectory.resolve("transactions.txt"), transactionLines,
                StandardCharsets.UTF_8);
    }
}
