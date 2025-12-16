// RegelProcessing.java
import java.util.*;

// i denne kode sker regelgenerering, binær konvertering og dataskalering, hvor vi kan få jævnere ræpresentation
public class RegelProcessing {

    public static void tilskrivStjernetegnTilAlle() {
        for (WebScrape.MorderDatapunkt m : WebScrape.alleMordereMap.values()) {
            Integer sign = -1;
            Integer d = m.getDag(); // kan være -1 for "ikke sat"
            Integer mm = m.getMåned();
            boolean hasDay = d != null && d > 0;
            boolean hasMonth = mm != null && mm > 0 && mm <= 12;

            if (hasDay && hasMonth) {
                sign = TilStjernetegn1.findStjernetegn(d, mm);
            } else if (m.fødselsdag != null && m.fødselsdag.length() >= 4 && !m.fødselsdag.equals("Ikke sat endnu")) {
                try {
                    String ddmm = m.fødselsdag.substring(0, 4);
                    int parsedDag = Integer.parseInt(ddmm.substring(0, 2));
                    int parsedMaaned = Integer.parseInt(ddmm.substring(2, 4));
                    sign = TilStjernetegn1.findStjernetegn(parsedDag, parsedMaaned);
                    if (!hasDay) m.dag = parsedDag;
                    if (!hasMonth) m.maaned = parsedMaaned;
                } catch (Exception ignored) {
                    sign = -1;
                }
            } else if (!hasDay && hasMonth) {
                int setDay = 15;
                m.dag = setDay;
                m.maaned = mm;
                m.fødselsdag = String.format("%02d%02d", setDay, mm);
                sign = TilStjernetegn1.findStjernetegn(setDay, mm);
            } else {
                sign = -1;
            }
            m.setStjernetegn(sign);
        }
    }

    public static List<Set<Integer>> convertBinaryToTransactions(List<List<Integer>> binaryList) {
        List<Set<Integer>> transacts = new ArrayList<>();
        for (List<Integer> vector : binaryList) {
            Set<Integer> tx = new HashSet<>();
            for (int i = 0; i < vector.size(); i++) {
                if (vector.get(i) == 1) {
                    tx.add(i);
                }
            }
            transacts.add(tx);
        }
        return transacts;
    }

    public static List<List<Integer>> convertToBinary(List<Set<Integer>> txs) {
        int alleItems = 19;
        List<List<Integer>> result = new ArrayList<>();

        for (WebScrape.MorderDatapunkt m : WebScrape.alleMordereMap.values()) {
            List<Integer> vector = new ArrayList<>(Collections.nCopies(alleItems, 0));

            Integer s = m.getStjernetegn();
            if (s != null && s >= 0 && s < 12) vector.set(s, 1);
            if (m.motiv != null && m.motiv >= 0 && m.motiv <= 3) vector.set(m.motiv + 12, 1);
            if (m.type != null && m.type >= 0 && m.type <= 3) vector.set(m.type + 16, 1);

            System.out.println(vector);
            result.add(vector);
        }
        return result;
    }

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

    private static List<Set<Integer>> removeDuplicateSets(List<Set<Integer>> input) {
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
        List<Set<Integer>> transactions = convertBinaryToTransactions(convertToBinary(txs));
        AprioriAggreval1 apriori = new AprioriAggreval1(transactions);
        apriori.Apriori();

        List<Set<Integer>> f1 = AprioriAggreval1.F1;
        List<Set<Integer>> f2 = AprioriAggreval1.F2;
        List<Set<Integer>> f3 = AprioriAggreval1.F3;
        SupportScaler scaler = new DomainSupportScaler();

        Set<AprioriAggreval1.Rule> rules = new HashSet<>();
        for (Set<Integer> itemset : AprioriAggreval1.frequentItemsets) {
            int n = itemset.size();
            if (n < 2) continue;

            for (int r = 1; r < n; r++) {
                Set<Set<Integer>> Xsets = genererKombs.generer(itemset, r);
                for (Set<Integer> X : Xsets) {
                    Set<Integer> Y = new HashSet<>(itemset);
                    Y.removeAll(X);

                    double supXY = scaler.scale(itemset, apriori.support(itemset));
                    double conf = apriori.confidence(X, Y);
                    double liftVal = apriori.lift(X, Y);
                    String name = outputApriori.labelForSet(X) + " -> " + outputApriori.labelForSet(Y);

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
