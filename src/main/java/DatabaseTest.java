import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:33060/world?allowPublicKeyRetrieval=true&useSSL=false",
                    "root",
                    "example"
            );

            System.out.println("Connected!");

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT Name, Population FROM country ORDER BY Population DESC LIMIT 5"
            );

            while (rs.next()) {
                System.out.println(
                        rs.getString("Name") + " - " +
                                rs.getInt("Population")
                );
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}