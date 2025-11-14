import java.util.*;

 public class genererKombs {
     static void genererKombs(int index, List<String> inputs, int comblength, // geeks4geeks
                              Set<ArrayList<String>> resultat, String[] arr) {
         if (inputs.size() == comblength) {
             resultat.add(new ArrayList<>(inputs));
             return;
         }
         for (int i = index; i < arr.length; i++) {
             inputs.add(arr[i] + ""); // arr er en midlertidig array som holder inputs-Strings
             genererKombs(i + 1, inputs, comblength, resultat, arr);
             inputs.remove(inputs.size() - 1);
         }
     }
     static Set<ArrayList<String>> generer(String arr[], int r) {
         int n = arr.length;
         Set<ArrayList<String>> resultat1 = new HashSet<>();
         List<String> inputs = new ArrayList<>();
         genererKombs(0, inputs, r, resultat1, arr);
         return resultat1;
     }
 }