import java.util.*;
import com.google.gson.Gson;
import static java.util.Collections.sort;

public class AprioriAggreval1 {

    // RULE-KLASSE (TIL REGLER / MQTT)
    public static class Rule {
        public final Set<Integer> X;      // antecedent
        public final Set<Integer> Y;      // consequent
        public final double support;
        public final double confidence;
        public final double lift;

        public Rule(Set<Integer> X, Set<Integer> Y,
                    double support, double confidence, double lift) {

            this.X = X;
            this.Y = Y;
            this.support = support;
            this.confidence = confidence;
            this.lift = lift;
        }

        // JSON-agtig tekst, som er nem at sende via MQTT
        public String toJson() {
            Map<String, Object> map = new HashMap<>();
            map.put("X", X);
            map.put("Y", Y);
            map.put("support", support);
            map.put("confidence", confidence);
            map.put("lift", lift);

            return new Gson().toJson(map); // denne ressurse gør det nemmere at overføre Set<String> til JSON
        }

        @Override
        public String toString() {
            return X + " -> " + Y +
                    " | sup=" + support +
                    ", conf=" + confidence +
                    ", lift=" + lift;
        }
    }

    // FELTER TIL APRIORI
    private final List<Set<Integer>> transactions;
    public static final Set<Set<Integer>> frequentItemsets = new HashSet<>();
    private final double minsup;

    static List<Set<Integer>> F1 = new ArrayList<>();
    static List<Set<Integer>> F2 = new ArrayList<>();
    static List<Set<Integer>> F3 = new ArrayList<>();

    public AprioriAggreval1(List<Set<Integer>> transactions) {
        this.transactions = transactions;
        this.minsup = 0.05; // default
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
        List<List<Integer>> Ck1 = new ArrayList<>();

        for (int i = 0; i < Fk.size(); i++) {
            for (int j = i + 1; j < Fk.size(); j++) {

                List<Integer> A = new ArrayList<>(Fk.get(i));
                List<Integer> B = new ArrayList<>(Fk.get(j));

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

        List<Set<Integer>> Ckay = new ArrayList<>();
        for (List<Integer> Ck : Ck1) {
            Ckay.add(new HashSet<>(Ck));
        }
        return Ckay;
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

    // (bruges hvis du vil have supports på alle itemsets samlet)
    public Map<Set<Integer>, Double> allSetsSupport() {
        Map<Set<Integer>, Double> supportMap = new HashMap<>();

        for (Set<Integer> itemSet : frequentItemsets) {
            double x = support(itemSet);
            supportMap.put(itemSet, x);
        }
        return supportMap;
    }

    //SELVE APRIORI

    public Set<Set<Integer>> Apriori() {

        List<Integer> items = new ArrayList<>();
        for (Set<Integer> t : transactions) {
            items.addAll(t);
        }
        sort(items);

        // C1: alle 1-itemsets
        List<Set<Integer>> C1 = new ArrayList<>();
        for (Integer s : items) {
            C1.add(Collections.singleton(s));
        }

        // F1
        F1 = filterFrequent(C1);

        // C2 -> F2
        List<Set<Integer>> C2 = generateCandidates(F1);
        F2 = filterFrequent(C2);

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
        double supX  = support(X);

        if (supX == 0.0) return 0.0;

        return supXY / supX;
    }

    public double lift(Set<Integer> X, Set<Integer> Y) {
        Set<Integer> union = new HashSet<>(X);
        union.addAll(Y);

        double supXY = support(union);
        double supX  = support(X);
        double supY  = support(Y);

        if (supX == 0.0 || supY == 0.0) return 0.0;

        return supXY / (supX * supY);
    }

    //  GENERER 1-ITEM → 1-ITEM-REGLER
    public List<Rule> generateRules(double minConfidence) {
        List<Rule> rules = new ArrayList<>();

        for (Set<Integer> itemset : frequentItemsets) {
            if (itemset.size() < 2) continue;

            List<Integer> items = new ArrayList<>(itemset);

            for (int i = 0; i < items.size(); i++) {
                for (int j = 0; j < items.size(); j++) {
                    if (i == j) continue;

                    Set<Integer> X = new HashSet<>(Collections.singleton(items.get(i)));
                    Set<Integer> Y = new HashSet<>(Collections.singleton(items.get(j)));

                    double supXY = support(itemset);
                    double conf = confidence(X, Y);

                    if (conf >= minConfidence) {
                        double liftVal = lift(X, Y);
                        Rule r = new Rule(X, Y, supXY, conf, liftVal);
                        rules.add(r);
                    }
                }
            }
        }
        return rules;
    }
    public double normalizedSupport(Set<Integer> X) {
        double sup = support(X);

        for (Integer i : X) {
            if (i >= 12 && i < 16) sup /= 4.0; // motiv
            else if (i >= 16)      sup /= 3.0; // type
        }
        return sup;
    }
}
// todo: lave prints med confidence og lift i WebScrape, lave variabler der kan sendes over med mqtt.
 /// vi må også finde kilder på metoder og teknikker der bliver brugt, så vi viser til at vi har læst (kan være geeks4geeks eller w3schools)
