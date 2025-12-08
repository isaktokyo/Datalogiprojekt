import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class DataScrape {

    public static class MorderDatapunkt {
        String displayNavn;
        String motiv = "Ikke sat endnu";
        String type = "Ikke sat endnu";
        String fødselsdag = "Ikke sat endnu";

        public MorderDatapunkt(String displayNavn) {
            this.displayNavn = displayNavn;
        }
        public String toString() {
            return  displayNavn +" ¶ "+ motiv +" ¶ "+  type +" ¶ "+ fødselsdag;
        }
        public static String normaliserNavn(String navn){
            if(navn == null) return "";
            String vasket = navn;

            // 1. Fjern alt i parentes: "Ted Bundy (Serial Killer)" -> "Ted Bundy "
            vasket = vasket.replaceAll("\\(.*?\\)", "");

            // 2. Erstatt "harde mellomrom" (\u00A0) med vanlig mellomrom
            vasket = vasket.replace('\u00A0', ' '); // dette blev nødvendig da der var usynlige mellemrum i scrapen som gjorde at vi ikke fik resultater

            // 3. Standardiser bindestreker (noen bruker lang tankestrek –, andre kort -)
            vasket = vasket.replaceAll("[–—]", "-");

            // efter flere runder med fejl i output, fant vi ud af at navnerne er skrevet forskelligt på fødselsdag-siden og på motiv/type siderne.
            // Derfor må vi flytte efternavn i all-caps bagerst i navnet.
            String[] navnDeler = vasket.split("\\s+");
            String first = navnDeler[0]; // first er værdien af det første tegn i navnet
            if(first.equals(first.toUpperCase())) { // vi checker hvis det første tegn er stor bogstav.
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < navnDeler.length; i++) { // vi itererer gennem navne
                    sb.append(navnDeler[i]); // append er en StringBuilder metode der kan gøre ændringer til en String. her flyttes de første ord bagerst.
                }
                sb.append(" ").append(first); // vi sætter et mellemrum ind, før vi
                vasket = sb.toString().trim();
            }

            return vasket.trim().toLowerCase(); // vi returnerer altså et vasket navn, og bruger displayName kun til når vi skal printe.
        }
    }
    public static void addToMap(Map<String, MorderDatapunkt> map, String navn, String motiv, String type, String fødselsdag) {
        String normNavn = MorderDatapunkt.normaliserNavn(navn);

        MorderDatapunkt person = map.get(normNavn);
        if (person == null) {// vi oprætter en ny person hvis den ikke findes
            person = new MorderDatapunkt(navn.trim());
            map.put(normNavn, person);
        }
        // Her opdaterer vi feltene statisk, hvis deres værdi ikke er null.
        if (motiv != null) person.motiv = motiv;
        if (type != null) person.type = type;
        if (fødselsdag != null) person.fødselsdag = fødselsdag;

        // debug-hjælper
        //System.out.println("addToMap: key=" + normNavn + " navn='" + navn + "' motiv=" + motiv + " type=" + type + " fødsels=" + fødselsdag);
    }

    public static class FodselsdagScrape {
// for at optimere våres logikk vil vi adde en morder til vores database hvis der ikke er nogen med samme navn.
        // Hvis navnet skulle være det samme, kan man checke om deres fødselsdag er forskellige.
        public static ArrayList<MorderDatapunkt> getFodselsdag(String url, String cssQuery, String månedNavn){

           // denne metode har tre hovedopgaver: 1 - at finde fødselsdager 2 - at normalisere dataen 3 - at koble fødselsdage op mod navn så vi får datapunkter.

            String dag = null;
            System.out.println("------------------"+månedNavn+"------------------");
            ArrayList<MorderDatapunkt> mordere = new ArrayList();
            String månedNavnCheck= url.substring(url.lastIndexOf("/") + 1); // dette giver for eksempel januar.

            try {
                Document document = Jsoup.connect(url).userAgent("Mozilla/5").get();
                Elements killer = document.select(cssQuery);


                for (Element fodsel : killer) {// her sker en normalisering og iterering igennem fødselsdatoerne der skal scrapes.

                    String tekst = fodsel.text().replaceAll("^[\\W_]+", "").trim(); // regex: vi trimmer væk alle tægn som ikke er ord, siden der er en "dot" på nettsiden før måneden

                    if (tekst.contains("born")) {
                        dag = "";
                        String[] oppdeltOrd = tekst.split(" ");

                        // checker de 3 første ord, med mindre der er færre end 3 ord, så checker vi disse.
                        for (int i = 0; i < Math.min(oppdeltOrd.length, 3); i++) {
                            String rensetDag = oppdeltOrd[i].replaceAll("[^0-9]", "");//regex som hjælper med at finde
                            if (!rensetDag.isEmpty() && rensetDag.length() <= 2) {
                                dag = rensetDag;
                                break;
                            }
                        }
                    }
                    // i den følgende delen vil vi identificere navn så det kan kobles op mod fødselsdag
                    Elements n = fodsel.select("a");
                    String navn = null;
                    // nu må vi køre igennem alle lenker i linjen for at identificere navnet.
                    for (Element navne : n) {
                        String title = navne.attr("title"); // dette bliver navnet fra css-koden
                        String navneTekst = navne.text();

                        if (title.contains(månedNavnCheck)) { //efter første version blev datoer forvekslet for navn, så dette er et filter som hopper over hvis man når til navn
                            continue; // for hver continue udelader vi forskellige elementer, så vi ender op med navne.
                        }
                        if (navneTekst.matches("\\d+")) {// vi bruger regEx, hvilket f. eks lader os søge igennem dataen efter tal. /d er commando for digits. (geeks4geeks)
                            continue;
                        }
                        navn = title;
                        break; // vi har fundet våres navn. loopet er færdig
                    }

                    // denne del lagrer det vi fram til nu har fundet, som del af en persons fødselsdag
                    if (navn != null && dag != null) {

                        MorderDatapunkt person = new MorderDatapunkt(navn);
                        person.fødselsdag = dag + " " + månedNavn;
                        mordere.add(person);
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            return mordere;
        }
    }

    static void main() throws IOException {
//dette er en data-scraping kode der henter 4 forskellige typer motiverede mordere,
// og deres type.
        // den samme koden gentager sig mange gange. jeg kommenterer derfor kun den første.
        // Vi kunne eventuelt lave en metode der tager navn, url og css:query.
        // et hashmap med nøkkel (String) og objekttype (MorderDatapunkt).
        Map<String, MorderDatapunkt> alleMordereMap = new HashMap<>();
        System.out.println("------------------Visionary motives------------------");
        ArrayList<String> Visionkiller = new ArrayList<>(); // vi laver en arraylist som navnerne bliver addet til.
        String VisionaryUrl = "https://skdb.fandom.com/wiki/Category:Visionary_motivated_Serial_Killers";
        try {
            Document document = Jsoup.connect(VisionaryUrl).userAgent("Mozilla/5.0").get(); // vi connecter til nettsiden
            Elements killer = document.select(".category-page__member-link, li.category-page__member a"); // vi vælger alle elementer med css-koden category-page__member-link
            System.out.println("Page Title: " + document.title());

            for (Element vision : killer) { // elementet vision itererer gennem killer, hvilket er alle navne på nettsiden.
                String VisionName = vision.attr("title"); // vi laver strings af titlerne på morderne. css-koden kalder dem title, så det gør vi ogs
                // efter jeg lavede dette, så jeg at der kom nogle duplikater. jeg adder derfor kun hvis den ikke findes allerede.
                if (!Visionkiller.contains(VisionName)) {
                    Visionkiller.add(VisionName);
                    addToMap(alleMordereMap, VisionName, "Visionary", "Ukendt", null);
                }
            }
            // efter at den ikke klaret at hente navn via den første cssQuery, kan vi prøve med denne.
            Elements figcaptions = document.select("figcaption.category-page__trending-page-title");
            for (Element f : figcaptions) {
                String txt = f.text().trim();
                if (!txt.isEmpty()) {
                    addToMap(alleMordereMap, txt, "Visionary", "Ukendt", null);
                }
            }
            System.out.println(Visionkiller); // vi printer det.

        } catch (IOException e) {e.printStackTrace();}

        System.out.println("------------------Mission-oriented------------------");
        ArrayList<String> Missionkiller = new ArrayList<>();
        String MissionUrl = "https://skdb.fandom.com/wiki/Category:Mission-oriented_Serial_Killers";
        try {
            Document document = Jsoup.connect(MissionUrl).userAgent("Mozilla/5.0").get(); // user agent er noget vi bruger for at få mere konsistent adgang på dataen
            Elements killer = document.select(".category-page__member-link, li.category-page__member a");

            for (Element mission : killer) {
                String MissionName = mission.attr("title");
                if (!Missionkiller.contains(MissionName)) {
                    Missionkiller.add(MissionName);
                    addToMap(alleMordereMap, MissionName, "Mission-Oriented", "Ukendt", null);
                }
            }
            // følgende del er bare kopieret.
            Elements figcaptions = document.select("figcaption.category-page__trending-page-title");
            for (Element f : figcaptions) {
                String txt = f.text().trim();
                if (!txt.isEmpty()) {
                    addToMap(alleMordereMap, txt, "Mission-oriented", "Ukendt", null);
                }
            }
            System.out.println(Missionkiller);

        } catch (IOException e) {e.printStackTrace();}

        System.out.println("------------------Hedonistic------------------");

        ArrayList<String> Hedokiller = new ArrayList<>();
        String HedoUrl = "https://skdb.fandom.com/wiki/Category:Hedonistic_Serial_Killers";
        try {
            Document document = Jsoup.connect(HedoUrl).userAgent("Mozilla/5").get();
            Elements killer = document.select(".category-page__member-link, li.category-page__member a");

            for (Element Hedo : killer) {
                String HedoName = Hedo.attr("title");
                if (!Hedokiller.contains(HedoName)) {
                    Hedokiller.add(HedoName);
                    addToMap(alleMordereMap, HedoName, "Hedonistic", "Ukendt", null);
                }
            }
            Elements figcaptions = document.select("figcaption.category-page__trending-page-title");
            for (Element f : figcaptions) {
                String txt = f.text().trim();
                if (!txt.isEmpty()) {
                    addToMap(alleMordereMap, txt, "Hedonistic", "Ukendt", null);
                }
            }
            System.out.println(Hedokiller);

        } catch (IOException e) {e.printStackTrace();}

        System.out.println("------------------Power Control------------------");

        ArrayList<String> Powerkiller = new ArrayList<>();
        String PowerUrl = "https://skdb.fandom.com/wiki/Category:Power_and_Control_motivated_Serial_Killers";
        try {
            Document document = Jsoup.connect(PowerUrl)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(15000)
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept", "text/html")
                    .get();


            Elements killer = document.select(".category-page__member-link, li.category-page__member a");

            for (Element Power : killer) {
                String PowerName = Power.attr("title");
                if (!Powerkiller.contains(PowerName)) {
                    Powerkiller.add(PowerName);
                    addToMap(alleMordereMap, PowerName, "Power Control", "Ukendt", null);
                }
            }
            // efter at den ikke klaret at hente navn via den første cssQuery, kan vi prøve med denne.
            Elements figcaptions = document.select("figcaption.category-page__trending-page-title");
            for (Element f : figcaptions) {
                String txt = f.text().trim();
                if (!txt.isEmpty()) {
                    addToMap(alleMordereMap, txt, "Power-control", "Ukendt", null);
                }
            }
            System.out.println(Powerkiller);

        } catch (IOException e) {e.printStackTrace();}

        ArrayList<String> allMotives = new ArrayList<>();allMotives.addAll(Visionkiller);allMotives.addAll(Missionkiller);allMotives.addAll(Hedokiller);allMotives.addAll(Powerkiller);

        System.out.println("------------------Organized------------------");
        ArrayList<String> Orgkiller = new ArrayList<>();
        String OrgUrl = "https://skdb.fandom.com/wiki/Category:Organized_Serial_Killers";
        try {
            Document document = Jsoup.connect(OrgUrl).userAgent("Mozilla/5").get();
            Elements killer = document.select(".category-page__member-link, li.category-page__member a");
            for (Element org : killer) {
                String orgName = org.attr("title");
                if (!Orgkiller.contains(orgName)&& allMotives.contains(orgName)) {

                    Orgkiller.add(orgName);
                    addToMap(alleMordereMap, orgName, null, "Organized", null);
                }
            }
            // efter at den ikke klaret at hente navn via den første cssQuery, kan vi prøve med denne.
            Elements figcaptions = document.select("figcaption.category-page__trending-page-title");
            for (Element f : figcaptions) {
                String txt = f.text().trim();
                if (!txt.isEmpty()) {
                    addToMap(alleMordereMap, txt, null, "Organized", null);
                }
            }
            System.out.println(Orgkiller);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("------------------Disorganized------------------");
        ArrayList<String> NonOrgkiller = new ArrayList<>();
        String NonOrgUrl = "https://skdb.fandom.com/wiki/Category:Disorganized_Serial_Killers";
        try {
            Document document = Jsoup.connect(NonOrgUrl).userAgent("Mozilla/5").get();
            Elements killer = document.select(".category-page__member-link, li.category-page__member a");

            for (Element nonorg : killer) {
                String nonorgName = nonorg.attr("title");

                if (!NonOrgkiller.contains(nonorgName)) {
                    NonOrgkiller.add(nonorgName);
                    addToMap(alleMordereMap, nonorgName, null, "Disorganized", null);
                }
                // efter at den ikke klaret at hente navn via den første cssQuery, kan vi prøve med denne.
                Elements figcaptions = document.select("figcaption.category-page__trending-page-title");
                for (Element f : figcaptions) {
                    String txt = f.text().trim();
                    if (!txt.isEmpty()) {
                        addToMap(alleMordereMap, txt, null, "Unorganized", null);
                    }
                }
            }
            System.out.println(NonOrgkiller);

        } catch (IOException e) {e.printStackTrace();}

        System.out.println("------------------mixed------------------"); // skal vi fjerne denne?
        ArrayList<String> mixedkiller = new ArrayList<>();
        String mixedUrl = "https://skdb.fandom.com/wiki/Category:Mixed_Serial_Killers";
        try {
            Document document = Jsoup.connect(mixedUrl).userAgent("Mozilla/5").get();
            Elements killer = document.select(".category-page__member-link, li.category-page__member a");

            for (Element mixedorg : killer) {
                String mixedName = mixedorg.attr("title");
                if (!mixedkiller.contains(mixedName)) {
                    mixedkiller.add(mixedName);
                    addToMap(alleMordereMap, mixedName, null, "Mixed", null);
                }
            }
            // efter at den ikke klaret at hente navn via den første cssQuery, kan vi prøve med denne.
            Elements figcaptions = document.select("figcaption.category-page__trending-page-title");
            for (Element f : figcaptions) {
                String txt = f.text().trim();
                if (!txt.isEmpty()) {
                    addToMap(alleMordereMap, txt, null, "mixed", null);
                }
            }
            System.out.println(mixedkiller);

        } catch (IOException e) {e.printStackTrace();}


        // her vil vi printe ud alle fødselsdage måned for måned. Det bliver for meget kode at gøre det manuelt,
        // så vi har lavet en metode som kan fyldes ind med månederne i url-linket.

        String cssQuery = "div.mw-parser-output ul li";
        String[] maanederEng = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        for (int i = 0; i < 12; i++) {
            String url = "https://skdb.fandom.com/wiki/" + maanederEng[i];

            //dette er en midlertidig liste vi henter
            ArrayList<MorderDatapunkt> fodselsListe = FodselsdagScrape.getFodselsdag(url, cssQuery, maanederEng[i]);

            if(fodselsListe!=null){
            for (MorderDatapunkt morder : fodselsListe) {
                // her kalles addToMap for hver person i fodselsListe som ikke findes
                // Hvis navnet findes fra før med motiv eller type, oppdateres navnet, og hvis ikke laves der en ny.
                addToMap(alleMordereMap, morder.displayNavn, null, null, morder.fødselsdag);
            }
            }
        }
    System.out.println("Her er alle færdige datapunkter: ");
    ArrayList<MorderDatapunkt> alleMordere = new ArrayList<>(alleMordereMap.values());
    alleMordere.sort((p1, p2) -> p1.displayNavn.compareTo(p2.displayNavn));
        for(MorderDatapunkt morder : alleMordere){
            if(!morder.motiv.equals("Ikke sat endnu") || !morder.fødselsdag.equals("Ikke sat endnu")) { // vi gidder ikke have data der ikke har motiv eller fødselsdag
                System.out.println(morder); // vi printer alle datapunkter
            }
        }
    }
}