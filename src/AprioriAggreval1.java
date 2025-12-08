 import java.util.*;
    public class AprioriAggreval1 {

        public static class items { // vi laver objektet items (attributter) + deres sub-attributter
            String name;

            public items(String name) {
                this.name = name;
            }

            // hvis et givent navn fremgår to gang, så er de den samme item
            @Override
            public boolean equals(Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                items items = (items) object;
                return Objects.equals(name, items.name);
            }

            @Override
            public int hashCode() { // det er kun hashcode der virker, men ved ikke hvorfor/tror det er pga. equals på linje 14
                return Objects.hash(name);
            }

            @Override
            public String toString() {
                return name;
            }
        }

        // liste af items/attributter for hver morderprofil / vores transaktioner
        private Set<Set<items>> transactions;
        // minimum support threshold
        private double minsup;
        // holder på vores mest hyppige sets
        private Set<Set<items>> frequentItemsets = new HashSet<>();

        public AprioriAggreval1 (Set<Set<items>> transactions, double minsup){
            this.transactions = transactions;
            this.minsup = minsup;
        }

        // behold en ekstra constructor hvis noen kaller med kun transactions (som i noen tidligere versjoner)
        public AprioriAggreval1(Set<Set<items>> transactions){
            this.transactions = transactions;
            this.minsup = 0.05; // default
        }

        public Set<Set<items>> getAggreval(){
            return transactions;
        }

        // ligesom isaks forrige men rettet til
        public double support(Set<items> X){ // data mining bog side 98
            int count = 0;
            if (transactions == null || transactions.isEmpty()) return 0.0;
            for(Set<items> t : transactions) {
                if(t.containsAll(X)) {
                    count++;
                }
            }
            return (double) count / transactions.size(); // her bruges support som fraktion (altså som procentdel og ikke heltal,
            // men det er bare ud fra hvad jeg kan finde om ligningen. tror godt vi kan lave det om så det er heltal.)
        }

    /* isaks:
    public static double support(Set<items> X, Set<items> Y){ // data mining bog side 98
        double minsup = 0.05; // dette bliver treshhold for vores første iteration (filter)
        double supportofXtoY = 0;
        if(X.isEmpty()||Y.isEmpty()){return 0;}
        if(supportofXtoY<minsup){
            return 0;
        }else{
            return supportofXtoY;
        }
     */
        private boolean diffAttribute(items a, items b){
            String A = a.name.split("=")[0];
            String B = b.name.split("=")[0];
            return !A.equals(B);
        }
        // downward closure ifølge bogen
        private boolean allSubsetsFrequent(Set<items> candidate, Set<Set<items>> Fk) {
            for (items removed : candidate) {
                Set<items> subset = new HashSet<>(candidate);
                subset.remove(removed);

                if (!Fk.contains(subset)) {
                    return false;
                }
            }
            return true;
        }
        // selve apriori metoden
        public Set<Set<items>> Apriori() {
            //Support counting — beregne hvor ofte kandidater forekommer i transaksjonene.

            //Prune (beskære) — fjerne kandidater som ikke kan være frekvente fordi nogle av delmengderne ikke er frekvente

            //Beholde kun de som oppfylder minsup, og lagre dem som Fk+1.

            //Øge k og gentage til ingen flere frekvente sæt findes.

            // først skal alle itemsets lige findes
            Set<items> C1 = new HashSet<>();
            for (Set<items> t : transactions) {
                C1.addAll(t);
            }
            // first pass (finder de hyppigste set)
            Set<Set<items>> F1 = new HashSet<>();
            for (items i : C1) {
                Set<items> single = Collections.singleton(i); // singleton er en instans af en klasse -
                // overvejer om vi skal bruge enum singeltons
                if (support(single) >= minsup) {
                    F1.add(single);
                }
            }
            // Gem F1
            frequentItemsets.addAll(F1);
            return frequentItemsets;
        }

    /*
    public static double confidence(Integer supportx, Integer supporty){
        double confidenceOfXtoY = (supportx+supporty)/supportx;
        double minconf =0.2; // dette bliver threshold for vores andre pass.
        if(confidenceOfXtoY<minconf) {
            return 0;
        }else {
            return confidenceOfXtoY;
        }
    }
    public static double lift(Integer supportx, Integer supporty){double placeholder = 0;
        return placeholder;}

    public static void Apriori (Set<items>transactions, double minsup) {
        for (int k = 1; k < transactions.size(); k++) {
            minsup = 0.05;
            Set<Set<items>> frequentItemsets = new HashSet<>();
            if(frequentItemsets.size() > 1){
                for (int j = 0; j < frequentItemsets.size(); j++) {
                    if (transactions.size() > minsup) {
                        frequentItemsets.add(transactions);
                        int Tsize = frequentItemsets.size();
                    }
                }
            }
        }
    }} */
        // Dette er mappede navn for alle stjernetegn fra 0 til 11 (svarer til TilStjernetegn1-classen)
        private static final String[] stjernetegnString = { // dette er for outputtets skyld
                "Vædder", "Tyr", "Tvilling", "Krebs", "Løve", "Jomfru",
                "Vægt", "Skorpion", "Skytte", "Stenbuk", "Vandmand", "Fisk"
        };

        // i denne metode iterer vi igennem WebScrape.alleMordereMap, og tildeler stjernetegn i form af Integer.
        // vores data mangler nogen ganger fødselsdag til morderen, og har kun måned. Vi har derfor valgt at sætte dag til 15.
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
        public static Set<Set<items>> transactionBuilder() { // her laves transactions ved brug af alleMordereMap i WebScraper
            // For at det skal virke skal man køre WebScrape først, så den har data.
            if (WebScrape.alleMordereMap == null || WebScrape.alleMordereMap.isEmpty()) {
                try { WebScrape.main(new String[0]); } catch (Throwable ignored) {}
            }

            // Tilskriv stjernetegn først
            tilskrivStjernetegnTilAlle();

            Set<Set<items>> txs = new HashSet<>();
            for (WebScrape.MorderDatapunkt m : WebScrape.alleMordereMap.values()) {
                Set<items> t = new HashSet<>(); // vi initierer et tomt itemset som fyldes med motiv, type og stjernetegn.

                // Motiv
                if (m.motiv != null && m.motiv >= 0) { // disse checks måtte til, fordi det er inkonsekvent data.
                    t.add(new items("motiv=" + m.motiv));
                } else {
                    t.add(new items("motiv=ukendt")); // vi giver værdien ukendt i tilfælde vi ikke finder en anden værdi
                }

                // Type (organiseret/uan)
                if (m.type != null && m.type >= 0) {
                    t.add(new items("type=" + m.type));
                } else {
                    t.add(new items("type=ukendt"));
                }

                // Stjernetegn (integer)
                Integer z = m.getStjernetegn();
                if (z != null && z >= 0 && z < 12) {
                    t.add(new items("stjernetegn=" + z));
                    // også legg til lesbart navn (valgfritt)
                    t.add(new items("stjernetegn_navn=" + stjernetegnString[z]));
                } else {
                    t.add(new items("stjernetegn=ukendt"));
                }

                // Måned (hvis tilgjængelig)
                Integer mm = m.getMåned();
                if (mm != null && mm > 0 && mm <= 12) {
                    t.add(new items("maaned=" + mm));
                } else {
                    t.add(new items("maaned=ukendt"));
                }

                // Dag kendt/ukendt
                Integer dd = m.getDag();
                if (dd != null && dd > 0) {
                    t.add(new items("dag_kendt=true"));
                } else {
                    t.add(new items("dag_kendt=false"));
                }txs.add(t);
            }return txs;
        }

        // dette er en iteration der viser os et bud på en main
        public static void main(String[] args) {
            // vi bruger transactionBuilder til at bygge Byg transaktioner direkte fra WebScrape
            Set<Set<items>> txs = transactionBuilder(); // vi lader txs være et set med itemssets

            // Og oprætter en instans af AprioriAggreval1 med default minsup
            AprioriAggreval1 ag = new AprioriAggreval1(txs, 0.05);

            if (txs == null) {
                System.out.println("Byggede transaktioner: 0");
            } else {
                System.out.println("Byggede transaktioner: " + txs.size());
            }
            int i = 0;
            for (Set<items> t : txs) {
                System.out.println("Transaktion nr: " + (++i) + " : " + t);
                if (i >= 20) break;
            }// denne løsning har vidst sig at ikke være særlig effektiv. Lige nu er det mere en demo, så derfor begrænser vi os til 20 transaktioner.

            // Kører en enkel Apriori-pass (kan være dyrt hvis mange transaksjoner)
            Set<Set<items>> frequent = ag.Apriori();
            System.out.println("\nFundet " + frequent.size() + " hyppige itemsets (minsup=" + ag.minsup + "):");
            int idx = 0;
            for (Set<items> fi : frequent) {
                System.out.println("F" + (++idx) + ": " + fi + " (support=" + ag.support(fi) + ")");
                if (idx >= 50) break; // vi sætter en grænse for apriori-passet her, da det er dyrt at køre igennem al dataen
            }

            // Print en enkel oppsummering per morder: navn, dag, måned, stjernetegn integer og navn
            System.out.println("\nOppsummering av stjernetegn per morder:");
            for (WebScrape.MorderDatapunkt m : WebScrape.alleMordereMap.values()) { // vi iterer igennem alle morderdatapunkter -
                Integer z = m.getStjernetegn(); // og initierer z, som representerer stjernetegn for hvert datapunkt.
                String zname;
                if (z != null && z >= 0 && z < stjernetegnString.length) { // hvis z befinder sig indenfor scopet (vi blev nødt til at blive meget omhyggelige)
                    zname = stjernetegnString[z];
                } else {
                    zname = "Ukendt";
                }
                String dagStr; // vi checker hvis dag-størrelse har en ægte værdi (over 0)
                if (m.dag > 0) {dagStr = String.valueOf(m.dag);
                } else {dagStr = "ukendt";
                }
                String maanedStr; // vi checker hvis maaned har værdi over 0
                if (m.maaned > 0) {maanedStr = String.valueOf(m.maaned);
                } else {maanedStr = "ukendt";
                }
                // vi brugte format for at præsentere et midlertidig output. https://www.w3schools.com/java/ref_string_format.asp
                System.out.println(String.format("%s -> dag=%s maaned=%s stjernetegn=%s", m.navn, dagStr, maanedStr, zname));
            }
            // med vores nuværende kode, har vi ikke printet nogle apriori-passes.
            // vi gennemfører blot et first pass. Det er også en inkonsistent data-kilde.
        }
    }

