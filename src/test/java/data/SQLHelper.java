package data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {

    }

    private static Connection getConn() throws SQLException {
        return DriverManager.getConnection(
            System.getProperty("db.url"),
            "app",
            "pass");
    }

    @SneakyThrows
    public static String getCode() {
        var codeSQL = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1;";
        var connection = getConn();
        return QUERY_RUNNER.query(connection, codeSQL, new ScalarHandler<>());
    }

    @SneakyThrows
    public static String getUserStatus(String userId) {
        var usersSQL = "SELECT status FROM users WHERE id = ?;";
        var connection = getConn();
        var usersStmt = connection.prepareStatement(usersSQL);
        usersStmt.setString(1, userId);
        var userData = usersStmt.executeQuery();
        userData.next();
        return userData.getString("status");
    }

    @SneakyThrows
    public static int getCardBalance(String cardNumber) {
        var usersSQL = "SELECT balance_in_kopecks FROM cards WHERE number = ?;";
        var connection = getConn();
        var usersStmt = connection.prepareStatement(usersSQL);
        usersStmt.setString(1, cardNumber);
        var userData = usersStmt.executeQuery();
        userData.next();
        return userData.getInt("balance_in_kopecks") / 100;
    }

    @SneakyThrows
    public static void deleteAll() {
        var connection = getConn();
        QUERY_RUNNER.execute(connection, "DELETE FROM cards;");
        QUERY_RUNNER.execute(connection, "DELETE FROM card_transactions;");
        QUERY_RUNNER.execute(connection, "DELETE FROM auth_codes;");
        QUERY_RUNNER.execute(connection, "DELETE FROM users;");
    }
}
