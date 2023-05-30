package chartreux.applilabv2.Entity;

public class Role {
    private String id;
    private String libelle;

    public Role(String id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
