import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ============================
// Define Custom Exception Classes
// ============================
class NegativeDepositException extends Exception {
    public NegativeDepositException(String message) {
        super(message);
    }
}

class OverdrawException extends Exception {
    public OverdrawException(String message) {
        super(message);
    }
}

class InvalidAccountOperationException extends Exception {
    public InvalidAccountOperationException(String message) {
        super(message);
    }
}

// ============================
// Observer Pattern - Define Observer Interface
// ============================

interface Observer {
    void update(String message);
}

// Implement TransactionLogger class (Concrete Observer)

class TransactionLogger implements Observer {

    public void update(String message) {
        System.out.println("Transaction Log:" + message);
    }
}

// ============================
// BankAccount (Subject in Observer Pattern)
// ============================
class BankAccount {
    protected String accountNumber;
    protected double balance;
    protected boolean isActive;
    private List<Observer> observers = new ArrayList<>();

    public BankAccount(String accNum, double initialBalance) {
        this.accountNumber = accNum;
        this.balance = initialBalance;
        this.isActive = true;
    }

    // Attach observer
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // Notify observers (Implement in methods)
    private void notifyObservers(String message) {
        // Notify all observers when a transaction 
        for (Observer observer: observers) {
            observer.update(message);
        }
    }

    public void deposit(double amount) throws NegativeDepositException {
        // Implement exception handling for negative deposits
        if (amount < 0) {
            throw new NegativeDepositException("Deposit must be positive");
        }
        balance += amount;
        notifyObservers("Deposited: $" + amount);
    }

    public void withdraw(double amount) throws OverdrawException, InvalidAccountOperationException {
        // Implement exception handling for overdrawing and closed accounts
        if (!isActive) throw new InvalidAccountOperationException("This Account is closed");
        if (amount > balance) {
            throw new OverdrawException("You cannot withdraw more than your balance. Your balance is $" + balance);
        }
        balance -= amount;
        notifyObservers("Withdrew: $" + amount);
    }

    public double getBalance() {
        return balance;
    }

    public void closeAccount() {
        // Prevent further transactions when the account is closed
        isActive = false;
        notifyObservers("Account has been closed");
    }
}

// ============================
// Decorator Pattern - Define SecureBankAccount Class
// ============================

abstract class BankAccountDecorator extends BankAccount {
    protected BankAccount decoratedAccount;

    public BankAccountDecorator(BankAccount account) {
        super(account.accountNumber, account.getBalance());
        this.decoratedAccount = account;
    }

    public void addObserver(Observer observer) {
        decoratedAccount.addObserver(observer);
    }

}

// Implement SecureBankAccount (Concrete Decorator)
class SecureBankAccount extends BankAccountDecorator {
    public static final double withdraw_limit = 500.00;

    public SecureBankAccount(BankAccount account) {
        super(account);
    }

    @Override
    public void deposit(double amount) throws NegativeDepositException {
        decoratedAccount.deposit(amount);
    }

    @Override 
    public void withdraw(double amount) throws OverdrawException, InvalidAccountOperationException {
        if (amount > withdraw_limit) {
            throw new InvalidAccountOperationException("You are limited to a $500 maximum withdraw amount. Please try again");
        }
        decoratedAccount.withdraw(amount);
    }

    @Override
    public double getBalance() {
        return decoratedAccount.getBalance();
    }

    @Override
    public void closeAccount() {
        decoratedAccount.closeAccount();
    }
    

}

// ============================
// Main Program
// ============================

public class BankAccountTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Ask the user to enter an initial balance and create a BankAccount object
            // Example: System.out.print("Enter initial balance: ");
            //          double initialBalance = scanner.nextDouble();
            //          BankAccount account = new BankAccount("123456", initialBalance);
            System.out.print("Enter initial balance: ");
            double initialBalance = scanner.nextDouble();
            scanner.nextLine();

            BankAccount account = new BankAccount("123456", initialBalance);
            System.out.println("Bank Account Created: #123456");

            // TODO: Create a TransactionLogger and attach it to the account
            TransactionLogger logger = new TransactionLogger();
            account.addObserver(logger);

            // Wrap account in SecureBankAccount decorator
            BankAccount secureAccount = new SecureBankAccount(account);

            // Allow the user to enter deposit and withdrawal amounts
            // Example: secureAccount.deposit(amount);
            System.out.println("Enter deposit amount: ");
            double depositAmount = scanner.nextDouble();
            secureAccount.deposit(depositAmount);

            // Example: secureAccount.withdraw(amount);
            System.out.println("Enter withdraw amount: ");
            double withdrawAmount = scanner.nextDouble();
            secureAccount.withdraw(withdrawAmount);

            // Display the final balance
            System.out.println("Final Balance: $" + secureAccount.getBalance());

            // Catch and handle exceptions properly
        } catch (NegativeDepositException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (OverdrawException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (InvalidAccountOperationException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occured: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}