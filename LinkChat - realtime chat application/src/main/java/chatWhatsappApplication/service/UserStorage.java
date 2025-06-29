package chatWhatsappApplication.service;

import chatWhatsappApplication.model.User;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserStorage {
    private static final String STORAGE_FILE = "users.ser";
    
    @SuppressWarnings("unchecked")
    public static Map<String, User> loadUsers() {
        File file = new File(STORAGE_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORAGE_FILE))) {
            return (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    public static void saveUsers(Map<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des utilisateurs : " + e.getMessage());
        }
    }
}
