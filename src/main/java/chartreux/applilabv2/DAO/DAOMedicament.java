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
        String sql = "SELECT m.id,m.nom,m.forme,m.description, m.ingredient_id FROM medicaments m LEFT JOIN stock_ingredients s ON m.id = s.medicament_id AND s.laboratoire_id = ? WHERE s.medicament_id IS NULL;";
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
}
