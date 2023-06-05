package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.Commande;
import chartreux.applilabv2.Entity.Ingredient;
import chartreux.applilabv2.Entity.Laboratoire;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOCommande {
    private Connection cnx;

    public DAOCommande(Connection cnx) {
        this.cnx = cnx;
    }

    public List<Pair<Commande, Integer>>  findAllCommandeLabNbIng(Laboratoire laboratoire) throws SQLException {
        List<Pair<Commande, Integer>> pairList = new ArrayList<>();
        DAOLaboratoire daoLaboratoire = new DAOLaboratoire(cnx);
        DAOEtat daoEtat = new DAOEtat(cnx);
        String sql = "SELECT c.id AS commande_id, c.laboratoire_id, c.date, c.etat_id, COUNT(DISTINCT cd.ingredient_id) AS nombre_ingredients FROM commande AS c JOIN commandeDet AS cd ON c.id = cd.commande_id WHERE c.laboratoire_id = ? GROUP BY c.id, c.laboratoire_id, c.date, c.etat_id";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, laboratoire.getId());
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            Commande commande = new Commande(
                    rs.getString("commande_id"),
                    daoLaboratoire.findId(rs.getString("laboratoire_id")),
                    rs.getDate("date"),
                    daoEtat.find(rs.getString("etat_id"))
            );
            pairList.add(new Pair<>(commande, rs.getInt("nombre_ingredients")));
        }
        return pairList;
    };

    public  List<Pair<Ingredient,Integer>> findAllIngCommand(Commande commande) throws SQLException {
        List<Pair<Ingredient,Integer>> pairList = new ArrayList<>();
        DAOIngredient daoIngredient = new DAOIngredient(cnx);
        String sql = "SELECT cd.ingredient_id, cd.quantite FROM commandeDet AS cd WHERE cd.commande_id = ?;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, commande.getId());
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            Ingredient ingredient = daoIngredient.findId(rs.getString("ingredient_id"));
            pairList.add(new Pair<>(ingredient, rs.getInt("quantite")));
        }

        return pairList;
    }

    public void update(Commande commande) throws SQLException {
        String sql = "UPDATE commande SET etat_id=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, commande.getEtat().getId());
        ps.setString(2, commande.getId());

        ps.executeUpdate();
    }
}
