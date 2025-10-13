import java.util.List;
import java.util.Set;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

// importer dataset (CSON, JSV eller noget andet)
// morder - variabler: String name, int antalMord, int[] fødselsdag eller ArrayList fødselsdag

    public void  Apriori(Set<String> kandidat){

    }
    public double supportWall(){return 0.75;} // denne metode skal afgrænse hvor meget support
    // der skal være for en link mellom to datapunkter. F eks: morder - fisk
    public double findSupport(){return 0;}// returnvalue er bare en placeholder

// metode for at konvertere fødselsdag til horoskop (if -setninger)

// Algoritme - tælle hvor mange stjernetegn der er
//
    // en main som initierer visualiseringen
    public static void main(String []args){
       /* Connection conn = null; // dette er bare kode fra forelesningen. det skal fungere at hente fra sqlite filen
        try {
            String url = "jdbc:sqlite:identifier.sqlite";
            conn = DriverManager.getConnection(url);
            System.out.println("Got it!");
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
        } */
    }
}}
