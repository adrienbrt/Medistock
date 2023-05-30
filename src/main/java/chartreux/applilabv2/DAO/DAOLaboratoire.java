package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;

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

    public List<Laboratoire> findId(String id) throws SQLException {
        List<Laboratoire> laboratoires = new ArrayList<>();
        String sql = "SELECT * FROM laboratoires WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, id);
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
