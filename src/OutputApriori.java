// OutputApriori.java
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OutputApriori {

    // med tanke på single responsibility principle fungerer denne klasses som entry point for programmet.
    // Vi vil konvertere ArrayList<alleMordere> til en List<List<Integer>> for at få mindre tematik over i selve apriori.

    static void main() { // denne main køres for at lave JSon og give output
        List<Set<Integer>> txs = OrkestrerData.transactionBuilder();

        Set<AprioriAggreval1.Rule> rules = RegelProcessing.processRules(txs);

        System.out.println("Antal regler: " + rules.size());

        // Output til JSON
        Gson gson = new Gson();
        List<List<Integer>> f1ForP5 = new ArrayList<>(); // vi initierer en arrayliste der skal fodres ind i vores histogram-visualisering.
        for (Set<Integer> t : txs) {
            for (Integer item : t) {
                    f1ForP5.add(List.of(item));           // én-verdi-liste for f1
            }
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
