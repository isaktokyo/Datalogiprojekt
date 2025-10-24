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
}

//nu skal vi igang med "support", hvis der er 10p og 3 af dem er fisk er der 3/10=0,3=30%

Public Map<String, Double> computeSupport(Map<String, Integer> freq, int total) { //her bliver der lavet liste over hvor mange gange hvert element forekommer+samlet antal +laver nyt map
Map<String, Double> support = new HashMap<>(); //laver tom map som gemmer hvert elements navn+dens support tal
  if (total <=0) return support;
  for (Map,Entry<String, Integer> e : freq.entrySet()) { //den starter en for loop og kører igennem navn og antal
    support.put(e.getKey(), (double) e. getValue() / total); //den regner hvor stor hver del fylder og gemmer det i en liste 
  }
    return support;
}
public Map<String, Double> filterByMinSupport(Map<String, Double> supportMap) { //metoden skal finde de ting i listen der har en supportværdi over et bestemt min
  Map<String, Double> filtered = new HashMap<>(); //den et tomt map, hvor vi kan ligge de ting der har højt nok tal
  for (Map,Entry<String, Double> e : supportMap.entrySet()){ //for hver ting+tal gem dem i e, så vi kan bruge dem
    if (e.getValue() >= this.minSupport) {
      filtered.put(e.getKey(), e.getValue());
    }
  }
  return filtered;
  //linjerne oppe tjekker om hvert ting har en høj nok værdi (mindst min support) hvis ja, bliver den gemt i filtered som efter sendes tilbage
}
  
  
