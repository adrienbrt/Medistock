package chartreux.applilabv2.Entity;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class User {
    private String id;
    private String login;
    private String password;
    private String nom;
    private String prenom;

    private boolean isAdmin;

    private List<Pair<Laboratoire,Role>> lesLaboUtil;

    public User(String id, String login, String password, String nom, String prenom, boolean isAdmin, List<Pair<Laboratoire,Role>> lesLaboUtil) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.isAdmin = isAdmin;
        this.lesLaboUtil = lesLaboUtil;
    }
    public User(String id, String login, String password, String nom, String prenom, boolean isAdmin, String lesLaboUtil) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.isAdmin = isAdmin;
    }

    public User(String id, String login, String password, String nom, String prenom, boolean isAdmin) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.isAdmin = isAdmin;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public List<Pair<Laboratoire,Role>> getlesLaboUtil() {
        return lesLaboUtil;
    }

    public void setlesLaboUtil(List<Pair<Laboratoire,Role>> lesLaboUtil) {
        this.lesLaboUtil = lesLaboUtil;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", role=" + isAdmin +
                '}';
    }
}
