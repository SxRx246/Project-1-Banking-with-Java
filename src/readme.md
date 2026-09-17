# ACME Bank Management System

A Java-based command-line banking application developed using Object-Oriented Programming (OOP). The system supports customer and banker accounts, secure login, banking transactions, overdraft protection, transaction limits, transaction history, and fraud detection.

## Technologies Used

- Java
- IntelliJ IDEA
- Git & GitHub
- Trello
- OOP
- Abstract Classes
- Enums
- File Handling
- Exception Handling
- Lambda Expressions & Java Streams
- Optional
- Regular Expressions
- Java Cryptography (Password Hashing)

## Trello & Planning

[ACME Bank Trello Board](https://trello.com/b/OfbAEpFn/my-trello-board)

The Trello board was used to organize project tasks, requirements, and development progress.

## Development Process

The project was developed incrementally by implementing and testing the required banking features throughout development.

The main stages included:

- User registration and login
- Password security
- Bank account management
- Deposits, withdrawals, and transfers
- Overdraft protection
- Daily transaction limits
- Transaction history and filtering
- Fraud detection and account locking

Features were tested using different valid and invalid scenarios, and issues were investigated and corrected as they were identified.

## Problem-Solving Strategy

The main approach was to break requirements into smaller features and implement them within the relevant parts of the application. Different inputs and scenarios were then tested to identify errors and verify the expected behavior.

## Favorite Function

### Transaction Filtering

Transaction filtering was one of my favorite parts of the project because it involved working with files, dates, Java Streams, and Lambda Expressions.

The system can filter transactions by periods such as today, yesterday, last 7 days, last 30 days, or a specific date range.

For example:

```java
Files.lines(Path.of("transactions.txt"))
        .filter(line -> !line.isBlank())
        .filter(line -> {
            LocalDate date = LocalDate.parse(line.split(",")[0]);

            return !date.isBefore(startOfLastWeek)
                    && !date.isAfter(endOfLastWeek);
        })
        .forEach(System.out::println);