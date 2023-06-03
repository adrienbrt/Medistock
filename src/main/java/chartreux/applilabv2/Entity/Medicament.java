package chartreux.applilabv2.Entity;

public class Medicament {
    private String id;
    private String nom;
    private String forme;
    private String description;private Ingredient ingredient;

    public Medicament(String id, String nom, String forme, String description, Ingredient ingredient) {
        this.id = id;
        this.nom = nom;
        this.forme = forme;
        this.description = description;
        this.ingredient = ingredient;
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

    public String getForme() {
        return forme;
    }

    public void setForme(String forme) {
        this.forme = forme;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}
