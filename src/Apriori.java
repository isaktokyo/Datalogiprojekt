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

public Map<String, Double> //metoden giver et map tilbage, hvor hver ting (String) har et tal med komma (Double) koblet til sig.
  findFrequentItemsWithMinSupoort(List<String>items) { // Den får en liste af ting og returnerer dem, der optræder ofte nok ud fra den fastsatte minimumsgrænse.
  Map<String, Integer> counts=findFrequentItems(items); // Her tælles hvor mange gange hver ting findes i listen, og gemmer resultatet i counts
  Map<String, Double>supports=computeSupport(Counts),
    items.size()); // Den beregner, hvor stor en andel (support) hver ting udgør af det samlede antal i listen.
  return filterByMinSupport(supports); // Den sender listen med support-tal videre og returnerer kun de ting, der har højt nok support.
} 
// Eksempel på brug
  public static void main(String[] args) {
    List<String> items = Arrays.asList( // Der laves en liste med 10 elementer (stjernetegn). Listen fungerer som data, som algoritmen skal analysere.
      "Fisk","Vægten","Fisk","Jomfru","Fisk",
      "Skytte","Jomfru","Vægten","Skytte","Fisk"
    ); // 10 elementer i alt

    Apriori a = new Apriori(0.2); //Opretter et Apriori-objekt, hvor 0.2 betyder, at minimumsgrænsen (minSupport) for at et element regnes som “hyppigt” er 20 % (altså skal det optræde i mindst 2 ud af 10 tilfælde).
    Map<String, Double> frequent = a.findFrequentItemsWithMinSupport(items); // Programmet kalder metoden, som finder alle elementer i listen, der optræder oftere end 20 %.

    System.out.println("Frekvens (count): " + a.findFrequentItems(items)); // Viser hvor mange gange hvert element (fx “Fisk”, “Jomfru” osv.) optræder i listen.
    System.out.println("Support (procent): "); // Hvor stor en procentdel hvert element udgør af det samlede datasæt
    for (Map.Entry<String, Double> e : a.computeSupport(a.findFrequentItems(items), items.size()).entrySet()) { // Går igennem alle elementer i et “map”, der indeholder hvert element og dets beregnede support-værdi (fx “Fisk” → 0.5).
      System.out.printf("  %s: %.2f%n", e.getKey(), e.getValue());
    }

    System.out.println("Elementer med support >= " + a.minSupport + ": " + frequent);
  }
}
