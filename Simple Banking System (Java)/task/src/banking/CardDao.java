package banking;

import org.sqlite.jdbc4.JDBC4Connection;

import java.sql.*;
import java.util.Optional;

public class CardDao {
    private final Connection conn;
    public CardDao(Connection conn) {
        this.conn = conn;

    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS card (" +
                "id INTEGER PRIMARY KEY," +
                "number TEXT," +
                "pin TEXT," +
                "balance INTEGER DEFAULT 0" +
                ");";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }

    public void insert(Card card) throws SQLException {
        String sql = "INSERT INTO card (number, pin) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, card.getNumber());
            ps.setString(2, card.getPin());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al insertar datos: " + e.getMessage());
        }
    }
    public Optional<Card> find(String num, String pin) throws SQLException {
        String sql = "SELECT id, number, pin, balance FROM card WHERE number = ? AND pin = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, num);
            ps.setString(2, pin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Card(rs.getString("number"), rs.getString("pin"), rs.getInt("id"), rs.getInt("balance")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar datos: " + e.getMessage());
        }
        return Optional.empty();
    }
    public void addIncome(String num, int amount) throws SQLException {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, num);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar registros: " + e.getMessage());
            throw e;
        }
    }
    public boolean transfer(String num, String num2, int amount) {

        if (!exists(num2)) {
            System.out.println("La cuenta de destino no existe");
            return false;
        }
        if ( num == num2){
            System.out.println("You can't transfer money to the same account!");
            return false;
        }
        String checkBalance = "SELECT balance FROM card WHERE number = ?";
        String addBalance = "UPDATE card SET balance = balance + ? WHERE number = ?";
        String subtractBalance = "UPDATE card SET balance = balance - ? WHERE number = ?";

        try {
            conn.setAutoCommit(false);

            // 1. Verificar si hay suficiente saldo
            try (PreparedStatement checkPs = conn.prepareStatement(checkBalance)) {
                checkPs.setString(1, num);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next() || rs.getInt("balance") < amount) {
                        System.out.println("Not enough money!");
                        conn.rollback();
                        return false;
                    }

                }
            }

            // 2. Restar dinero de la cuenta origen
            try (PreparedStatement subPs = conn.prepareStatement(subtractBalance)) {
                subPs.setInt(1, amount);
                subPs.setString(2, num);
                subPs.executeUpdate();
            }

            // 3. Sumar dinero a la cuenta destino
            try (PreparedStatement addPs = conn.prepareStatement(addBalance)) {
                addPs.setInt(1, amount);
                addPs.setString(2, num2);
                addPs.executeUpdate();
            }

            conn.commit();
            System.out.println("Success!");
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean exists(String number) {
        String sql = "SELECT 1 FROM card WHERE number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar existencia: " + e.getMessage());
            return false;
        }
    }
    public int getSaldo(String num) throws SQLException {
        String sql = "SELECT balance FROM card WHERE number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, num);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("balance");
                } else {
                    throw new SQLException("error inesperado");
                }
            }
        }
    }
    public void delete(String num) throws SQLException {
        String sql = "DELETE FROM card WHERE number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, num);
            ps.executeUpdate();
        }
    }

    public void close() throws SQLException {
        conn.close();
    }
}
        //System.out.println(conn.isClosed());}