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
  for (Map. Entry<String, Integer> e : freq.entrySet()) { //den starter en for loop og kører igennem navn og antal
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
  findFrequentItemsWithMinSupoort(List<String>items) { // Den får en liste af ting og returnerer dem, der optræder ofte nok ud fra den fastsatte minimumsgrænse.
  Map<String, Integer> counts=findFrequentItems(items); // Her tælles hvor mange gange hver ting findes i listen, og gemmer resultatet i counts
  Map<String, Double>supports=computeSupport(counts),
    items.size()); // Den beregner, hvor stor en andel (support) hver ting udgør af det samlede antal i listen.
  return filterByMinSupport(supports); // Den sender listen med support-tal videre og returnerer kun de ting, der har højt nok support.
} 



//vi starter med confidence delen
//beregn support for alle 2-item kombinationer (altså hvor ofte 2 ting optræder sammen)
public Map<Set<String>, Double> computePairSupport(List<List<String>> transactions) {
  Map<Set<String>,Integer> pairCounts = new HashMap<>();
  int totalTransactions = treansactions.size();

  for (List<String> t : transactions) {
    for (int i = 0; i < t.size(); i++) {
      for (int j = i + 1; j < t.size(); j++) {
        Set<String> pair = new HashSet<>();
        pair.add(t.get(i));
        pair.add(t.get(j));
        pairCounts.put(pair, pairCounts.getOrDefault(pair, 0) + 1);
      }
    }
  }

  Map<Set<String>, Double> pairSupport = new HashMap<>();
  for (Map.Entry<Set<String> Integer> e : pairCounts.entrySet()) {
    pairSupport.put(e.getKey(), (double) e.getValue() / totalTransactions);
  }

  return pairSupport;
}

public Map<String, Double> computeConfidence(
  Map<SetzString>, Double>pairSupport,
  Map<String, Double> singleSupport) {
  Map <String, Double> confidence = new HashMap<>();

  for (Map.Entry<Set<String>, Double> e : pairSupport,entrySet()) {
    List<String> items = new ArrayList<>(e.getKey());
    if (items.size() == 2) {
      String A = items.get(0);
      String B = items.get(1);

      double supportAB = e.getValue();
      double supportA = singleSupport.getOrDefault(A, 0.0);
      double supportB = singleSupport.getOrDefault(B, 0.0); 

      if (supportA > 0) confidence.put(A + " → " + B, supportAB/ supportA);
      if (supportB > 0) confidence.put(B+ " → " + A, supportAB / support B);
      }
    }
  return confidence;
  }

  

