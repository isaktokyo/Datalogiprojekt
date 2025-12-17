// RegelProcessing.java
import java.util.*;

// i denne kode sker regelgenerering, binær konvertering og dataskalering, hvor vi kan få jævnere ræpresentation
public class RegelProcessing {


    public static class DomainSupportScaler implements SupportScaler {
        @Override
        public double scale(Set<Integer> itemset, double rawSupport) {
            double scaled = rawSupport;
            for (Integer i : itemset) {
                if (i >= 12 && i < 16) scaled /= 4.0;
                else if (i >= 16) scaled /= 3.0;
            }
            return scaled;
        }
    }

    static List<Set<Integer>> removeDuplicateSets(List<Set<Integer>> input) {
        List<Set<Integer>> result = new ArrayList<>();
        Set<Set<Integer>> seen = new HashSet<>();

        for (Set<Integer> s : input) {
            List<Integer> sorted = new ArrayList<>(s);
            Collections.sort(sorted);
            Set<Integer> normalized = new LinkedHashSet<>(sorted);

            if (!seen.contains(normalized)) {
                seen.add(normalized);
                result.add(s);
            }
        }
        return result;
    }

    public static Set<AprioriAggreval1.Rule> processRules(List<Set<Integer>> txs) {

        List<List<Integer>> txsTilBi = OrkestrerData.convertToBinary(txs);
        List<Set<Integer>> transactions = OrkestrerData.convertBinaryToTransactions(txsTilBi);
        AprioriAggreval1 apriori = new AprioriAggreval1(transactions);
        apriori.Apriori();

        SupportScaler scaler = new DomainSupportScaler();

        // nedenunder er kode der tilsvarer en GenerateRules metode fra referance-koden af scepion1d
        Set<AprioriAggreval1.Rule> rules = new HashSet<>();
        for (Set<Integer> itemset : AprioriAggreval1.frequentItemsets) { // frequent Itemsets indeholder f1, f2 og f3.
            int n = itemset.size();
            if (n < 2) continue; // vi skal bruge itemsets større end 2 værdier for at lave rules.

            for (int r = 1; r < n; r++) {
                Set<Set<Integer>> Xsets = genererKombs.generer(itemset, r); //
                for (Set<Integer> X : Xsets) {
                    Set<Integer> Y = new HashSet<>(itemset);
                    Y.removeAll(X);

                    double supXY = scaler.scale(itemset, apriori.support(itemset));
                    double conf = apriori.confidence(X, Y);
                    double liftVal = apriori.lift(X, Y);
                    String name = CreateLabels.labelForSet(X) + " -> " + CreateLabels.labelForSet(Y);

                    if (conf >= 0.0) {
                        AprioriAggreval1.Rule rule = new AprioriAggreval1.Rule(name, X, Y, supXY, conf, liftVal);
                        rules.add(rule);
                    }
                }
            }
        }
        return rules;
    }
}
