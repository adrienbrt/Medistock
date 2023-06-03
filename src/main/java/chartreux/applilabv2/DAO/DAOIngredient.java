package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.Ingredient;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;
import javafx.beans.binding.When;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DAOIngredient {
    private Connection cnx;
    public DAOIngredient(Connection cnx) {
        this.cnx = cnx;
    }

    public Ingredient findId(String id) throws SQLException {
        Ingredient ingredient = null;
        String sql = "SELECT * FROM ingredients WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, id);
        try (ResultSet rs = ps.executeQuery()){
            if(rs.next()){
                ingredient = new Ingredient(
                        rs.getString("id"),
                        rs.getString("nom")
                );
            }
        }

        return ingredient;
    }

    public List<Pair<Ingredient, Integer>> findIngredientLab(Laboratoire laboratoire) throws SQLException {
        List<Pair<Ingredient, Integer>> listPair = new ArrayList<>();
        String sql = "SELECT i.id,i.nom, si.quantite FROM ingredients i JOIN stock_ingredients si ON i.id = si.ingredient_id WHERE si.laboratoire_id = ?;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,laboratoire.getId());
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            Ingredient ingredient = new Ingredient(
                    rs.getString("id"),
                    rs.getString("nom")
            );
            Integer qtt = rs.getInt("quantite");
            Pair<Ingredient, Integer> pair = new Pair<>(ingredient,qtt);

            listPair.add(pair);
        }
        return listPair;
    }

    public List<Ingredient> missingIngredientLab(Laboratoire laboratoire) throws SQLException{
        List<Ingredient> ingredientList =new ArrayList<>();
        String sql = "SELECT i.id, i.nom FROM ingredients i LEFT JOIN stock_ingredients s ON i.id = s.ingredient_id AND s.laboratoire_id = ? WHERE s.ingredient_id IS NULL;";
        PreparedStatement ps =cnx.prepareStatement(sql);
        ps.setString(1, laboratoire.getId());
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            Ingredient ingredient = new Ingredient(
                    rs.getString("id"),
                    rs.getString("nom")
            );
            ingredientList.add(ingredient);
        }

        return  ingredientList;
    }

    public void createIngredient(String nom) throws SQLException {
        String id = "ing" + (count()+1);
        String sql = "INSERT INTO ingredients (id, nom) VALUES(?, ?);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,id);
        ps.setString(2,nom);

        ps.executeUpdate();
    }

    public int count() throws SQLException {
        int count = 0;
        String SQL = "SELECT COUNT(*) FROM ingredients";
        PreparedStatement ps = cnx.prepareStatement(SQL);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
        return count;
    }

    public void deleteIngredientFromLab(Ingredient leIngredient, Laboratoire leLabo) throws SQLException {
        String idIng = leIngredient.getId();
        String idLabo = leLabo.getId();

        String sql = "DELETE FROM stock_ingredients WHERE ingredient_id=? AND laboratoire_id=?;";

        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,idIng);
        ps.setString(2,idLabo);

        ps.executeUpdate();
    }

    public void addIngredientFromLab(Ingredient leIngredient, Laboratoire leLabo, Integer qtt) throws SQLException {
        String sql = "INSERT INTO pharmav2.stock_ingredients (ingredient_id, laboratoire_id, quantite) VALUES(?, ?, ?);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,leIngredient.getId());
        ps.setString(2,leLabo.getId());
        ps.setInt(3, qtt);

        ps.executeUpdate();

    }

    public void updateIngredientLab(Ingredient leIngredient, Laboratoire leLabo, Integer qtt) throws SQLException {
        String sql = "UPDATE pharmav2.stock_ingredients SET quantite=? WHERE ingredient_id=? AND laboratoire_id=?;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, qtt);
        ps.setString(2, leIngredient.getId());
        ps.setString(3, leLabo.getId());

        ps.executeUpdate();
    }

}
