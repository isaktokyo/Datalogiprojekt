// Source - https://stackoverflow.com/questions/37552736/how-do-you-loop-through-a-column-of-a-csv-file-to-find-a-certain-string-in-java
// Posted by matebannan, modified by community. See post 'Timeline' for change history
// Retrieved 2025-12-06, License - CC BY-SA 3.0

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVReader {
    static ArrayList<String> råDataFøds = new ArrayList<>();
    static String[] dataen = new String[0];
    static ArrayList<String> hedonisticData = new ArrayList<>();
    static ArrayList<String> powerData = new ArrayList<>();
    static ArrayList<String> missionData = new ArrayList<>();
    static ArrayList<String> visionaryData = new ArrayList<>();
    static ArrayList<String> organizedData = new ArrayList<>();
    static ArrayList<String> disorganizedData = new ArrayList<>();
    static ArrayList<String> mixedData = new ArrayList<>();

    public static void main(String[] args) {

        CSVReader obj = new CSVReader();
        obj.run();

        System.out.println(råDataFøds); //1562 mordere!
        System.out.println(powerData);
        System.out.println(organizedData);
    }

    public void run() {
        String[] maanedene = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        // vi fylder arraylists med (semi) rå data, som kun er checket for at de ikke er empty.
        getData("Hedonistic.csv", hedonisticData);
        getData("Power.csv", powerData);
        getData("MissionOriented.csv", missionData);
        getData("Visionary.csv", visionaryData);
        getData("Organized.csv", organizedData);
        getData("Disorganized.csv", disorganizedData);
        getData("Mixed.csv", mixedData);

        for (int m = 0; m< maanedene.length; m++) {
            try (BufferedReader br = new BufferedReader(new FileReader(maanedene[m] + "Birth.csv"))
            ) {
                String csvSplit = ",";
                String line;
                while ((line = br.readLine()) != null) {
                    // Fjern "
                    if(line.contains("Birth")) { // vi vil kun hente de linjer der er morderes fødselsdato.
                        line = line.replace("\"", ""); // vi

                        // Split hver komma
                        String[] cols = line.split(",");

                        if (cols.length < 2) continue; // Skip ugyldige linjer

                        String dato = cols[0].trim(); // Kolonne 0 = dato,
                        String navn = cols[1].trim(); // Kolonne 1 = navn.

                        if (!dato.matches("\\d{2}/\\d{2}/\\d{4}"))
                            continue; // vi checker formatet: det skal være en dato: 10/10/1995
                        // vi burde dog lægge til en buffer, som gør det muligt at inkludre ukomplette fødselsdatoer,
                        // fordi vores database er ukomplett.

                        råDataFøds.add(navn + ";;" + dato); // Lav midlertidig kombineret datapunkt
                    }
                    }

                } catch (IOException io) {
                System.out.println(io);
            }
        }
    }
    public void getData (String filename, ArrayList<String> dataen){
       try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Hent kun navn fra CSV-cellene i anførselstegn
                int idx1 = line.indexOf('"');
                int idx2 = line.indexOf('"', idx1 + 1);
                if (idx1 != -1 && idx2 != -1) {
                    line = line.substring(idx1 + 1, idx2);
                }

                int index = line.indexOf("https"); // for hver person behøver vi kun navn. vi vil derfor fjerne alt der kommer efter.
                if (index != -1) { // bare hvis "http" faktisk findes
                    System.out.println(index);
                    line = line.substring(0, index-2); // vi fjerner alt efter index hvor https starter, og så to mere(nemlig et komma og mellemrum)
                    System.out.println(line);

                }
                // lægger line ind i listen
                    dataen.add(line);

            }
        } catch (IOException io) {
            System.out.println(io);
        }

    }
}
