import java.util.*;

public class WebScrape {
    public static Map<String, MorderDatapunkt> alleMordereMap = new HashMap<>();

    public static class MorderDatapunkt {
        String navn;
        Integer motiv = -1; // -1 = ikke sat endnu.
        Integer type = -1;
        String fødselsdag = "Ikke sat endnu";
        Integer dag = -1;
        Integer maaned  = -1;
        Integer stjernetegn = -1;

        public MorderDatapunkt getMorderDatapunkt () {
            return MorderDatapunkt.this;
        }

        public Integer getDag() {
            return dag;
        }
        public Integer getMåned() {
            return maaned;
        }
        public Integer getStjernetegn() {
            return stjernetegn;
        }

        public void setStjernetegn(Integer stj) {
            if (stj != null) this.stjernetegn = stj;
        }

        public MorderDatapunkt(String navn) {
            this.navn = navn;
        }

        public String toString() {
            return navn + " ¶ " + motiv + " ¶ " + type + " ¶ " + fødselsdag+ " ¶ " /* +gender*/;
        }
        // for denne class bruger vi regEx som metode for data-vask. vi har taget udgangspunkt i denne artikkel: https://www.geeksforgeeks.org/java/regular-expressions-in-java/
        public static String normaliserNavn(String rawName) {
            if (rawName == null) return "";

            String name = rawName.trim();
            if (name.equalsIgnoreCase("Serial Killer Name") || name.equalsIgnoreCase("Position")) {
                return ""; // ignorerer kolonne-navne i csv filerne
            }

            // Hvis der er råFødselData med ;; separator, tager vi kun delen før.
            if (name.contains(";;")) {
                name = name.split(";;")[0].trim();
            }

             //Fjerner anførselstegn
            name = name.replace("\"", "");

            // for at bruge navn som en nøkkel, må rekkefølgen på navne fra fødselsdag og fra motiv være det samme
            if (name.contains(",")) { // derfor gør vi om navne der står med efternavne først
                String[] parts = name.split(",");
                if (parts.length >= 2) {
                    name = parts[1].trim() + " " + parts[0].trim();
                }
            }

            // Vi fjerner URL-deler hvis det findes
            name = name.replaceAll("https?://.*", ""); // alt etter https bli
            name = name.replaceAll("\\d+", "");        // fjern tall

            // Vi normaliserer whitespace
            ///  skal vi have mellemrum i replacement
            name = name.replaceAll("\\s+", " ").trim(); // dette er en todo

            // Lowercase for map-nøkkel
            return name.toLowerCase();
        }
    }

    public static void addToMap(Map<String, MorderDatapunkt> map, String navn, Integer motiv, Integer type, String fødselsdag) {
        // Normaliser navnet for bruk som nøkkel i map-en
        String key = MorderDatapunkt.normaliserNavn(navn);

        // Hent eksisterende person fra map-en, eller lag en ny hvis den ikke finnes
        MorderDatapunkt person = map.get(key);
        if (person == null) { // vi oprætter en ny person hvis den ikke findes
            person = new MorderDatapunkt(navn.trim());
            map.put(key, person);
        }

        // Her opdaterer vi feltene statisk, hvis deres værdi ikke er null.
        if (motiv != null) person.motiv = motiv;
        if (type != null) person.type = type;
        if (fødselsdag != null && fødselsdag.length() == 4) {
            try {
                person.dag = Integer.parseInt(fødselsdag.substring(0, 2));
                person.maaned = Integer.parseInt(fødselsdag.substring(2, 4));
            } catch (NumberFormatException ignored) {}
        }

        if (fødselsdag != null) {
            person.fødselsdag = fødselsdag;
        }
        // debug-hjælper
        //System.out.println("addToMap: nøkkel=" + key + " navn='" + navn + "' motiv=" + motiv + " type=" + type + " fødsels=" + fødselsdag);
    }

    public static ArrayList<String> filter(ArrayList<String> råData) {
        // filtrere ut første kolonne: serial killer name, profile image, eller position
        // filtrere ut https-link (sker i CSVReader), motivation
        råData.removeIf(s -> s.trim().isEmpty()); // dette var lambda setninger der blev anbefalet automatisk i intellij. de virker
        råData.removeIf(s -> s.contains("position"));
        råData.removeIf(s -> s.contains("serial killer name"));
        råData.removeIf(s -> s.contains("profile image"));
        råData.removeIf(s -> s.contains("Template:Serial Killer Infobox/doc"));

        return råData; // vi ønsker at returnere denne data
    }

    public static void itererFodselsdager(Map<String, MorderDatapunkt> map) {
        for (String line : CSVReader.råDataFøds) {
            String[] deler = line.split(";;");
            if (deler.length == 2) {
                String navn = deler[0].trim();
                String dato = deler[1].trim();
                String ddmm = dato.substring(0, 5);
                ddmm = ddmm.replace("/", ""); // for at fodre fødselsdato til "TilStjernetegn" skal vi kun have to siffer til dag, og to til maned

                int mm = Integer.parseInt(ddmm.substring(0, 3));
                int dd = Integer.parseInt(ddmm.substring(2, 4));

                addToMap(map,navn, null, null, ddmm);
            }
        }
    }

