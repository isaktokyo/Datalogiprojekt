// OutputApriori.java
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OutputApriori {

    // med tanke på single responsibility principle fungerer denne klasses main-metode som entry point for programmet.
    // Vi vil konvertere ArrayList<alleMordere> til en List<List<Integer>> for at få mindre tematik over i selve apriori.

    static void main() { // denne main køres for at lave JSon og give output
        List<Set<Integer>> txs = OrkestrerData.transactionBuilder();

        Set<AprioriAggreval1.Rule> rules = RegelProcessing.processRules(txs);

        System.out.println("Antal regler: " + rules.size());

        // Output til JSON
        Gson gson = new Gson();
        List<List<Integer>> f1ForP5 = new ArrayList<>();

        for (Set<Integer> t : txs) { // her iterer vi igennem hver transaction i txs
            List<Integer> enkelt = new ArrayList<>(t); // laver en ArrayListe med integers med udgangspunkt
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
}
