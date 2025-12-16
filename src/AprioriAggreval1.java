import java.util.*;

import com.google.gson.Gson;

import static java.util.Collections.sort;

public class AprioriAggreval1 {

    // rule-klasse til regler og mqtt
    public static class Rule {
        public String navn;
        public final Set<Integer> X;      // antecedent
        public final Set<Integer> Y;      // consequent
        public final double support;
        public final double confidence;
        public final double lift;

        public Rule(String name, Set<Integer> X, Set<Integer> Y,
                    double support, double confidence, double lift) {

            this.navn = name;
            this.X = X;
            this.Y = Y;
            this.support = support;
            this.confidence = confidence;
            this.lift = lift;
        }

        // JSON tekst, som er nem at sende via MQTT
        public String toJson() {
            Map<String, Object> map = new HashMap<>();
            map.put("navn", navn);
            map.put("X", X);
            map.put("Y", Y);
            map.put("support", support);
            map.put("confidence", confidence);
            map.put("lift", lift);

            return new Gson().toJson(map); // denne ressurse gør det nemmere at overføre Set<String> til JSON
        }

        @Override
        public String toString() {
            return (navn != null ? navn + " : " : "") +
                    X + " -> " + Y +
                    " | sup=" + support +
                    ", conf=" + confidence +
                    ", lift=" + lift;
        }
    }

    // Felter til Apriori
    private final List<Set<Integer>> transactions;
    public static final Set<Set<Integer>> frequentItemsets = new HashSet<>();
    private final double minsup;

    static List<Set<Integer>> F1 = new ArrayList<>();
    static List<Set<Integer>> F2 = new ArrayList<>();
    static List<Set<Integer>> F3 = new ArrayList<>();

    public AprioriAggreval1(List<Set<Integer>> transactions) {
        this.transactions = transactions;
        this.minsup = 0.005; // default
    }

    public List<Set<Integer>> getAggreval() {
        return transactions;
    }

    // SUPPORT

    // support(X): andel af transaktioner der indeholder hele itemsettet X
    public double support(Set<Integer> X) {
        int count = 0;
        if (transactions == null || transactions.isEmpty()) return 0.0;

        for (Set<Integer> t : transactions) {
            if (t.containsAll(X)) {
                count++;
            }
        }
        return (double) count / transactions.size();
    }

    //HJÆLPEMETODER TIL APRIORI

    // generateCandidates: danner C_{k+1} ud fra F_k
    private List<Set<Integer>> generateCandidates(List<Set<Integer>> Fk) {
        List<Set<Integer>> Ck1 = new ArrayList<>();

        List<List<Integer>> sorteretFk = new ArrayList<>();
        for (Set<Integer> t : Fk) {
            List<Integer> candidate = new ArrayList<>(t);
            Collections.sort(candidate);
            sorteretFk.add(candidate);
        }

        Set<Set<Integer>> settFør = new HashSet<>(); // for at skabe et korrekt third pass, må dataen sorteres. et HashSet er nemlig uordnet.
        for (int i = 0; i < sorteretFk.size(); i++) {
            for (int j = i + 1; j < sorteretFk.size(); j++) {
                List<Integer> a = sorteretFk.get(i); // a er en arraylist med værdi af i på ethvert punkt
                List<Integer> b = sorteretFk.get(j); // b er en arraylist af værdier af i+1 på ethvert givent tidspunkt

                // join listerne kun hvis de første k-1 matcher (k = størrelse af a)
                if (a.size() == 1 || prefixMatches(a, b)) {
                    List<Integer> joinedList = new ArrayList<>(a);
                    // tilføj det sidste element fra b (som er større eller forskelligt pga. sortering)
                    joinedList.add(b.get(b.size() - 1));
                    Collections.sort(joinedList); // sørger for at listen er konstant reproducerbar, eller deterministisk.

                    Set<Integer> joinedSet = new HashSet<>(joinedList);
                    if (!settFør.contains(joinedSet)) { // settFør vil sørge for at vi ikke adder noogle duplikater til joinedSet.
                        settFør.add(joinedSet);
                        Ck1.add(joinedSet);
                    }
                }
            }
        }
        return Ck1;
    }

