package DbTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDb {

    public static Connection ConnectDb() {
        String url = "jdbc:mysql://localhost:3306/applicationticket";
        String user = "root";
        String password = "";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion réussie !");
            return conn;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
		
    }
}
