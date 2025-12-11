import java.util.*;

import static java.util.Collections.sort;

public class AprioriAggreval1 {

    private final List<Set<Integer>> transactions;
    private static final Set<Set<Integer>> frequentItemsets = new HashSet<>();
    private final double minsup;

    static List<Set<Integer>> F1 = new ArrayList<>();
    static List<Set<Integer>> F2 = new ArrayList<>();
    static List<Set<Integer>> F3 = new ArrayList<>();

    public AprioriAggreval1(List<Set<Integer>> transactions) {
        this.transactions =transactions;
        minsup = 0.05; // default
    }
    public List<Set<Integer>> getAggreval() {
        return transactions;
    }

    // ligesom isaks forrige men rettet til
    public double support(Set<Integer> X) { // data mining bog side 98
        int count = 0;
        if (transactions == null || transactions.isEmpty()) return 0.0;
        for (Set<Integer> t : transactions) {
            if (t.containsAll(X)) {
                count++;
            }
        }
        return (double) count / transactions.size();
    }
    
    // hjælpeklasser - vi skal måske ikke bruge dem, har bedt chat om at lave dem
    // generate candidates - lavet ud fra github manden ifølge chat
    private List<Set<Integer>> generateCandidates(List<Set<Integer>> Fk) {
        List<List<Integer>> Ck1 = new ArrayList<>();

        for (int i = 0; i < Fk.size(); i++) {
            for (int j = i + 1; j < Fk.size(); j++) {

                List<Integer> A =  new ArrayList<>(Fk.get(i)); // vi laver arraylists,
                List<Integer> B =  new ArrayList<>(Fk.get(j)); // så vi kan bruge indeks af de forskellige lister.
                // dette kan man nemligt ikke med HashSet, hvilket vores kode er baseret på.

                // join kun hvis de første k-1 matcher
                if (A.size() == 1 || prefixMatches(A, B)) {

                    List<Integer> joined = new ArrayList<>(A);
                    joined.add(B.get(B.size() - 1));

                    if (!Ck1.contains(joined)) {
                        Ck1.add(joined);
                    }
                }
            }
        }
        List<Set<Integer>> Ckay = new ArrayList<>(); // vores metode returnerer List af set af integers.
        for(List<Integer> Ck : Ck1) { // derfor skal vi iterere igennem Ck1,
            Ckay.add(new HashSet<> (Ck)); // og caste indholdet tilbage til List af set af integer.
        }
        return Ckay;
    }

    // prefix matches, forstår ikke - men de hjælper til join step
    private boolean prefixMatches(List<Integer> A, List<Integer> B) {
        if (A.size() != B.size()) return false;

        for (int i = 0; i < A.size() - 1; i++) {
            if (!A.get(i).equals(B.get(i))) {
                return false;
            }
        }
        return true;
    }

    // github mandens måde at filtrere ud kandidater under minsup
    private List<Set<Integer>> filterFrequent(List<Set<Integer>> Ck) {
        List<Set<Integer>> Fk = new ArrayList<>(); // initierer Liste med itemsets med frequent candidates

        for (Set<Integer> cand : Ck) {
            double x = support(cand);
            if(x>=minsup) {
                Fk.add(cand);
            }
        }
        return Fk;
    }

    // downward closure ifølge bogen Behold
    private boolean allSubsetsFrequent(Set<Integer> candidate, List<Set<Integer>> Fk) {
        for (Integer removed : candidate) {
            Set<Integer> subset = new HashSet<>(candidate);
            subset.remove(removed);

            if (!Fk.contains(subset)) {
                return false;
            }
        }
        return true;
    }
    public Map<Set<Integer>, Double> allSetsSupport() {
        Map<Set<Integer>, Double> Support = new HashMap<>();

        for(Set<Integer> itemSet : frequentItemsets) { // vi vil finde første plads i et itemset
            // og pladsen efter, og beregne deres support
                double x = support(itemSet);
                Support.put(itemSet, x);
        }
        return Support;
    }

    // selve apriori metoden (alt er apriori, men her knyttes ting sammen
    public Set<Set<Integer>> Apriori() {

        // dette er F1
        List<Integer> items = new ArrayList<>();
        for (Set<Integer> t : transactions){
            items.addAll(t);
            sort(items); // sorterer items alfabetisk
        }

        //laver alle C1 (de unikke items)
        List<Set<Integer>> C1 = new ArrayList<>();
        for (Integer s : items) {
            C1.add(Collections.singleton(s));
        }

        // F1
         F1 = filterFrequent(C1);

        // C2 til F2
        List<Set<Integer>> C2 = generateCandidates(F1);
         F2 = filterFrequent(C2);

        // C3 til F3
        List<Set<Integer>> C3 = generateCandidates(F2);
         F3 = filterFrequent(C3);
        frequentItemsets.clear();
        frequentItemsets.addAll(F1);
        frequentItemsets.addAll(F2);
        frequentItemsets.addAll(F3);
        return frequentItemsets;
    }

    public double confidence(Set<Integer> X, Set<Integer> Y) {
        Set<Integer> union = new HashSet<>(X);
        union.addAll(Y);

        double supXY = support(union); // supXY viser hvor stor en andel af transaktionenerne indeholder både X og Y
        double supX  = support(X); // transaktioner som kun indeholder X

        if (supX == 0) return 0.0;

        return supXY / supX;
    }

    public double lift(Set<Integer> X, Set<Integer> Y) {
        Set<Integer> union = new HashSet<>(X);
        union.addAll(Y);

        double supXY = support(union);
        double supX  = support(X);
        double supY  = support(Y);

        if (supX == 0 || supY == 0) return 0.0;

        return supXY / (supX * supY);
    }
 ///  todo: lave prints med confidence og lift i WebScrape, lave en ny klasse der overtager WebScrape-metoder, lave variabler der kan sendes over med mqtt.
} /// vi må også finde kilder på metoder og teknikker der bliver brugt, så vi viser til at vi har læst (kan være geeks4geeks eller w3schools)