package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.Ingredient;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Medicament;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOMedicament {
    private Connection cnx;
    public DAOMedicament(Connection cnx) {
        this.cnx = cnx;
    }

    public Medicament findId(String id) throws SQLException {
        Medicament medicament = null;
        String sql = "SELECT * FROM medicaments WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,id);
        try(ResultSet rs = ps.executeQuery()){
            medicament = new Medicament(
                    id,
                    rs.getString("nom"),
                    rs.getString("forme"),
                    rs.getString("description"),
                    new DAOIngredient(cnx).findId(rs.getString("ingredient_id"))
            );
        }
        return medicament;
    }

    public List<Pair<Medicament, Integer>> findMedicamentLab(Laboratoire laboratoire) throws SQLException {
        List<Pair<Medicament, Integer>> listPair = new ArrayList<>();
        String sql = "SELECT m.id,m.nom,m.forme,m.description, m.ingredient_id ,sm.quantite FROM medicaments m JOIN stock_medicaments sm ON m.id = sm.medicament_id WHERE sm.laboratoire_id = ?;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,laboratoire.getId());
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            Medicament medicament = new Medicament(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("forme"),
                    rs.getString("description"),
                    new DAOIngredient(cnx).findId(rs.getString("ingredient_id"))
            );
            Integer qtt = rs.getInt("quantite");
            Pair<Medicament, Integer> pair = new Pair<>(medicament,qtt);

            listPair.add(pair);
        }
        return listPair;
    }

    public List<Medicament> missingMedicLab(Laboratoire laboratoire) throws SQLException {
        List<Medicament> medicamentList =new ArrayList<>();
        String sql = "SELECT m.id,m.nom,m.forme,m.description, m.ingredient_id FROM medicaments m LEFT JOIN stock_medicaments s ON m.id = s.medicament_id AND s.laboratoire_id = ? WHERE s.medicament_id IS NULL;";
        PreparedStatement ps =cnx.prepareStatement(sql);
        ps.setString(1, laboratoire.getId());
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            Medicament medicament = new Medicament(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("forme"),
                    rs.getString("description"),
                    new DAOIngredient(cnx).findId(rs.getString("ingredient_id"))
            );
            medicamentList.add(medicament);
        }

        return  medicamentList;
    }

    public void createMedoc(Medicament medicament) throws SQLException {
        String id = "ing" + (count()+1);
        String sql = "INSERT INTO medicaments (id, nom, forme, description, ingredient_id) VALUES(?, ?, ?, ?, ?);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,id);
        ps.setString(2,medicament.getNom());
        ps.setString(3, medicament.getForme());
        ps.setString(4, medicament.getDescription());
        ps.setString(5, medicament.getIngredient().getId());

        ps.executeUpdate();
    }

    public void addMedocLab(Medicament medicament, Laboratoire laboratoire, Integer qtt) throws SQLException {
        String sql = "INSERT INTO stock_medicaments (medicament_id, laboratoire_id, quantite) VALUES(?, ?, ?);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,medicament.getId());
        ps.setString(2,laboratoire.getId());
        ps.setInt(3, qtt);

        ps.executeUpdate();
    }

    public void updateMedocLab(Medicament medicament, Laboratoire laboratoire, Integer qtt) throws SQLException {
        String sql = "UPDATE stock_medicaments SET quantite=? WHERE medicament_id=? AND laboratoire_id=?;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, qtt);
        ps.setString(2, medicament.getId());
        ps.setString(3, laboratoire.getId());

        ps.executeUpdate();
    }

    public void updateMedoc(Medicament medicament) throws SQLException {
        String sql = "UPDATE medicaments SET nom=?, forme=?, description=?, ingredient_id=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, medicament.getNom());
        ps.setString(2, medicament.getForme());
        ps.setString(3, medicament.getDescription());
        ps.setString(4, medicament.getIngredient().getId());
        ps.setString(5, medicament.getId());

        ps.executeUpdate();
    }

    public void deleteMedocLab(Medicament medicament,Laboratoire laboratoire) throws SQLException {
        String sql ="DELETE FROM stock_medicaments WHERE medicament_id=? AND laboratoire_id=?;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, medicament.getId());
        ps.setString(2, laboratoire.getId());

        ps.executeUpdate();
    }

    public int count() throws SQLException {
        int count = 0;
        String SQL = "SELECT COUNT(*) FROM medicaments";
        PreparedStatement ps = cnx.prepareStatement(SQL);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
        return count;
    }
}