    public static void tilskrivStjernetegnTilAlle() {

        for (MorderDatapunkt m : alleMordereMap.values()) {
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
    // Motivation:
    // Ikke sat endnu = -1
    // Visionary = 0
    // Mission Oriented = 1
    // Hedonistic = 2
    // Power-Control = 3

    // Type:
    // Ikke sat endnu = -1
    // organiseret = 0
    // ikke-organiseret = 1
    // mixed = 2

    // Dette er mappede navn for alle stjernetegn fra 0 til 11 (svarer til TilStjernetegn1-classen)
    private static final String[] stjernetegnString = { // dette er for outputtets skyld
            "Vædder", "Tyr", "Tvilling", "Krebs", "Løve", "Jomfru",
            "Vægt", "Skorpion", "Skytte", "Stenbuk", "Vandmand", "Fisk"
    };

    public static List<Set<Integer>> transactionBuilder() { // her laves transactions ved brug af alleMordereMap i WebScraper
        // For at det skal virke skal man køre WebScrape først, så den har data.
        if (alleMordereMap == null || alleMordereMap.isEmpty()) {
            if (alleMordereMap.isEmpty()) {
                loadData();
            }
        }
        tilskrivStjernetegnTilAlle();// Vi tilskriver stjernetegn først

        List<Set<Integer>> txs = new ArrayList<>();
        for (MorderDatapunkt m : alleMordereMap.values()) {
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

    // med tanke på single responsibility principle flyttet vi denne funktion over i en egen metode, istedenfor i loadData.
    // Vi vil konvertere ArrayList<alleMordere> til en List<List<Integer>> for at få mindre tematik over i selve apriori.
    public static List<List<Integer>> convertToBinary(List<Set<Integer>> txs) {
        int alleItems =19;
        List<List<Integer>> result = new ArrayList<>();

        for (MorderDatapunkt m : alleMordereMap.values()) {
            List<Integer> vector = new ArrayList<>(Collections.nCopies(alleItems, 0));// kapasiteten til vektoren er sat til 21

            Integer s = m.getStjernetegn();

            // for stjernetegn, motiv og type vil vi sætte vector-værdier hvis de ikke er 0, og indenfor range af det de skal være.
            if (s!=0 && s >= 0 && s< 12) {
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

    static void main() {
        loadData();
    }

    public static void loadData() { // pleide å være main
        CSVReader leser = new CSVReader();
        leser.run();

        System.out.println("------------------Visionary motiver------------------");
        ArrayList<String> Visionkiller = new ArrayList<>(); // vi laver en arraylist som navnerne bliver addet til.
        ArrayList<String> råV1 = CSVReader.visionaryData; // vi importerer den rå data til en arraylist
        ArrayList<String> råV = filter(råV1);
        for(String line : råV) {
            String visionaryNavn = line;
            String keyNavn = MorderDatapunkt.normaliserNavn(line);

            if(!Visionkiller.contains(keyNavn)) {
                Visionkiller.add(visionaryNavn);
                addToMap(alleMordereMap, visionaryNavn, 0, null, null);
            }
        }
        System.out.println(Visionkiller);

        System.out.println("--------------------Mission-oriented-------------------------");
        ArrayList<String> Missionkiller = new ArrayList<>();
        ArrayList<String> råM1 = CSVReader.missionData;
        ArrayList<String> råM = filter(råM1);
        for(String line : råM) {
            String missionNavn =line;
            String keyNavn = MorderDatapunkt.normaliserNavn(line);

            if(!Missionkiller.contains(keyNavn)) {
                Missionkiller.add(missionNavn);
                addToMap(alleMordereMap, missionNavn, 1, null, null);
            }
        }
        System.out.println(Missionkiller);

        System.out.println("--------------------Hedonistiske motiver-------------------------");
        ArrayList<String> Hedokiller = new ArrayList<>();
        ArrayList<String> råH1 = CSVReader.hedonisticData;
        ArrayList<String> råH = filter(råH1);

        for(String line : råH) {
            String HedoNavn = line;
            String keyNavn = MorderDatapunkt.normaliserNavn(line);
            if(!Hedokiller.contains(keyNavn)) {
                Hedokiller.add(HedoNavn);
                addToMap(alleMordereMap, HedoNavn, 2, null, null);
            }
        }
        System.out.println(Hedokiller);

        System.out.println("--------------------Power-control motiver-------------------------");
        ArrayList<String> Powerkiller = new ArrayList<>();
        ArrayList<String> råP1 = CSVReader.powerData;
        ArrayList<String> råP = filter(råP1);

        for(String line : råP) {
            String PowerNavn = line;
            String keyNavn = MorderDatapunkt.normaliserNavn(line);

            if(!Powerkiller.contains(keyNavn)) {
                Powerkiller.add(PowerNavn);
                addToMap(alleMordereMap, PowerNavn, 3, null, null);
            }
        }
        System.out.println(Powerkiller);

        System.out.println("--------------------Organiserede typer-------------------------");
        ArrayList<String> orgkiller = new ArrayList<>();
        ArrayList<String> råO1 = CSVReader.organizedData;
        ArrayList<String> råO = filter(råO1);

        for(String line : råO) {
            String orgNavn = line;
            String keyNavn = MorderDatapunkt.normaliserNavn(line);
            if(!orgkiller.contains(keyNavn)) {
                orgkiller.add(orgNavn);
                addToMap(alleMordereMap, orgNavn, null, 0, null);
            }
        }
        System.out.println(orgkiller);

        System.out.println("--------------------Uorganiserede typer-------------------------");
        ArrayList<String> unkiller = new ArrayList<>();
        ArrayList<String> råU1 = CSVReader.disorganizedData;
        ArrayList<String> råU = filter(råU1);

        for(String line : råU) {
            String uNavn = line;
            String keyNavn = MorderDatapunkt.normaliserNavn(line);

            if(!unkiller.contains(keyNavn)) {
                unkiller.add(uNavn);
                addToMap(alleMordereMap, uNavn, null, 1, null);
            }
        }
        System.out.println(unkiller);

        System.out.println("--------------------mixet typer-------------------------");
        ArrayList<String> mixedkiller = new ArrayList<>();
        ArrayList<String> råMi1 = CSVReader.mixedData;
        ArrayList<String> råMi = filter(råMi1);

        for(String line : råMi) {
            String mixedNavn = line;
            String keyNavn = MorderDatapunkt.normaliserNavn(line);
            if(!mixedkiller.contains(keyNavn)) {
                mixedkiller.add(mixedNavn);
                addToMap(alleMordereMap, mixedNavn, null, 2, null);
            }
        }
        System.out.println(mixedkiller);

        itererFodselsdager(alleMordereMap);
        ArrayList<MorderDatapunkt> alleMordereFinal = new ArrayList<>();
        ArrayList<MorderDatapunkt> alleMordere = new ArrayList<>(alleMordereMap.values());
        int teller = 0;
        alleMordere.sort((p1, p2) -> p1.navn.compareTo(p2.navn)); // alfabetisk sortering. (https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/List.html#sort(java.util.Comparator))
        for (MorderDatapunkt morder : alleMordere) {
            if (!morder.motiv.equals(-1) || !morder.fødselsdag.equals("Ikke sat endnu")) {// vi gidder ikke have data der ikke har motiv eller fødselsdag
                /// her genererer vi random motiv til de mordere der mangler, grundet inkonsistent data. REFERER til dette
                // skal vi bruge randomData så?
                // for at få denne apriori til at give noget meningsfyldt data til trods for vores mangel på motiv og type-data
                // vil vi distubruere alle manglende motiv og typer ligeligt ud over de mulige værdier, så resultatet vil svare til en reel tendens hos dataen.
                if (morder.motiv == -1) {
                    int antallMotiv = 4;
                    morder.motiv = teller % antallMotiv;
                    teller++;
                }

                if (morder.type == -1) {  //  husk til rapportskrivning: vores overvejelser med at bruge random vs distribuere lige
                    int antallType = 3;
                    morder.type = teller % antallType;
                    teller++;
                }

               // System.out.println(morder); // vi printer alle datapunkter

                alleMordereFinal.add(morder); // vi adder til endnu en arrayliste, der vil bruge værdier der ikke er -1.
             //   System.out.println(morder.getDag() + " " + morder.getMåned());
            }
        }
        System.out.println(alleMordereFinal.size());
        List<Set<Integer>> txs = WebScrape.transactionBuilder();
        System.out.println("Tx count = " + txs.size());
        List<List<Integer>> convert = WebScrape.convertToBinary(txs);

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
            System.out.println(f + " -> " + String.format("%.2f", apriori.support(f)));
        }
        System.out.println("F2:"); // vi printer second pass med support
        for (Set<Integer> f : f2) {

            /// todo: lav en if-sætning som ikke lader de samme to items vises to gange f eks [16, 15] og [15, 16]
            System.out.println(f + " -> " + String.format("%.2f", apriori.support(f)));
        }
        ///  dette output består ikke af 3 forskellige elementer, hvorfor?
        System.out.println("F3:"); // vi printer third pass med support
        for (Set<Integer> f : f3) {
            System.out.println(f + " -> " + String.format("%.2f", apriori.support(f)));
        }
        /// todo vi må forstærke stjernetegn i dataen, da de ikke når over minsup nogen gang.
    }
}

