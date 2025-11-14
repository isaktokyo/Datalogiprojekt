import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.reflect.Array.set;

public class Main {

// importer dataset (CSON, JSV eller noget andet)
// morder - variabler: String name, int antalMord, int[] fødselsdag eller ArrayList fødselsdag

    public void Apriori(Set<String> kandidat) {
        String dbPath = "identifier.sqlite";
        List<String> lines = indlaesData.getSunSignFromDB(dbPath);
        DefaultDict<Integer, List<Integer>> item_counts =
                new DefaultDict<Integer, List<Integer>>(ArrayList.class); // dette er en python metode,
        // Som vi har overført til java. Vi har lånt det.
        DefaultDict<Integer, List<Integer>> pair_counts =
                new DefaultDict<Integer, List<Integer>>(ArrayList.class);

        // first pass
        for (String line : lines) {//find candidate items
            for (String item : line.split(",")) {
                item_counts.get(item).add(1);
            }
          /*  String sign = TilStjernetegn1.findStjernetegn(line);
            System.out.println(line + " → " + sign);
            System.out.println(item_counts);*/
        }
        /*
        Object frequent_items = set();
        for(){}
*/
        System.out.println(item_counts);
        for (String line : lines) {
            item_counts.get(1).add(Integer.parseInt(line));
            System.out.println(item_counts);
        }
    }

    public double supportWall() { // denne metode skal afgrænse hvor meget support
        // der skal være for en link mellom to datapunkter. F eks: morder - fisk
        double threshold = 0.75;
        return threshold;
    }

    public double findSupport() {
        return 0;
    }// returnvalue er bare en placeholder

    // en main som initierer visualiseringen og de forskellige metoder.
    public static void main(String[] args) {
        String dbPath = "identifier.sqlite";
        List<String> datoer = indlaesData.getSunSignFromDB(dbPath);

        for (String dato : datoer) {
            String sign = TilStjernetegn1.findStjernetegn(dato);
            System.out.println(dato + " → " + sign);
        }

        Main m = new Main();
        m.Apriori(new HashSet<>());

        String[] arr = {"hey", "i", "just", "met", "you"};
        int r = 3;
        Set<ArrayList<String>> resultat = genererKombs.generer(arr, r);
        for (List<String> comb : resultat) {
            for (String carly : comb) {
                System.out.print(carly + " ");
            }
            System.out.println();
        }
    }
}
