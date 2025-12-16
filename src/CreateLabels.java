import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CreateLabels {

    // Helper label arrays og metoder for at lave læsbare navne
    private static final String[] Stjernetegn = {
            "Vædderen","Tyren","Tvillingerne","Krebsen","Løven","Jomfruen",
            "Vægten","Skorpionen","Skytte","Stenbukken","Vandmanden","Fiskene"
    };
    // motiv 12..15
    private static final String[] MOTIV = {"Visionary","Mission-oriented","Hedonistic","Power-control"};
    // type 16..19
    private static final String[] TYPE = {
            "Organiseret","Uorganiseret","Mix"
    };

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
        return String.join(", ", labels);
    }

}
