package chartreux.applilabv2.DAO;

import java.sql.Connection;

public class DAOCommande {
    private Connection cnx;

    public DAOCommande(Connection cnx) {
        this.cnx = cnx;
    }
}
