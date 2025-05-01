package banking;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class Menu {

    Scanner scanner = new Scanner(System.in);
    private BankService bank;

    public Menu(BankService bank) {
        this.bank = bank;
    }

    public void showMenu() throws SQLException {
        do{
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    ManageCreation();
                    break;
                case "2":
                    ManageLogin();
                    break;

                case "0":
                    System.out.println("Bye!");
                    bank.closeConnection();
                    return;
            }
        }while(true);
    }

    public void ManageCreation() throws SQLException {
        Card card = bank.generateCard();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(card.getNumber());
        System.out.println("Your card PIN:");
        System.out.println(card.getPin());


    }
    public void ManageLogin() throws SQLException {
        System.out.println("Enter your card number:");
        String num = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();
        Optional<Card> opt = bank.logIn(num, pin);
        if(opt.isEmpty()){
            System.out.println("Wrong card number or PIN!");
            return;
        }
        System.out.println("You have successfully logged in!");
        loginMenu(opt.get());
        ;
    }
    public void loginMenu(Card card) throws SQLException {
        do{
            System.out.println("1. Balance");
            System.out.println("2. add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    showBalance(card);
                    break;
                case "2":
                    ;addIncome(card);
                    break;
                case "3":
                    transfer(card);
                    break;
                case "4":
                    delete(card);
                    showMenu();
                    break;
                case "5":
                    System.out.println("You have successfully logged out!");;
                    return;

                case "0":
                    System.out.println("Bye!");
                    bank.closeConnection();
                    System.exit(1);
                    return;
            }
        }while(true);
    }
    public void showBalance(Card card) throws SQLException {
        System.out.println("Balance: "+ bank.getSaldo(card.getNumber()));
    }
    public void addIncome(Card card) throws SQLException {
        System.out.println("Enter income:");
        int output = scanner.nextInt();
        scanner.nextLine();
        bank.addSaldo(card.getNumber(), output);
    }
    public void transfer(Card card){
        System.out.println("Enter card number:");
        String input = scanner.nextLine();
        //System.out.println(bank.exists(input));
        if(bank.isLuhn(input) && !bank.exists(input)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        }else if (bank.exists(input)){
            System.out.println("Enter how much money you want to transfer:");
            int amount = scanner.nextInt();
            scanner.nextLine();
            if(amount > 0) {
                bank.transfer(card.getNumber(), input, amount);
            }else{
                System.out.println("ingrese monto positivo");
            }
        }else {
            System.out.println("Such a card does not exist.");
        }

    }
    public void delete(Card card) throws SQLException {
        if(bank.exists(card.getNumber())) {
            bank.delete(card.getNumber());
            System.out.println("The account has been closed!");
        }
    }
}