    // prefixMatches: tjekker om to lister er ens på de første k-1 positioner
    private boolean prefixMatches(List<Integer> A, List<Integer> B) {
        if (A.size() != B.size()) return false;

        for (int i = 0; i < A.size() - 1; i++) {
            if (!A.get(i).equals(B.get(i))) {
                return false;
            }
        }
        return true;
    }

    // filtrerer kandidater under minsup fra
    private List<Set<Integer>> filterFrequent(List<Set<Integer>> Ck) {
        List<Set<Integer>> Fk = new ArrayList<>();

        for (Set<Integer> cand : Ck) {
            double x = support(cand);
            if (x >= minsup) {
                Fk.add(cand);
            }
        }
        return Fk;
    }

    //SELVE APRIORI

    public Set<Set<Integer>> Apriori() {

        //alternativ, der giver sorterede lister
        ///  skal vi referere til chat på f eks dette? treeSet var et forslag som gav os mulighed til at samle unike, sorterede i items

        Set<Integer> uniqueItems = new TreeSet<>(); // vi gør det samme som nedenunder, men behandler dataen konsekvent som Set<Integer>, hvilke er uordnede.
        // derfor får vi dataen sorteret samtidig som vi husker de unikke værdier.
        for (Set<Integer> t : transactions) {
            uniqueItems.addAll(t);// vi adder hver unike værdi. det er altså ikke duplikater her.
        }
        /*
        List<Integer> items = new ArrayList<>();
        for (Set<Integer> t : transactions) {
            items.addAll(t);
        }
        sort(items);
*/
        // C1: alle 1-itemsets
        List<Set<Integer>> C1 = new ArrayList<>();
        for (Integer s : uniqueItems) {
            C1.add(Collections.singleton(s));
        }

        // F1
        F1 = filterFrequent(C1);
        // F1 = removeDuplicateSets(C1);

        // C2 -> F2
        List<Set<Integer>> C2 = generateCandidates(F1);
        F2 = filterFrequent(C2);
        // F1 = removeDuplicateSets(C1);


        // C3 -> F3
        List<Set<Integer>> C3 = generateCandidates(F2);
        F3 = filterFrequent(C3);

        frequentItemsets.clear();
        frequentItemsets.addAll(F1);
        frequentItemsets.addAll(F2);
        frequentItemsets.addAll(F3);

        return frequentItemsets;
    }

    // CONFIDENCE & LIFT
    public double confidence(Set<Integer> X, Set<Integer> Y) {
        Set<Integer> union = new HashSet<>(X);
        union.addAll(Y);

        double supXY = support(union);
        double supX = support(X);

        if (supX == 0.0) return 0.0;

        return supXY / supX;
    }

    public double lift(Set<Integer> X, Set<Integer> Y) {
        Set<Integer> union = new HashSet<>(X);
        union.addAll(Y);

        double supXY = support(union);
        double supX = support(X);
        double supY = support(Y);

        if (supX == 0.0 || supY == 0.0) return 0.0;

        return supXY / (supX * supY);
    }

    //  Genererer 1-itemsets til 1-itemset-regler.
    public List<Rule> generateRules(double minConfidence) {
        List<Rule> rules = new ArrayList<>();

        for (Set<Integer> itemset : frequentItemsets) {
            if (itemset.size() < 2) continue;

            List<Integer> items = new ArrayList<>(itemset);

            for (int i = 0; i < items.size(); i++) {
                for (int j = 0; j < items.size(); j++) {
                    if (i == j) continue;

                    String name = items.get(i).toString();

                    Set<Integer> X = new HashSet<>(Collections.singleton(items.get(i)));
                    Set<Integer> Y = new HashSet<>(Collections.singleton(items.get(j)));

                    double supXY = support(itemset);
                    double conf = confidence(X, Y);

                    if (conf >= minConfidence) {
                        double liftVal = lift(X, Y);
                        Rule r = new Rule(name, X, Y, supXY, conf, liftVal);
                        rules.add(r);
                    }
                }
            }
        }
        return rules;
    }
}
/// vi skal vise til kilder på metoder og teknikker der bliver brugt, så vi viser til det vi har læst