package chatWhatsappApplication.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import chatWhatsappApplication.model.User;

public class ContactService {
    private static ContactService instance;

    private ContactService() {}

    public static synchronized ContactService getInstance() {
        if (instance == null) {
            instance = new ContactService();
        }
        return instance;
    }

public boolean addFriendByEmail(int currentUserId, String friendEmail) {
    String findUserQuery = "SELECT id FROM users WHERE email = ?";
    String checkExistingQuery = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
    String insertFriendQuery = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";

    try (Connection conn = DatabaseConnection.getConnection()) {

        // 1. Vérifier si l’email existe
        try (PreparedStatement findStmt = conn.prepareStatement(findUserQuery)) {
            findStmt.setString(1, friendEmail);
            ResultSet rs = findStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Email non trouvé.");
                return false;
            }

            int friendId = rs.getInt("id");

            // ❌ Empêcher d'ajouter soi-même
            if (currentUserId == friendId) {
                System.out.println("❌ Vous ne pouvez pas vous ajouter vous-même.");
                return false;
            }

            // ❌ Vérifier si l'amitié existe déjà
            try (PreparedStatement checkStmt = conn.prepareStatement(checkExistingQuery)) {
                checkStmt.setInt(1, currentUserId);
                checkStmt.setInt(2, friendId);
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next()) {
                    System.out.println("⚠️ Cet utilisateur est déjà votre ami.");
                    return false;
                }
            }

            // 2. Ajouter l’ami (dans les deux sens)
            try (PreparedStatement insertStmt = conn.prepareStatement(insertFriendQuery)) {
                insertStmt.setInt(1, currentUserId);
                insertStmt.setInt(2, friendId);
                insertStmt.executeUpdate();
            }

            try (PreparedStatement insertStmt2 = conn.prepareStatement(insertFriendQuery)) {
                insertStmt2.setInt(1, friendId);
                insertStmt2.setInt(2, currentUserId);
                insertStmt2.executeUpdate();
            }

            System.out.println("✅ Ami ajouté avec succès !");
            return true;
        }

    } catch (SQLException e) {
        System.err.println("Erreur SQL : " + e.getMessage());
        return false;
    }
}

public List<User> getFriends(int userId) {
    List<User> friends = new ArrayList<>();

    String sql = "SELECT u.id, u.first_name, u.last_name, u.email " +
                 "FROM friends f " +
                 "JOIN users u ON u.id = f.friend_id " +
                 "WHERE f.user_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
    int id = rs.getInt("id");
    String firstName = rs.getString("first_name");
    String lastName  = rs.getString("last_name");
    String email     = rs.getString("email");
    String profileImage = rs.getString("profile_image");

    // On utilise le constructeur avec ID et profileImage
    User friend = new User(
        id,
        firstName,
        lastName,
        email,
        "",        // pas de mot de passe
        null,      // pas de date de naissance
        "",        // pas de genre
        profileImage
    );

    friends.add(friend);
}

    } catch (SQLException e) {
        System.err.println("Erreur SQL (getFriends) : " + e.getMessage());
    }

    return friends;
}


}
