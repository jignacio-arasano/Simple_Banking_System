package banking;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;

public class BankService {
    private CardDao dao;

    public BankService(CardDao dao) {
        this.dao = dao;
    }

    public Card generateCard() throws SQLException {
        String num = generateNum();
        String pin = generatePin();

        Card card =  new Card(num, pin);
        dao.insert(card);
        return card;
    }
    public Optional <Card> logIn(String number, String pin) throws SQLException {
        return dao.find(number, pin);
    }

    public String generateNum(){
        String bin = "400000";
        String customerNum = String.format("%09d", new Random().nextInt(1000000000));
        String parcial = bin+customerNum;
        int sum = 0;
        int [] parcialarr = new int[parcial.length()];
        for(int i  = 0 ; i < parcialarr.length ; i ++){
            parcialarr[i] = Character.getNumericValue(parcial.charAt(i));
        }
        for(int i  = 0 ; i < parcialarr.length ; i ++){
            if (i%2 == 0 ){
                parcialarr[i] = parcialarr [i] * 2;
            }
            if(parcialarr[i] > 9){
                parcialarr[i] -= 9;
            }
        }
        for ( int i = 0; i< parcialarr.length; i++){
            //System.out.println(parcialarr[i]);
            sum+= parcialarr[i];
        }
        String checkSum = String.valueOf(((sum%10-10) * -1));
        if (Integer.valueOf(checkSum) == 10){
            checkSum = "0" ;
        }
        //System.out.println(checkSum);
        return bin+customerNum+checkSum;
    }

    public String generatePin(){
        return String.format("%04d", new Random().nextInt(10000));
    }
    public boolean isLuhn(String num){
        int [] arr  = new int[num.length()];
        for(int i = 0 ; i< arr.length ; i++){
            arr[i] = Character.getNumericValue(num.charAt(i));
        }
        for(int i = 0 ; i< arr.length ; i++){
            if (i==0 || i%2 == 0){
                arr[i] = arr[i] * 2;
                if(arr[i]> 9){
                    arr[i] -= 9;
                }
            }
        }
        int sum = 0;
        for (int e : arr){
            sum+=e;

        }
        if (sum%10 == 0){
            return true;
        }else{
            return false;
        }
    }
    public void addSaldo(String num, int amount) throws SQLException {
        if (amount>=0) {
            dao.addIncome(num, amount);
            System.out.println("Income was added!");
        }else {
            System.out.println("ingrese un valor positivo");
        }
    }
    public void transfer (String num1, String num2, int amount){
        //System.out.println(isLuhn(num2));
        if(isLuhn(num2)) {
            dao.transfer(num1, num2, amount);
        }else {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        }
    }
    public boolean exists (String num){
        return dao.exists(num);
    }
    public void delete(String num) throws SQLException {
        dao.delete(num);
    }
    public int getSaldo(String num) throws SQLException {
        return dao.getSaldo(num);
    }
    public void closeConnection () throws SQLException {
        dao.close();
    }
}
