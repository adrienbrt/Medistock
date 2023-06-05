package chartreux.applilabv2.Entity;

import javafx.util.Pair;

import java.util.Date;
import java.util.List;

public class Commande {
    private String id;
    private Laboratoire laboratoire;
    private Date date;
    private Etat etat;
    private List<Pair<Ingredient,Integer>> listDetail;

    public Commande(String id, Laboratoire laboratoire, Date date, Etat etat, List<Pair<Ingredient, Integer>> listDetail) {
        this.id = id;
        this.laboratoire = laboratoire;
        this.date = date;
        this.etat = etat;
        this.listDetail = listDetail;
    }

    public Commande(String id, Laboratoire laboratoire, Date date, Etat etat) {
        this.id = id;
        this.laboratoire = laboratoire;
        this.date = date;
        this.etat = etat;
    }

    public Commande() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Laboratoire getLaboratoire() {
        return laboratoire;
    }

    public void setLaboratoire(Laboratoire laboratoire) {
        this.laboratoire = laboratoire;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public List<Pair<Ingredient, Integer>> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<Pair<Ingredient, Integer>> listDetail) {
        this.listDetail = listDetail;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id='" + id + '\'' +
                ", laboratoire=" + laboratoire +
                ", date=" + date +
                ", etat_id='" + etat.toString() + '\'' +
                ", listDetail=" + listDetail.toString() +
                '}';
    }
}
