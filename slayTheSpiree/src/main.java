import java.io.*;
import java.util.*;
import java.util.regex.*;

public class main {
    public static void main(String[] args) {
        try {
            // Read file and process data
            File file = new File("slayTheSpiree/src/input.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));

            Random random = new Random();
            int deckId = 100000000 + random.nextInt(900000000);
            Map<Integer, Integer> costFrequency = new HashMap<>();
            List<String> invalidCards = new ArrayList<>();
            int totalCost = 0;

            //reading in each line and splitting at colon
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length != 2) {
                    invalidCards.add(line);
                    continue;
                }

                //cardName = "name"
                //costStr = value
                String cardName = parts[0].trim();
                String costStr = parts[1].trim();

                //check cardname
                if (cardName.isEmpty()) {
                    invalidCards.add(line);
                    continue;
                }

                //validate cost
                int cost;
                try {
                    cost = Integer.parseInt(costStr);
                } catch (NumberFormatException e) {
                    invalidCards.add(line);
                    continue;
                }

                if (cost < 0 || cost > 6) {
                    invalidCards.add(line);
                    continue;
                }

                totalCost += cost;
                costFrequency.put(cost, costFrequency.getOrDefault(cost, 0) + 1);
            }

            br.close();

            // Check for void conditions
            if (invalidCards.size() > 10 || costFrequency.size() > 1000) {
                generateVoidReport(deckId);
            } else {
                generateReport(deckId, totalCost, costFrequency, invalidCards);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //function to generate report
    private static void generateReport(int deckId, int totalCost, Map<Integer, Integer> costFrequency, List<String> invalidCards) throws IOException {
        FileWriter writer = new FileWriter("slayTheSpiree/src/SpireDeck " + deckId + ".txt");

        writer.write("Deck ID: " + deckId + "\n");
        writer.write("Total Cost: " + totalCost + "\n");
        writer.write("Energy Cost Histogram:\n");

        // Write histogram
        for (Map.Entry<Integer, Integer> entry : costFrequency.entrySet()) {
            writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
        }

        writer.write("Invalid Cards:\n");
        for (String invalidCard : invalidCards) {
            writer.write(invalidCard + "\n");
        }

        writer.close();
    }

    //function to generate report if it is invalid/void
    private static void generateVoidReport(int deckId) throws IOException {
        FileWriter writer = new FileWriter("slayTheSpiree/src/SpireDeck " + deckId + "(VOID).txt");

        writer.write("VOID\n");

        writer.close();
    }
}
