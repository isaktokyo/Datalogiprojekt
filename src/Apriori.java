import java.util.*;

public class Apriori {
  private double minSupport; //det er den min support værdi (0,2= 20% fx,vi kan altid ændre den)

//så sætter vi constructor ind for at gemme værdien
  public Apriori(double minSupport) {
    this.minSupport = minSupport;
  }
  
//så skal vi bruge frequency metoden for at se hvor mange gange stjernetegnene dukker op
  public Map<String, Integer> findFrequentItems(List<String> items) {
    Map<String, Integer> freq = new HashMap<>(); //freq er en hash map, der gemmer datastruktur, her laver vi et tomt

    for (String item : items) {
      freq.put(item, freq.getOrDefault(item, 0) + 1); //beder den om at finde den nuværende tælling for hvert item (eller skriv 0 hvis ikke findes), læg 1 til, og gem 
    }

return freq; //så beder vi den om at returnere resultatet (fisk=4 eller jomfru=2)
  }

//nu skal vi igang med "support", hvis der er 10p og 3 af dem er fisk er der 3/10=0,3=30%

public Map<String, Double> computeSupport(Map<String, Integer> freq, int total) { //her bliver der lavet liste over hvor mange gange hvert element forekommer+samlet antal +laver nyt map
Map<String, Double> support = new HashMap<>(); //laver tom map som gemmer hvert elements navn+dens support tal
  if (total <=0) return support;
  for (Map.Entry<String, Integer> e : freq.entrySet()) { //den starter en for loop og kører igennem navn og antal
    support.put(e.getKey(), (double) e. getValue() / total); //den regner hvor stor hver del fylder og gemmer det i en liste 
  }
    return support;
}
public Map<String, Double> filterByMinSupport(Map<String, Double> supportMap) { //metoden skal finde de ting i listen der har en supportværdi over et bestemt min
  Map<String, Double> filtered = new HashMap<>(); //den et tomt map, hvor vi kan ligge de ting der har højt nok tal
  for (Map. Entry<String, Double> e : supportMap.entrySet()){ //for hver ting+tal gem dem i e, så vi kan bruge dem
    if (e.getValue() >= this.minSupport) {
      filtered.put(e.getKey(), e.getValue());
    }
  }
  return filtered;
  //linjerne oppe tjekker om hvert ting har en høj nok værdi (mindst min support) hvis ja, bliver den gemt i filtered som efter sendes tilbage
}

public Map<String, Double> //metoden giver et map tilbage, hvor hver ting (String) har et tal med komma (Double) koblet til sig.
  findFrequentItemsWithMinSupport(List<String>items) { // Den får en liste af ting og returnerer dem, der optræder ofte nok ud fra den fastsatte minimumsgrænse.
  Map<String, Integer> counts=findFrequentItems(items); // Her tælles hvor mange gange hver ting findes i listen, og gemmer resultatet i counts
  Map<String, Double>supports=computeSupport(counts, items.size()); // Den beregner, hvor stor en andel (support) hver ting udgør af det samlede antal i listen.
  return filterByMinSupport(supports); // Den sender listen med support-tal videre og returnerer kun de ting, der har højt nok support.
} 



//vi starter med confidence delen
//beregn support for alle 2-item kombinationer (altså hvor ofte 2 ting optræder sammen)
public Map<Set<String>, Double> computePairSupport(List<List<String>> transactions) { //beregner hvor ofte par stjernetegn forekommer blandt seriemordere; nøglen(stjerntegney); værdien(double)viser supporten
  Map<Set<String>,Integer> pairCounts = new HashMap<>(); //tæller hvor mange gange hvert par forekommer
  int totalTransactions = transactions.size(); //tæller hvor mange mordere der er i alt, gemmer det samlet antal seriemordere i variablen "total transasctions"

  for (List<String> t : transactions) { //går igennem hver seriemorder i listen "transactions"; t: 1 seriemorders data
    for (int i = 0; i < t.size(); i++) { //starter en yderste løkke, der gennemgår alle elementer i morderens liste, i er positionen for det første mulige par
      for (int j = i + 1; j < t.size(); j++) { //indre løkke, laver par mellem i og j, laver i+1 så man ikke laver det samme 2 gange
        Set<String> pair = new HashSet<>(); //oprettes nyt tomt sæt, som skal indeholde 2 stjernetegn
        pair.add(t.get(i));//henter de 2 elementer fra morder listen r og tilføjer til sættet
        pair.add(t.get(j));
        pairCounts.put(pair, pairCounts.getOrDefault(pair, 0) + 1); //opdateres tællingen hvor mange gange det blevet set + lægges 1 til hvis man har set parret 1 gang til
      }
    }
  }

  Map<Set<String>, Double> pairSupport = new HashMap<>(); //nyt map, hvor resultatet gemmes, nøglen sæt med 2 stjernetegn + support værdien hvor stor en andel af morderene har den kombi
  for (Map.Entry<Set<String>, Integer> e : pairCounts.entrySet()) { //paircounts er hvor mange gange stjernetegnene er set
    pairSupport.put(e.getKey(), (double) e.getValue() / totalTransactions);//beregner support værdien for hvert par og gemmer det i nyt map
  }

  return pairSupport;
}

public Map<String, Double> computeConfidence( //de næste 3 linjer er metodens hoved, tager i mod 2 maps: pair support hvor en andel af mordere der har et bestemt par af stjernetegn
  Map<Set<String>, Double>pairSupport,//forsættelse: singleSupport: hvor stor en andel der har et stjernetegn
  Map<String, Double> singleSupport) { 
  Map <String, Double> confidence = new HashMap<>();//opretter nyt tomt map, hvor resultaterne gemmes

  for (Map.Entry<Set<String>, Double> e : pairSupport.entrySet()) { //går igennem hvert par og dets supportværdi
    List<String> items = new ArrayList<>(e.getKey()); //liste ud af parrene
    if (items.size() == 2) { //tjekker den har 2 elementer (et par)
      String A = items.get(0);//disse to er de 2 elementer i parret a og b
      String B = items.get(1);

      double supportAB = e.getValue(); //gemmer support værdien for a og b, hvor ofte de forekommer sammen
      double supportA = singleSupport.getOrDefault(A, 0.0); //de næste to: henter support værdien for hver stjernetegn fra single support
      double supportB = singleSupport.getOrDefault(B, 0.0); //get.. betyder hvis stjernetegnet ikke findes i map så brug 0 som standardværdi

      if (supportA > 0) confidence.put(A + " → " + B, supportAB/ supportA); //kernen af beregning: så de 2 regler udregnes for hvert par support (a,b)/support(a) og den anden med (b)
      if (supportB > 0) confidence.put(B + " → " + A, supportAB / supportB); //begge regler bliver lagt i confidence mappet med tekst som nøgler
      }
    }
  return confidence; //returnere hele confidence map som nu har alle sammenhænge
  }

  

