import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class outputApriori {

    public outputApriori() throws MqttException {
    }

    public static void tilskrivStjernetegnTilAlle() {

        for (WebScrape.MorderDatapunkt m : WebScrape.alleMordereMap.values()) {
            Integer sign = -1;
            Integer d = m.getDag();     // kan være -1 for "ikke sat"
            Integer mm = m.getMåned();
            boolean hasDay = d != null && d > 0; // vi bruger booleans for at undersøge om datapunkterne har opgivede dage eller ikke.
            boolean hasMonth = mm != null && mm > 0 && mm <= 12;

            if (hasDay && hasMonth) {
                sign = TilStjernetegn1.findStjernetegn(d, mm);
            } else if (m.fødselsdag != null && m.fødselsdag.length() >= 4 && !m.fødselsdag.equals("Ikke sat endnu")) {
                // efter nogen bugs bruger vi dette fallback: vi bruger de første 4 tegn (ddMM) hvis de findes og er parseable
                try {
                    String ddmm = m.fødselsdag.substring(0, 4);
                    int parsedDag = Integer.parseInt(ddmm.substring(0, 2));
                    int parsedMaaned = Integer.parseInt(ddmm.substring(2, 4));
                    sign = TilStjernetegn1.findStjernetegn(parsedDag, parsedMaaned); // vi laver int ud af substrings i fødselsdatoer, og fodrer TilStjernetegn1
                    if (!hasDay) m.dag = parsedDag;
                    if (!hasMonth) m.maaned = parsedMaaned;
                } catch (Exception ignored) {
                    sign = -1; // vi falder tilbage på -1 hvis man ikke finder en værdi.
                }
            } else if (!hasDay && hasMonth) {
                // hvis vi mangler dag men måned findes setter vi dag til den 15.
                int setDay = 15;
                m.dag = setDay;
                m.maaned = mm;
                m.fødselsdag = String.format("%02d%02d", setDay, mm);
                sign = TilStjernetegn1.findStjernetegn(setDay, mm);
            } else {
                sign = -1;
            }
            m.setStjernetegn(sign); //
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
            List<Integer> vector = new ArrayList<>(Collections.nCopies(alleItems, 0));// kapasiteten til vektoren er sat til 21

            Integer s = m.getStjernetegn();

            // for stjernetegn, motiv og type vil vi sætte vector-værdier hvis de ikke er 0, og indenfor range af det de skal være.
            if (s != 0 && s >= 0 && s < 12) {
                vector.set(m.getStjernetegn(), 1);
            }
            if (m.motiv != null && m.motiv >= 0 && m.motiv <= 3) {
                vector.set(m.motiv + 12, 1); // setter den korrekte index ind direkte
            }
            if (m.type != null && m.type >= 0 && m.type <= 3) {
                vector.set(m.type + 16, 1);
            }
            System.out.println(vector);

            result.add(vector);
        }
        return result;
    }

    // med tanke på single responsibility principle flyttet vi denne funktion over i en egen metode, istedenfor i loadData.
    // Vi vil konvertere ArrayList<alleMordere> til en List<List<Integer>> for at få mindre tematik over i selve apriori.

    public static List<Set<Integer>> transactionBuilder() { // her laves transactions ved brug af alleMordereMap i WebScraper
        // For at det skal virke skal man køre WebScrape først, så den har data.
        if (WebScrape.alleMordereMap == null || WebScrape.alleMordereMap.isEmpty()) {
            if (WebScrape.alleMordereMap.isEmpty()) {
                WebScrape.loadData();
            }
        }
        tilskrivStjernetegnTilAlle();// Vi tilskriver stjernetegn først

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

    static void main() {
        List<Set<Integer>> txs = transactionBuilder();
        System.out.println("Tx count = " + txs.size());
        List<List<Integer>> convert = convertToBinary(txs);

        System.out.println(convert);
        List<Set<Integer>> transactions = convertBinaryToTransactions(convert);

        AprioriAggreval1 apriori = new AprioriAggreval1(transactions);
        apriori.Apriori();

        List<Set<Integer>> resultat = apriori.getAggreval();
        System.out.println(resultat);
        Map<Set<Integer>, Double> groupSup = apriori.allSetsSupport();

        // vi bruger entry-klassen som en måde at hænte verdier fra en given nøgle i mappet groupSup
        // kilde: https://stackoverflow.com/questions/8689725/map-entry-how-to-use-it
        List<Set<Integer>> f1 = AprioriAggreval1.F1;
        List<Set<Integer>> f2 = AprioriAggreval1.F2;
        List<Set<Integer>> f3 = AprioriAggreval1.F3;

        System.out.println("F1:"); // vi printer first pass med support

        for (Set<Integer> f : f1) {
            // lav en ny arraylist med f1 sit indhold og itererer gennem den istedenfor
            // check hvis f er del af denne nye arraylist, print kun hvis den ikke er
            System.out.println(f + " -> " + String.format("%.2f", apriori.support(f)));
        }
        System.out.println("F2:"); // vi printer second pass med support
        for (Set<Integer> f : f2) {
            /// todo: lav en if-sætning som ikke lader de samme to items vises to gange f eks [16, 15] og [15, 16]
        // prøv evt. equals x = y -> removeduplicate

            System.out.println(f + " -> " + String.format("%.2f", apriori.support(f)));
        }
        ///  dette output består ikke af 3 forskellige elementer, hvorfor?
        System.out.println("F3:"); // vi printer third pass med support
        for (Set<Integer> f : f3) {
            System.out.println(f + " -> " + String.format("%.2f", apriori.support(f)));
            double normSup = apriori.normalizedSupport(f);
            System.out.println("Normalized support: " + f + " -> " + String.format("%.2f%%", normSup * 100));
            // String minsup = AprioriAggreval1.minsup;
            // System.out.println(AprioriAggreval.generateRules(minsup));
        }
        Set<AprioriAggreval1.Rule> rules = new HashSet<>();
        for(Set<Integer> itemset : AprioriAggreval1.frequentItemsets){
                int n = itemset.size();
                if (n < 2) continue;

                System.out.println();

                for (int r = 1; r < n; r++) { // X kan ha 1..n-1 elementer
                    Set<Set<Integer>> Xsets = genererKombs.generer(itemset, r);

                    for (Set<Integer> X : Xsets) {
                        Set<Integer> Y = new HashSet<>(itemset);
                        Y.removeAll(X);

                        double supXY = apriori.support(itemset);
                        double conf = apriori.confidence(X, Y);
                        double liftVal = apriori.lift(X, Y);

                        if (conf >= 0.6) { // minConfidence
                            AprioriAggreval1.Rule rule = new AprioriAggreval1.Rule(X, Y, supXY, conf, liftVal);
                            rules.add(rule);
                        }
                    }
                }
            }
            try {
        String broker = "tcp://localhost:1883"; // dette er en standard broker for et java-projekt
        String clientId = "apriori-java";
        // Laver klient
        MqttClient client = new MqttClient(broker, clientId);
        client.connect();
                for (AprioriAggreval1.Rule r : rules) {
                    MqttMessage message = new MqttMessage(r.toJson().getBytes());
                    message.setQos(0);
                    client.publish("apriori/rule", message);
                    System.out.println("Published: " + r);
                }
                client.disconnect();
                client.close();
} catch (MqttException e) {
                e.printStackTrace();
            }
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("output.json")) {
            gson.toJson(rules, writer);  // rules er et Set<AprioriAggreval1.Rule>
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
    }/// todo vi må forstærke stjernetegn i dataen, da de ikke når over minsup nogen gang. motiv(support) / 3, type(support) / 4





