import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

// importer dataset (CSON, JSV eller noget andet)
// morder - variabler: String name, int antalMord, int[] fødselsdag eller ArrayList fødselsdag

    public void Apriori(Set<String> kandidat) {
        String dbPath = "identifier.sqlite";
        List<String> dates = indlaesData.getSunSignFromDB(dbPath);
        DefaultDict<Integer, List<Integer>> item_counts = new DefaultDict<Integer, List<Integer>>(ArrayList.class); // dette er en python metode,
        // Som vi har overført til java. Vi har lånt det.

        List<String> signList = new ArrayList<>();
        for (String line : dates) {
        String sign = TilStjernetegn1.findStjernetegn(line);
        signList.add(sign);
        }

        // first pass
        for (String line : dates) {//find candidate items
            for (String item : line.split(",")) {
                item_counts.get(item).add(1);
            }
            System.out.println("frekvens af stjernetegn (first pass): "+item_counts);
        }
       Set<ArrayList<String>> item_sets = genererKombs.generer(dates.toArray(new String[dates.size()]), 2);
        System.out.println(item_sets);
    }
    public double supportWall(double customThreshold) {
        // denne metode skal afgrænse hvor meget support
        // der skal være for en link mellom to datapunkter. F eks: morder - fisk.
        // den tager input i main (customThreshold)
        double defaultThreshold = 0.75;
        if (customThreshold > 0 && customThreshold <= 1) {
            return customThreshold;
        }else {
            return defaultThreshold;
            }
      }

    public double findSupport(List<String> dataset, String item) { // Logik til at se forekomster af item / totalt antal elementer i datasættet
        int count = 0;
        for (String line : dataset) {
            if (line.contains(item)) { // hvis søgeordet findes, tælles det
                count++;
            }
        }
        return (double) count / dataset.size();
    }

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
        int r = 4;
        Set<ArrayList<String>> resultat = genererKombs.generer(arr, r);
        for (List<String> comb : resultat) {
            for (String carly : comb) {
                System.out.print(carly + " ");
            }
            System.out.println();
        }
        // dummy datasæt
        List<String> dataset = new ArrayList<>();
        dataset.add("Morder, Fisk");
        dataset.add("Morder, Fisk");
        dataset.add("Morder, Fisk");
        dataset.add("Morder, Fisk");
        dataset.add("Morder, Fisk");
        dataset.add("Morder, Fisk");
        dataset.add("Morder, Fisk");
        dataset.add("Morder, Fisk");


        // Find support for "morder"
        //beregner sandsynligheden for om ordet 'morder'optræder i dataslttet
        String searchedItem = "Fisk";
        double support = m.findSupport(dataset, searchedItem);
        System.out.println("Support for " + searchedItem + ": " + support);

        // Bruger threshold for at evaluere 'vigtigheden'
        double threshold = m.supportWall(0.6);
        // her har vi vores brugerdefinerede værdi der sammenligner og derved afgøre en hyppighed
        if (support > threshold) {
            System.out.println("kombinationen ' " + searchedItem + " ' er hyppig.");
        } else {
            System.out.println("Kombinationen ' " + searchedItem + " ' er ikke hyppig.");
        } // ved sammenligningen af support med threshold, afgøres hyppigheden af et item
    }
}
