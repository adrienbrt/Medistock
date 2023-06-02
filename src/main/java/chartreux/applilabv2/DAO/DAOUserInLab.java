package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.UserInLab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOUserInLab {
    private final Connection cnx;

    public DAOUserInLab(Connection cnx) {
        this.cnx = cnx;
    }

    public List<UserInLab> getUserInLabRecordsByUserId(String userId) throws SQLException {
        List<UserInLab> userInLabRecords = new ArrayList<>();
        String query = "SELECT * FROM userInLab WHERE userId = ?";
        PreparedStatement statement = cnx.prepareStatement(query);
        statement.setString(1,userId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            String labId = resultSet.getString("labId");
            String roleId = resultSet.getString("roleId");

            // Créez un nouvel objet UserInLab avec les valeurs récupérées
            UserInLab userInLab = new UserInLab(userId, labId, roleId);
            userInLabRecords.add(userInLab);
        }
        return userInLabRecords;
    }
}
