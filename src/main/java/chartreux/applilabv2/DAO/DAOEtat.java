package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.Etat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOEtat {
    private Connection cnx;

    public DAOEtat(Connection cnx) {
        this.cnx = cnx;
    }

    /**
     * Recherche un état par son identifiant.
     * @param id L'identifiant de l'état à rechercher.
     * @return L'objet Etat correspondant à l'état trouvé, ou null si aucun état correspondant n'est trouvé.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public Etat find(String id) throws SQLException {
        Etat etat = null;
        String sql = "SELECT * FROM etat WHERE id=?;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, id);
        try (ResultSet rs = ps.executeQuery()){
            if(rs.next()){
                etat = new Etat(id, rs.getString("libelle"));
            }
        }
        return etat;
    }

    /**
     * Récupère tous les états présents dans la base de données.
     * @return Une liste d'objets Etat représentant tous les états présents dans la base de données.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public List<Etat> findAll() throws SQLException {
        List<Etat> etatList = new ArrayList<>();
        String sql = "SELECT * FROM etat";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            Etat etat = new Etat(
                    rs.getString("id"),
                    rs.getString("libelle")
            );
            etatList.add(etat);
        }
        return etatList;
    }
}
