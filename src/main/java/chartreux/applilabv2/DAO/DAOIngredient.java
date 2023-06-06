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

    /**
     * Recherche un ingrédient par son identifiant.
     * @param id L'identifiant de l'ingrédient à rechercher.
     * @return L'objet Ingredient correspondant à l'ingrédient trouvé, ou null si aucun ingrédient correspondant n'est trouvé.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
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

    /**
     * Récupère tous les ingrédients présents dans la base de données.
     * @return Une liste d'objets Ingredient représentant tous les ingrédients présents dans la base de données.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public List<Ingredient> findAll() throws SQLException {
        List<Ingredient> ingredientList =new ArrayList<>();
        String sql = "SELECT * FROM ingredients";
        PreparedStatement ps =cnx.prepareStatement(sql);
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

    /**
     * Récupère tous les ingrédients et leurs quantités associées pour un laboratoire donné.
     * @param laboratoire Le laboratoire pour lequel récupérer les ingrédients.
     * @return Une liste de paires (Pair) composées d'un objet Ingredient et d'un entier représentant la quantité associée.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
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

    /**
     * Récupère tous les ingrédients manquants dans un laboratoire donné.
     * @param laboratoire Le laboratoire pour lequel récupérer les ingrédients manquants.
     * @return Une liste d'objets Ingredient représentant les ingrédients manquants dans le laboratoire.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
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

    /**
     * Crée un nouvel ingrédient dans la base de données avec le nom spécifié.
     * @param nom Le nom de l'ingrédient à créer.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public void createIngredient(String nom) throws SQLException {
        String id = "ing" + (count()+1);
        String sql = "INSERT INTO ingredients (id, nom) VALUES(?, ?);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,id);
        ps.setString(2,nom);

        ps.executeUpdate();
    }

    /**
     * Compte le nombre total d'ingrédients présents dans la base de données.
     * @return Le nombre total d'ingrédients.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
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

    /**
     * Supprime un ingrédient associé à un laboratoire spécifié.
     * @param leIngredient L'ingrédient à supprimer.
     * @param leLabo Le laboratoire associé à l'ingrédient.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public void deleteIngredientFromLab(Ingredient leIngredient, Laboratoire leLabo) throws SQLException {
        String idIng = leIngredient.getId();
        String idLabo = leLabo.getId();

        String sql = "DELETE FROM stock_ingredients WHERE ingredient_id=? AND laboratoire_id=?;";

        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,idIng);
        ps.setString(2,idLabo);

        ps.executeUpdate();
    }

    /**
     * Ajoute un ingrédient à un laboratoire avec une quantité spécifiée.
     * @param leIngredient L'ingrédient à ajouter.
     * @param leLabo Le laboratoire auquel ajouter l'ingrédient.
     * @param qtt La quantité de l'ingrédient à ajouter.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public void addIngredientFromLab(Ingredient leIngredient, Laboratoire leLabo, Integer qtt) throws SQLException {
        String sql = "INSERT INTO stock_ingredients (ingredient_id, laboratoire_id, quantite) VALUES(?, ?, ?);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,leIngredient.getId());
        ps.setString(2,leLabo.getId());
        ps.setInt(3, qtt);

        ps.executeUpdate();

    }

    /**
     * Met à jour la quantité d'un ingrédient dans un laboratoire spécifié.
     * @param leIngredient L'ingrédient à mettre à jour.
     * @param leLabo Le laboratoire associé à l'ingrédient.
     * @param qtt La nouvelle quantité de l'ingrédient.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public void updateIngredientLab(Ingredient leIngredient, Laboratoire leLabo, Integer qtt) throws SQLException {
        String sql = "UPDATE stock_ingredients SET quantite=? WHERE ingredient_id=? AND laboratoire_id=?;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, qtt);
        ps.setString(2, leIngredient.getId());
        ps.setString(3, leLabo.getId());

        ps.executeUpdate();
    }

    /**
     * Récupère la quantité d'un ingrédient dans un laboratoire spécifié.
     * @param ingredient L'ingrédient dont récupérer la quantité.
     * @param laboratoire Le laboratoire associé à l'ingrédient.
     * @return La quantité de l'ingrédient dans le laboratoire, ou 0 si l'ingrédient n'est pas présent dans le laboratoire.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public Integer getQttOneIngredientLab(Ingredient ingredient,Laboratoire laboratoire) throws SQLException {
        int nb = 0;
        String sql = "SELECT quantite FROM stock_ingredients WHERE ingredient_id = ? AND laboratoire_id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,ingredient.getId());
        ps.setString(2,laboratoire.getId());
        try (ResultSet rs = ps.executeQuery()){
            if(rs.next()){
                nb = rs.getInt("quantite");
            }
        }
        return nb;
    }

    /**
     * Met à jour la quantité d'un ingrédient dans un laboratoire en soustrayant une valeur spécifiée.
     * @param newQtt La valeur à soustraire à la quantité actuelle de l'ingrédient.
     * @param ingredient L'ingrédient à mettre à jour.
     * @param laboratoire Le laboratoire associé à l'ingrédient.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public void updateIngredientValue(Integer newQtt,Ingredient ingredient,Laboratoire laboratoire) throws SQLException {
        Integer oldQtt = getQttOneIngredientLab(ingredient,laboratoire);
        Integer newQttCalc = oldQtt-newQtt;

        updateIngredientLab(ingredient,laboratoire,newQttCalc);
    }

    /**
     * Récupère l'ingrédient ayant la plus petite quantité dans un laboratoire spécifié.
     * @param laboratoire Le laboratoire pour lequel récupérer l'ingrédient ayant la plus petite quantité.
     * @return Une liste de paires (Pair) composées d'un objet Ingredient et d'un entier représentant la quantité associée.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public List<Pair<Ingredient, Integer>> findIngredientLabHome(Laboratoire laboratoire) throws SQLException {
        List<Pair<Ingredient, Integer>> listPair = new ArrayList<>();
        String sql = "SELECT i.id, i.nom, si.quantite FROM ingredients i JOIN stock_ingredients si ON i.id = si.ingredient_id WHERE si.laboratoire_id = ? ORDER BY si.quantite ASC LIMIT 1;";
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


}
