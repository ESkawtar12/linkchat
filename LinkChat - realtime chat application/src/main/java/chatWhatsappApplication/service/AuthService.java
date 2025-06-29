package chatWhatsappApplication.service;

import chatWhatsappApplication.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Base64;

public class AuthService {
    private static AuthService instance;
    private User currentUser;

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean register(User user) {
        if (isEmailUsed(user.getEmail())) {
            return false;
        }

        String sql = "INSERT INTO users (first_name, last_name, email, password, birth_date, gender) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashPassword(user.getPassword()));
            stmt.setDate(5, Date.valueOf(user.getBirthDate()));
            stmt.setString(6, user.getGender());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
public boolean login(String email, String password) {
    String sql = "SELECT * FROM users WHERE email = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String storedHash = rs.getString("password");
            if (verifyPassword(password, storedHash)) {
                int id = rs.getInt("id");
                String firstName    = rs.getString("first_name");
                String lastName     = rs.getString("last_name");
                LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
                String gender       = rs.getString("gender");
                String profileImage = rs.getString("profile_image"); // bien récupérer

                // Nouveau constructeur avec ID et profileImage
                currentUser = new User(
                    id,
                    firstName,
                    lastName,
                    email,
                    storedHash,
                    birthDate,
                    gender,
                    profileImage
                );
                return true;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}

    private boolean isEmailUsed(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    private boolean verifyPassword(String inputPassword, String storedHash) {
        String hashedInput = hashPassword(inputPassword);
        return hashedInput.equals(storedHash);
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
