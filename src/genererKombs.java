import java.util.*;

public class genererKombs {

    // dette er en rekursiv metode der genererer kombinationer af integers.
    static void genererKombs(int index, Set<Integer> combInst, int comblength,
                             Set<Set<Integer>> resultat, List<Integer> arr) {
        // index er algoritmens nuværende posisjon i arr, combInst er en instans som bygges
        if (combInst.size() == comblength) { // når vi har valgt nok elementer, lagres kombinasjonen
            resultat.add(new HashSet<>(combInst));
            return;
        }

        // Dette er 'mønsteret' for vores rekursion. mønsteret indeholder 1) valg af arr/element, 2) udforskning af kombinationer,
        // 3) fravalg af det forrige element til fordel for at finde nye kombinationer
        for (int i = index; i < arr.size(); i++) {
            combInst.add(arr.get(i)); // arr er en midlertidig array som holder combInst-Integers
            genererKombs(i + 1, combInst, comblength, resultat, arr);
            combInst.remove(arr.get(i));
        }
    }

    // Metoden fodre generereKobs med input, og kommunikere med main. Den laver sine egne sets med arraylists, som er resultat1.
    static Set<Set<Integer>> generer(Set<Integer> itemset, int r) {
        Set<Set<Integer>> resultat1 = new HashSet<>();
        List<Integer> items = new ArrayList<>(itemset);
        Set<Integer> inputs = new HashSet<>();
        genererKombs(0, inputs, r, resultat1, items);
        return resultat1;
    }
}
