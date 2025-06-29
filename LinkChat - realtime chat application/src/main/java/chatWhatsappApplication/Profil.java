package chatWhatsappApplication;

public class Profil {
    private String nom;
    private String prenom;
    private String email;
    private String imagePath;

    public Profil(String nom, String prenom, String email, String imagePath) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.imagePath = imagePath;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getImagePath() {
        return imagePath;
    }
}


