import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class indlaesData{
    public static List<String> getFødselFromDB(String dbPath){
        List<String> fødsler =  new ArrayList<>();
        Connection conn = null; // dette er bare kode fra forelæsningen. det skal fungere at hente fra sqlite filen
        try {
            String url = "jdbc:sqlite:"+dbPath;
            conn = DriverManager.getConnection(url);
            System.out.println("Got it!");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT navn, fødsel, comment FROM tbl1");

            while (rs.next()) {
                String navn = rs.getString("navn");
                String fødsel = rs.getString("fødsel");
                String comment = rs.getString("comment");
                System.out.println(navn + " | " + fødsel + " | " + comment);
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return fødsler;
    }
}