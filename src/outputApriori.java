// outputApriori.java
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class outputApriori {

    // med tanke på single responsibility principle flyttet vi denne funktion over i en egen metode, istedenfor i loadData.
    // Vi vil konvertere ArrayList<alleMordere> til en List<List<Integer>> for at få mindre tematik over i selve apriori.
    public static List<Set<Integer>> transactionBuilder() { // her laves transactions ved brug af alleMordereMap i WebScraper
        // For at det skal virke skal man køre WebScrape først, så den har data.
        if (WebScrape.alleMordereMap == null || WebScrape.alleMordereMap.isEmpty()) {
            if (WebScrape.alleMordereMap.isEmpty()) {
                WebScrape.loadData();
            }
        }
        RegelProcessing.tilskrivStjernetegnTilAlle(); // Vi tilskriver stjernetegn først

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

        Set<AprioriAggreval1.Rule> rules = RegelProcessing.processRules(txs);

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
                System.out.println("Publiseret: " + r);
            }
            client.disconnect();
            client.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("Antal regler: " + rules.size());

        // Output til JSON
        Gson gson = new Gson();
        List<List<Integer>> f1ForP5 = new ArrayList<>();

        for (Set<Integer> t : txs) { // her iterer vi igennem hver transaction i txs
            List<Integer> enkelt = new ArrayList<>(t); // lav en arrayliste med integers
            Collections.sort(enkelt);                 // valgfritt: sorterer for konsistent output
            f1ForP5.add(enkelt);
        }
        try (FileWriter writer = new FileWriter("/Users/isakreite/IdeaProjects/MQTT/f1.json")) {
            gson.toJson(f1ForP5, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter("/Users/isakreite/IdeaProjects/MQTT-scatter/rules.json")) {
            gson.toJson(rules, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Labels for visualisering: laver læsbare navne
    private static final String[] Stjernetegn = {"Vædderen", "Tyren", "Tvillingerne", "Krebsen", "Løven", "Jomfruen", "Vægten", "Skorpionen", "Skytte", "Stenbukken", "Vandmanden", "Fiskene"}; // motiv 12..15
    private static final String[] MOTIV = {"Visionary", "Mission-oriented", "Hedonistic", "Power-control"};
    private static final String[] TYPE = {"Organiseret", "Uorganiseret", "Mix"}; // type 16..19

    public static String labelForIndex(int idx) {
        if (idx >= 0 && idx < 12) return Stjernetegn[idx];
        if (idx >= 12 && idx < 16) return MOTIV[idx - 12];
        if (idx >= 16 && idx < 20) return TYPE[idx - 16];
        return "Item" + idx;
    }

    public static String labelForSet(Set<Integer> s) {
        if (s == null || s.isEmpty()) return "";
        List<Integer> list = new ArrayList<>(s);
        Collections.sort(list);
        List<String> labels = new ArrayList<>();
        for (int i : list) {
            labels.add(labelForIndex(i));
        }
        return String.join(", ", labels); // join-function gør det muligt for os at se
    }
}
