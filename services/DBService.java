package services;

import java.sql.*;

public class DBService {
    private Connection connection;

    public DBService() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC driver
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/your_dbname", "your_username", "your_password" // Connect to DB
            );
        } catch (Exception e) {
            System.out.println("❌ Database Connection Error: " + e.getMessage()); // Handle connection failure
        }
    }
}
