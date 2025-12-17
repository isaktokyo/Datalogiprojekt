import java.util.*;

public class OrkestrerData {

    public static List<Set<Integer>> transactionBuilder() { // her laves transactions ved brug af alleMordereMap i WebScraper
        // For at det skal virke skal man køre WebScrape først, så den har data.
        if (WebScrape.alleMordereMap == null || WebScrape.alleMordereMap.isEmpty()) {
            if (WebScrape.alleMordereMap.isEmpty()) {
                WebScrape.loadData();
            }
        }
        tilskrivStjernetegnTilAlle(); // Vi tilskriver stjernetegn først

        List<Set<Integer>> txs = new ArrayList<>();
        for (WebScrape.MorderDatapunkt m : WebScrape.alleMordereMap.values()) {
            Set<Integer> t = new HashSet<>(); // vi initierer et tomt itemset som fyldes med motiv, type og stjernetegn.

            // Stjernetegn (integer)
            Integer z = m.getStjernetegn();
            if (z != null && z >= 0 && z < 12) {
                t.add(z);
                // også legg til lesbart navn (valgfritt)
            }
            // Motiv
            if (m.motiv != null && m.motiv >= 0) { // disse checks måtte til, fordi det er inkonsekvent data.
                t.add(12 + m.motiv);
            }

            // Type (organiseret/uan)
            if (m.type != null && m.type >= 0) {
                t.add(16 + m.type);
            }

            txs.add(t);
        }
        return txs;
    }

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
}
