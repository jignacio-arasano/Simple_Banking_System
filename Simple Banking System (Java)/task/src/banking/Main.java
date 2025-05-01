package banking;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        String dbFileName = null;

        for  ( int i = 0; i< args.length-1 ; i++){
            if(args[0].equals("-fileName")){
                dbFileName = args[1];
            }
        }
        if(dbFileName == null){
            System.out.println("error argumento nulo en linea de comandos");
            System.exit(1);
        }

        String url = "jdbc:sqlite:" + dbFileName;

        try { Connection conn = DriverManager.getConnection(url);
            CardDao cardDao = new CardDao(conn);
            cardDao.createTable();

            BankService bankService = new BankService(cardDao);

            Menu menu = new Menu(bankService);
            menu.showMenu();

        }catch (Exception e){
            System.out.println("error en la conexion");
        }
    }
}