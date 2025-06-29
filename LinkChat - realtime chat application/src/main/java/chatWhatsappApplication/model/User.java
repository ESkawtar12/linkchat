package chatWhatsappApplication.model;

import java.io.Serializable;
import java.time.LocalDate;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id; // ✅ Ajouté pour la gestion de l'identifiant unique
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String gender;
    private String profileImage;

    // ✅ Constructeur sans ID (utilisé lors de l'inscription)
    public User(String firstName, String lastName, String email, String password,
                LocalDate birthDate, String gender,String profileImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.gender = gender;
          this.profileImage = profileImage;
    }

    // ✅ Constructeur avec ID (utilisé lors de la connexion)
    public User(int id, String firstName, String lastName, String email, String password,
                LocalDate birthDate, String gender,String profileImage) {
        this(firstName, lastName, email, password, birthDate, gender,profileImage );
        this.id = id;
    }

    // ✅ Getter pour l'ID


public int getId() { return id; }
public void setId(int id) { this.id = id; }

    // Getters et Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
