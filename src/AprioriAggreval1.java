 import java.util.*;
    public class AprioriAggreval1 {

    static Vector<String> new_candidates;
    static Vector<Double> supports;
    private static double minsup;

    // skal item hede item ?
    public static class item { // vi laver objektet item (attributter) + deres sub-attributter
        private final String name;

        public item(String name) {
            this.name = name;
        }

        // hvis et givent objekt fremgår to gang, så er de den samme item
        // TODO vi kan evt. slette den her snart hahahah hehehehe
        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            item item = (item) object;
            return Objects.equals(name, item.name);
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

    // liste af item/attributter for hver morderprofil / vores transaktioner
    private Set<Set<item>> transactions;
    // holder på vores mest hyppige sets
    private Set<Set<item>> frequentItemsets = new HashSet<>();

    public AprioriAggreval1(Set<Set<item>> transactions, double minsup) {
        this.transactions = transactions;
        this.minsup = minsup;
    }

    // behold en ekstra constructor hvis noen kaller med kun transactions (som i noen tidligere versjoner)
    public AprioriAggreval1(Set<Set<item>> transactions) {
        this.transactions = transactions;
        this.minsup = 0.05; // default
    }

    public Set<Set<item>> getAggreval() {
        return transactions;
    }

    // ligesom isaks forrige men rettet til
    public double support(Set<item> X) { // data mining bog side 98
        int count = 0;
        if (transactions == null || transactions.isEmpty()) return 0.0;
        for (Set<item> t : transactions) {
            if (t.containsAll(X)) {
                count++;
            }
        }
        return (double) count / transactions.size(); // her bruges support som fraktion (altså som procentdel og ikke heltal,
        // men det er bare ud fra hvad jeg kan finde om ligningen. tror godt vi kan lave det om så det er heltal.)
    }

    // downward closure ifølge bogen Behold
    private boolean allSubsetsFrequent(Set<item> candidate, Set<Set<item>> Fk) {
        for (item removed : candidate) {
            Set<item> subset = new HashSet<>(candidate);
            subset.remove(removed);

            if (!Fk.contains(subset)) {
                return false;
            }
        }
        return true;
    }

    // selve apriori metoden
    public Set<Set<item>> Apriori() {
        //Support counting — beregne hvor ofte kandidater forekommer i transaksjonene.

            //Prune (beskære) — fjerne kandidater som ikke kan være frekvente fordi nogle av delmengderne ikke er frekvente

            //Beholde kun de som oppfylder minsup, og lagre dem som Fk+1.

            //Øge k og gentage til ingen flere frekvente sæt findes.

        // først skal alle itemsets lige findes

        ///  Vi beholder logikken nedenunder:
        //Variablen F1 kan blive Eller Set<Vector<String>> F1 / Set<candidates>.

        List<List<String>> transactionsAsStrings = new ArrayList<>();
/*
        for (Set<item> t : transactions) {
            List<String> itemsAsString = new ArrayList<>();
            for (item items : t) {

                for (Set<item> t : transactions) {
                    List<String> itemsAsString = new ArrayList<>();
                    for (item item : t) {
                        itemsAsStrings.add(item.toString());
                    }
                    transactionsAsStrings.add(itemsAsString);
                }
//laver alle C1 (de unikke items)
                Set<String> allUniqueItems = new HashSet<>();
                for (List<List<String>> t

                Set<item> C1 = new HashSet<>();

                for (Set<item> t : transactions) {
                    C1.addAll(t);
                }

                // first pass (finder de hyppigste set)
                Set<Vector<String>> F1 = new HashSet<>();
                for (item i : C1) {
                    Vector<item>

                            single = Collections.singleton(i); // singleton er en instans af en klasse -
                    // overvejer om vi skal bruge enum singeltons
                    if (support(single) >= minsup) {
                        F1.add(single);
                    }
                }
                // Gem F1
                frequentItemsets.addAll(F1);
                return frequentItemsets;*/
                Set<Set<item>> F = new HashSet<>();
                return F;

            }



            // i denne metode iterer vi igennem WebScrape.alleMordereMap, og tildeler stjernetegn i form af Integer.
            // vores data mangler nogen ganger fødselsdag til morderen, og har kun måned. Vi har derfor valgt at sætte dag til 15.

            // dette er en iteration der viser os et bud på en main
            public static void main (String[]args){
                // vi bruger transactionBuilder til at bygge Byg transaktioner direkte fra WebScrape
                Set<Set<item>> txs = WebScrape.transactionBuilder(); // vi lader txs være et set med itemssets

            // Og oprætter en instans af AprioriAggreval1 med default minsup
            AprioriAggreval1 ag = new AprioriAggreval1(txs, 0.05);

                if (txs == null) {
                    System.out.println("Byggede transaktioner: 0");
                } else {
                    System.out.println("Byggede transaktioner: " + txs.size());
                }
                int i = 0;
                for (Set<item> t : txs) {
                    System.out.println("Transaktion nr: " + (++i) + " : " + t);
                    if (i >= 20) break;
                }// denne løsning har vidst sig at ikke være særlig effektiv. Lige nu er det mere en demo, så derfor begrænser vi os til 20 transaktioner.

                // Kører en enkel Apriori-pass (kan være dyrt hvis mange transaksjoner)
                Set<Set<item>> frequent = ag.Apriori();
                System.out.println("\nFundet " + frequent.size() + " hyppige itemsets (minsup=" + ag.minsup + "):");
                int idx = 0;
                for (Set<item> fi : frequent) {
                    System.out.println("F" + (++idx) + ": " + fi + " (support=" + ag.support(fi) + ")");
                    if (idx >= 50)
                        break; // vi sætter en grænse for apriori-passet her, da det er dyrt at køre igennem al dataen
                }
            /*
            // Print en enkel oppsummering per morder: navn, dag, måned, stjernetegn integer og navn
            System.out.println("\nOppsummering av stjernetegn per morder:");
            for (WebScrape.MorderDatapunkt m : WebScrape.alleMordereMap.values()) { // vi iterer igennem alle morderdatapunkter -
                Integer z = m.getStjernetegn(); // og initierer z, som representerer stjernetegn for hvert datapunkt.
                String zname;
                if (z != null && z >= 0 && z < stjernetegnString.length) { // hvis z befinder sig indenfor scopet (vi blev nødt til at blive meget omhyggelige)
                    zname = stjernetegnString[z];} else {zname = "Ukendt";}
                String dagStr; // vi checker hvis dag-størrelse har en ægte værdi (over 0)
                if (m.dag > 0) {dagStr = String.valueOf(m.dag);} else {dagStr = "ukendt";}
                String maanedStr; // vi checker hvis maaned har værdi over 0
                if (m.maaned > 0) {maanedStr = String.valueOf(m.maaned);} else {maanedStr = "ukendt";}
                // vi brugte format for at præsentere et midlertidig output. https://www.w3schools.com/java/ref_string_format.asp
                System.out.println(String.format("%s -> dag=%s maaned=%s stjernetegn=%s", m.navn, dagStr, maanedStr, zname));
            }
            */
                // med vores nuværende kode, har vi ikke printet nogle apriori-passes.
                // vi gennemfører blot et first pass. Det er også en inkonsistent data-kilde.
            }
        }

