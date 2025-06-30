// filepath: src/main/java/chatWhatsappApplication/service/MessageService.java
package chatWhatsappApplication.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageService {
    public static void saveMessage(String sender, String receiver, String content) {
        String sql = "INSERT INTO messages (sender_email, recipient_email, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sender);
            stmt.setString(2, receiver);
            stmt.setString(3, content);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Message> getMessages(String user1, String user2) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT id, sender_email, recipient_email, content, deleted, edited FROM messages " +
                     "WHERE (sender_email = ? AND recipient_email = ?) " +
                     "   OR (sender_email = ? AND recipient_email = ?) " +
                     "ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(
                    rs.getInt("id"),
                    rs.getString("sender_email"),
                    rs.getString("recipient_email"),
                    rs.getString("content"),
                    rs.getBoolean("deleted"),
                    rs.getBoolean("edited")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static void deleteMessage(int messageId) {
        String sql = "UPDATE messages SET deleted = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add this inner class to represent a message
    public static class Message {
        public int id;
        public String sender;
        public String receiver;
        public String content;
        public boolean deleted;
        public boolean edited; // NEW

        public Message(int id, String sender, String receiver, String content, boolean deleted, boolean edited) {
            this.id = id;
            this.sender = sender;
            this.receiver = receiver;
            this.content = content;
            this.deleted = deleted;
            this.edited = edited;
        }

        public Message(int id, String sender, String receiver, String content, boolean deleted) {
            this(id, sender, receiver, content, deleted, false);
        }

        public Message(String sender, String receiver, String content) {
            this(-1, sender, receiver, content, false, false);
        }
    }

    // Add this method:
    public static void editMessage(int messageId, String newContent) {
        String sql = "UPDATE messages SET content = ?, edited = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newContent);
            stmt.setInt(2, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}