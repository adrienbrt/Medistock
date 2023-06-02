package chartreux.applilabv2.Entity;

public class Laboratoire {
    private String id;
    private String nom;
    private String adresse;
    private String Ville;
    private String CP;

    public Laboratoire(String id, String nom, String adresse, String ville, String CP) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.Ville = ville;
        this.CP = CP;
    }

    public Laboratoire(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }


    public Laboratoire() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return Ville;
    }

    public void setVille(String ville) {
        Ville = ville;
    }

    public String getCP() {
        return CP;
    }

    public void setCP(String CP) {
        this.CP = CP;
    }
}
