import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.reflect.Array.set;

public class Main {

// importer dataset (CSON, JSV eller noget andet)
// morder - variabler: String name, int antalMord, int[] fødselsdag eller ArrayList fødselsdag

    public void  Apriori(Set<String> kandidat){
        String dbPath = "identifier.sqlite";
        List<String> lines = indlaesData.getSunSignFromDB(dbPath);
        DefaultDict<Integer, List<Integer>> item_counts =
                new DefaultDict<Integer, List<Integer>>(ArrayList.class); // dette er en python metode,
        // Som vi har overført til java. Vi har lånt det.
        DefaultDict<Integer, List<Integer>> pair_counts =
                new DefaultDict<Integer, List<Integer>>(ArrayList.class);
      //  item_counts.get("Væren").add(1);

        // first pass
        for (String line : lines) {//find candidate items
            for(String item: line.split(",")){
                item_counts.get(item).add(1);
            }
          /*  String sign = TilStjernetegn1.findStjernetegn(line);
            System.out.println(line + " → " + sign);
            System.out.println(item_counts);*/
        }
        Object frequent_items = set();
        for(){

        }






        System.out.println(item_counts);

        for (String line : lines){
            item_counts.get(1).add(Integer.parseInt(line));
            System.out.println(item_counts);
        }
    }
    public double supportWall(){ // denne metode skal afgrænse hvor meget support
        // der skal være for en link mellom to datapunkter. F eks: morder - fisk
       double threshold = 0.75;
        return threshold;

    }
    public double findSupport(){return 0;}// returnvalue er bare en placeholder

// metode for at konvertere fødselsdag til horoskop (if -sætninger)

// Algoritme - tælle hvor mange stjernetegn der er
//
    // en main som initierer visualiseringen
    public static void main(String []args){


            String dbPath = "identifier.sqlite";
            List<String> datoer = indlaesData.getSunSignFromDB(dbPath);

            for (String dato : datoer) {
                String sign = TilStjernetegn1.findStjernetegn(dato);
                System.out.println(dato + " → " + sign);
            }
       /* Connection conn = null; // dette er bare kode fra forelæsningen. det skal fungere at hente fra sqlite filen
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
