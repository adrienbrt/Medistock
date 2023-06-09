package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.Laboratoire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOLaboratoire {
    private final Connection cnx;
    public DAOLaboratoire(Connection cnx){
        this.cnx=cnx;
    }

    /**
     * Recherche un laboratoire par son identifiant.
     * @param id L'identifiant du laboratoire à rechercher.
     * @return Le laboratoire correspondant à l'identifiant, ou null s'il n'existe pas.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public Laboratoire findId(String id) throws SQLException {
        Laboratoire laboratoire = null;
        String sql = "SELECT * FROM laboratoires WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, id);
        try (ResultSet rs = ps.executeQuery()){
            if(rs.next()){
                laboratoire = new Laboratoire(
                        rs.getString("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("ville"),
                        rs.getString("CP")
                );
            }
        }

        return laboratoire;
    }

    /**
     * Récupère tous les laboratoires présents dans la base de données.
     * @return Une liste contenant tous les laboratoires.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public List<Laboratoire> findAll() throws SQLException {
        List<Laboratoire> laboratoires = new ArrayList<>();
        String sql = "SELECT * FROM laboratoires";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            Laboratoire laboratoire = new Laboratoire(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("adresse"),
                    rs.getString("ville"),
                    rs.getString("CP"));
            laboratoires.add(laboratoire);
        }
        return laboratoires;
    }

}
