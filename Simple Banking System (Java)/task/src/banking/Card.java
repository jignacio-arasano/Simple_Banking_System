package banking;

import java.util.Random;

public class Card {
    private int id;
    private String number;
    private String pin;
    private int saldo;
    public Card (String number, String pin, int id, int saldo){
        this.number = number;
        this.pin = pin;
        this.saldo = this.saldo;
        this.id = this.id;
    }
    public Card(String number, String pin) {
        this.number = number;
        this.pin = pin;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }
}
