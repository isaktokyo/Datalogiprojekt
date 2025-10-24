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
//altså hvor ofte et item forekommer

