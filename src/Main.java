import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the Banking Management System.
 */
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        try {
            Bank bank = new Bank(Path.of("data"));
            runApplication(bank);
        } catch (IOException exception) {
            System.err.println("Could not load or save banking data: " + exception.getMessage());
        } finally {
            SCANNER.close();
        }
    }

    private static void runApplication(Bank bank) {
        boolean running = true;
        while (running) {
            showMenu();
            System.out.print("Choose an option: ");
            String choice = SCANNER.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> createAccount(bank);
                    case "2" -> viewAccounts(bank);
                    case "3" -> checkBalance(bank);
                    case "4" -> deposit(bank);
                    case "5" -> withdraw(bank);
                    case "6" -> transfer(bank);
                    case "7" -> transactionHistory(bank);
                    case "0" -> {
                        running = false;
                        System.out.println("Thank you for using the Banking Management System.");
                    }
                    default -> System.out.println("Invalid option. Please choose 0-7.");
                }
            } catch (IOException exception) {
                System.out.println("Storage error: " + exception.getMessage());
            } catch (IllegalArgumentException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
            System.out.println();
        }
    }

    private static void showMenu() {
        System.out.println("====================================");
        System.out.println("     BANKING MANAGEMENT SYSTEM");
        System.out.println("====================================");
        System.out.println("1. Create Account");
        System.out.println("2. View All Accounts");
        System.out.println("3. Check Balance");
        System.out.println("4. Deposit Money");
        System.out.println("5. Withdraw Money");
        System.out.println("6. Transfer Money");
        System.out.println("7. Transaction History");
        System.out.println("0. Exit");
        System.out.println("------------------------------------");
    }

    private static void createAccount(Bank bank) throws IOException {
        System.out.print("Enter account holder name: ");
        String name = SCANNER.nextLine().trim();
        BankAccount account = bank.createAccount(name);
        System.out.println("Account created successfully!");
        System.out.println("Account number: " + account.getAccountNumber());
        System.out.println("Account holder: " + account.getAccountHolder());
        System.out.println("Opening balance: KES 0.00");
    }

    private static void viewAccounts(Bank bank) {
        List<BankAccount> accounts = bank.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.println("\n--- ALL BANK ACCOUNTS ---");
        for (BankAccount account : accounts) {
            System.out.printf("%s | %s | KES %,.2f%n",
                    account.getAccountNumber(), account.getAccountHolder(),
                    account.getBalance());
        }
    }

    private static void checkBalance(Bank bank) {
        BankAccount account = findAccount(bank);
        if (account != null) {
            System.out.println("Account holder: " + account.getAccountHolder());
            System.out.println("Account number: " + account.getAccountNumber());
            System.out.printf("Current balance: KES %,.2f%n", account.getBalance());
        }
    }

    private static void deposit(Bank bank) throws IOException {
        BankAccount account = findAccount(bank);
        if (account == null) return;
        BigDecimal amount = readAmount("Enter deposit amount (KES): ");
        bank.deposit(account.getAccountNumber(), amount);
        System.out.println("Deposit successful.");
        System.out.printf("New balance: KES %,.2f%n", account.getBalance());
    }

    private static void withdraw(Bank bank) throws IOException {
        BankAccount account = findAccount(bank);
        if (account == null) return;
        BigDecimal amount = readAmount("Enter withdrawal amount (KES): ");
        bank.withdraw(account.getAccountNumber(), amount);
        System.out.println("Withdrawal successful.");
        System.out.printf("New balance: KES %,.2f%n", account.getBalance());
    }

    private static void transfer(Bank bank) throws IOException {
        System.out.print("Enter sender account number: ");
        String sender = SCANNER.nextLine().trim();
        System.out.print("Enter recipient account number: ");
        String recipient = SCANNER.nextLine().trim();
        BigDecimal amount = readAmount("Enter transfer amount (KES): ");
        bank.transfer(sender, recipient, amount);
        System.out.println("Transfer successful.");
        System.out.printf("Amount transferred: KES %,.2f%n", amount);
        System.out.printf("Sender balance: KES %,.2f%n",
                bank.findAccount(sender).getBalance());
    }

    private static void transactionHistory(Bank bank) {
        BankAccount account = findAccount(bank);
        if (account == null) return;
        List<Transaction> history = bank.getTransactions(account.getAccountNumber());
        System.out.println("\n--- TRANSACTION HISTORY ---");
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        for (Transaction transaction : history) {
            System.out.printf("%s | %-15s | KES %,.2f | %s%n",
                    transaction.getTimestamp().format(DATE_FORMAT),
                    transaction.getType(), transaction.getAmount(),
                    transaction.getDetails());
        }
    }

    private static BankAccount findAccount(Bank bank) {
        System.out.print("Enter account number: ");
        String number = SCANNER.nextLine().trim();
        BankAccount account = bank.findAccount(number);
        if (account == null) {
            System.out.println("Account not found.");
        }
        return account;
    }

    private static BigDecimal readAmount(String prompt) {
        System.out.print(prompt);
        String input = SCANNER.nextLine().trim();
        try {
            return new BigDecimal(input);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid numeric amount.");
        }
    }
}
