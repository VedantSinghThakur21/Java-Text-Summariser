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

    public String getLastPDFContent() {
        String query = "SELECT content FROM pdf_documents ORDER BY id DESC LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("content"); // Return latest PDF content
            }
        } catch (SQLException e) {
            System.out.println("❌ DB PDF Load Error: " + e.getMessage());
        }
        return ""; // Return empty string if no PDF found
    }

    public void viewHistory() {
        String query = "SELECT * FROM chat_history ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n📜 Chat History:");
            int count = 1;
            while (rs.next()) { // Iterate through chat history
                System.out.println("🔸 Q" + count + ": " + rs.getString("question"));
                System.out.println("   🟢 A" + count + ": " + rs.getString("answer"));
                System.out.println("--------------------------------------------------");
                count++;
            }
            if (count == 1) {
                System.out.println("⚠️ No chat history found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error loading chat history: " + e.getMessage());
        }
    }

    public void deleteHistory() {
        String query = "DELETE FROM chat_history";
        try (Statement stmt = connection.createStatement()) {
            int rows = stmt.executeUpdate(query); // Execute delete operation
            System.out.println("🗑️ Deleted " + rows + " record(s) from chat history.");
        } catch (SQLException e) {
            System.out.println("❌ Error deleting chat history: " + e.getMessage());
        }
    }
}
