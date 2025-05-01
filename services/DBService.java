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

    public void saveQAPair(String question, String answer) {
        String query = "INSERT INTO qa_pairs (question, answer) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, question.trim());
            stmt.setString(2, answer.trim());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ DB Insert Error (qa_pairs): " + e.getMessage());
        }
    }

    public String getPreviousAnswer(String question) {
        String query = "SELECT answer FROM chat_history WHERE question = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, question.trim()); // Set question parameter
            ResultSet rs = stmt.executeQuery(); // Execute query
            if (rs.next()) {
                return rs.getString("answer"); // Return found answer
            }
        } catch (SQLException e) {
            System.out.println("❌ DB Query Error: " + e.getMessage()); // Handle query error
        }
        return null; // Return null if no answer found
    }

    public void savePDFContent(String fileName, String content) {
        String query = "INSERT INTO pdf_documents (file_name, content) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, fileName); // Store file name
            stmt.setString(2, content); // Store PDF content
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ DB PDF Save Error: " + e.getMessage());
        }
    }
}
