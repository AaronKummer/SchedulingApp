package Utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Instant;
import java.io.IOException;
import java.sql.SQLException;

/**
 * helper for db connection
 */
public class ConnectDB {
    public static Connection makeConnection() throws ClassNotFoundException, SQLException, Exception{
        try {
            return DriverManager.getConnection("jdbc:mysql://wgudb.ucertify.com:3306/WJ07Tko", "U07Tko", "53689124839");
        } catch (SQLException e) {
        }
        return null;
    }

    /**
     * helper to close connection
     */
    public static void closeConnection(Connection connection) throws SQLException{
        connection.close();
        System.out.println("Connection closed.");
    }
}