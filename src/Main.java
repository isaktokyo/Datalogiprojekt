import java.util.Set;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

// importer dataset (CSON, JSV eller noget andet)
// morder - variabler: String name, int antalMord, int[] fødselsdag

    public static String findStjernetegn(int dag, int maaned) {
        if ((maaned == 3 && dag >= 21) || (maaned == 4 && dag <= 19)) return "Vædder";
        if ((maaned == 4 && dag >= 20) || (maaned == 5 && dag <= 20)) return "Tyr";
        if ((maaned == 5 && dag >= 21) || (maaned == 6 && dag <= 20)) return "Tvilling";
        if ((maaned == 6 && dag >= 21) || (maaned == 7 && dag <= 22)) return "Krebs";
        if ((maaned == 7 && dag >= 23) || (maaned == 8 && dag <= 22)) return "Løve";
        if ((maaned == 8 && dag >= 23) || (maaned == 9 && dag <= 22)) return "Jomfru";
        if ((maaned == 9 && dag >= 23) || (maaned == 10 && dag <= 22)) return "Vægt";
        if ((maaned == 10 && dag >= 23) || (maaned == 11 && dag <= 21)) return "Skorpion";
        if ((maaned == 11 && dag >= 22) || (maaned == 12 && dag <= 21)) return "Skytte";
        if ((maaned == 12 && dag >= 22) || (maaned == 1 && dag <= 19)) return "Stenbuk";
        if ((maaned == 1 && dag >= 20) || (maaned == 2 && dag <= 18)) return "Vandmand";
        if ((maaned == 2 && dag >= 19) || (maaned == 3 && dag <= 20)) return "Fisk";
        return "Ukendt";
    }
    public void  Apriori(Set<String> kandidat){}
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
}
