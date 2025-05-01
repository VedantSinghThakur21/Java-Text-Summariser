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

    public void saveQuestionAnswer(String question, String answer) {
        String query = "INSERT INTO chat_history (question, answer) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE answer = VALUES(answer)"; // Updates answer if question exists
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, question.trim()); // Trim and set question
            stmt.setString(2, answer.trim()); // Trim and set answer
            stmt.executeUpdate(); // Execute SQL update
        } catch (SQLException e) {
            System.out.println("❌ DB Insert/Update Error (chat_history): " + e.getMessage()); // Handle errors
        }
    }
}
