import java.util.*;

 public class genererKombs {
     static void genererKombs(int index, List<String> combInst, int comblength, // geeks4geeks
                              Set<ArrayList<String>> resultat, String[] arr) {
         // index er algoritmens nuværende placering i arr, hvilket er den originale array, eller vores input.
       // combInst er en instans af en kombination af forskellige Strings der er i gang med at blive bygget. resultat er vores resultat.
         if (combInst.size() == comblength) { // Når vi har udvalgt nok elementer, gemmes kombinationen og rekursionen stopper.
             resultat.add(new ArrayList<>(combInst));
             return;
         }
         // Dette er 'mønsteret' for vores rekursion. mønsteret indeholder 1) valg af arr/element, 2) udforskning af kombinationer,
         // 3) fravalg af det forrige element til fordel for at finde nye kombi
         for (int i = index; i < arr.length; i++) {
             combInst.add(arr[i] + ""); // arr er en midlertidig array som holder combInst-Strings
             genererKombs(i + 1, combInst, comblength, resultat, arr);
             combInst.remove(combInst.size() - 1);
         }
     }
     // Metoden fodre generereKobs med input, og kommunikere med main. Den laver sine egne sets med arraylists, som er resultat1.
     static Set<ArrayList<String>> generer(String arr[], int r) {
         Set<ArrayList<String>> resultat1 = new HashSet<>();
         List<String> inputs = new ArrayList<>();
         genererKombs(0, inputs, r, resultat1, arr);
         return resultat1;
     }
 }