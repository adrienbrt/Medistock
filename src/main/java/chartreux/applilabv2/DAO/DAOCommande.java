package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.Commande;
import chartreux.applilabv2.Entity.Ingredient;
import chartreux.applilabv2.Entity.Laboratoire;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DAOCommande {
    private Connection cnx;

    public DAOCommande(Connection cnx) {
        this.cnx = cnx;
    }

    /**
     * Récupère toutes les commandes d'un laboratoire avec le nombre d'ingrédients de chaque commande.
     * @param laboratoire L'objet Laboratoire correspondant au laboratoire dont on souhaite récupérer les commandes.
     * @return Une liste de paires (Commande, Integer) représentant les commandes avec le nombre d'ingrédients de chaque commande.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
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
                    rs.getDate("date").toLocalDate(),
                    daoEtat.find(rs.getString("etat_id"))
            );
            pairList.add(new Pair<>(commande, rs.getInt("nombre_ingredients")));
        }
        return pairList;
    };

    /**
     * Récupère tous les ingrédients d'une commande avec leur quantité respective.
     * @param commande L'objet Commande correspondant à la commande dont on souhaite récupérer les ingrédients.
     * @return Une liste de paires (Ingredient, Integer) représentant les ingrédients avec leur quantité respective.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
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

    /**
     * Met à jour une commande dans la base de données.
     * @param commande L'objet Commande correspondant à la commande à mettre à jour.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public void update(Commande commande) throws SQLException {
        String sql = "UPDATE commande SET etat_id=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, commande.getEtat().getId());
        ps.setString(2, commande.getId());

        ps.executeUpdate();
    }

    /**
     * Crée une nouvelle commande dans la base de données.
     * @param commande L'objet Commande correspondant à la nouvelle commande à créer.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public void create(Commande commande) throws SQLException{
        String id = "cmd"+(count()+1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sql = "INSERT INTO commande (id, laboratoire_id, `date`, etat_id) VALUES(?, ?, ?, ?);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,id);
        ps.setString(2,commande.getLaboratoire().getId());
        ps.setString(3, commande.getDate().format(formatter));
        ps.setString(4,commande.getEtat().getId());
        
        ps.executeUpdate();
        ps.clearParameters();
        
        sql = "INSERT INTO commandeDet (commande_id, ingredient_id, quantite) VALUES(?, ?, ?);";
        ps = cnx.prepareStatement(sql);
        for (Pair<Ingredient,Integer> pair: commande.getListDetail()) {
            ps.setString(1,id);
            ps.setString(2, pair.getKey().getId());
            ps.setInt(3, pair.getValue());

            ps.executeUpdate();
            ps.clearParameters();
        }
        
        

    }

    /**
     * Récupère toutes les commandes d'un laboratoire (à l'exception de certains états) avec le nombre d'ingrédients de chaque commande.
     * @param laboratoire L'objet Laboratoire correspondant au laboratoire dont on souhaite récupérer les commandes.
     * @return Une liste de paires (Commande, Integer) représentant les commandes avec le nombre d'ingrédients de chaque commande.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public List<Pair<Commande, Integer>>  findAllCommandeLabNbIngHome(Laboratoire laboratoire) throws SQLException {
        List<Pair<Commande, Integer>> pairList = new ArrayList<>();
        DAOLaboratoire daoLaboratoire = new DAOLaboratoire(cnx);
        DAOEtat daoEtat = new DAOEtat(cnx);
        String sql = "SELECT c.id AS commande_id, c.laboratoire_id, c.date, c.etat_id, COUNT(DISTINCT cd.ingredient_id) AS nombre_ingredients " +
                "FROM commande AS c " +
                "JOIN commandeDet AS cd ON c.id = cd.commande_id " +
                "WHERE c.laboratoire_id = ? " +
                "AND c.etat_id NOT IN ('etat4', 'etat5') " +
                "GROUP BY c.id, c.laboratoire_id, c.date, c.etat_id";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, laboratoire.getId());
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            Commande commande = new Commande(
                    rs.getString("commande_id"),
                    daoLaboratoire.findId(rs.getString("laboratoire_id")),
                    rs.getDate("date").toLocalDate(),
                    daoEtat.find(rs.getString("etat_id"))
            );
            pairList.add(new Pair<>(commande, rs.getInt("nombre_ingredients")));
        }
        return pairList;
    };


    /**
     * Compte le nombre total de commandes dans la base de données.
     * @return Le nombre total de commandes.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public int count() throws SQLException {
        int count = 0;
        String SQL = "SELECT COUNT(*) FROM commande";
        PreparedStatement ps = cnx.prepareStatement(SQL);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
        return count;
    }
}
