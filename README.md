# Java Banking Management System

A console-based banking application built with Java to demonstrate object-oriented programming, account management, and banking transactions.

## Features

- Create and manage multiple bank accounts
- Deposit money into an account
- Transfer money between accounts
- Track account balances
- Record banking transactions
- Store account and transaction records locally

## Technologies

- Java
- Object-Oriented Programming (OOP)
- Git and GitHub
- PowerShell

## Project Structure

## Project Structure

BankingManagementSystem/
|-- src/
|   |-- Main.java
|   |-- Bank.java
|   |-- BankAccount.java
|   `-- Transaction.java
|-- screenshots/
|-- .gitignore
`-- README.md

## Requirements

- Java Development Kit (JDK)
- Git (optional, for version control)

## How to Run

Open PowerShell in the project directory.

**1. Compile the Java files**

```powershell
javac -d out src/BankAccount.java src/Transaction.java src/Bank.java src/Main.java
```

**2. Run the application**

```powershell
java -cp out Main
```

## Data and Security

The application uses local text files to store account and transaction records. The `data/` directory is excluded from version control to avoid publishing local test records.

Base64 encoding is not encryption. This project is for educational purposes and should not be used to manage real money or sensitive financial information.

## Future Improvements

- Automated unit testing
- Secure authentication and access controls
- Database integration
- Improved input validation and error handling
- Graphical user interface

## Author

**Susan Maina**

GitHub: https://github.com/Susan367760

---

*This is an educational portfolio project and is not production-ready banking software.*