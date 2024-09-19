import java.io.*;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils; // Updated import
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class Main {
    public static void main(String[] args) {
        try {
            // Read file and process data
            File file = new File("src/main/java/input.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));

            Random random = new Random();
            int deckId = 100000000 + random.nextInt(900000000);
            Map<Integer, Integer> costFrequency = new HashMap<>();
            List<String> invalidCards = new ArrayList<>();
            int totalCost = 0;

            // Reading each line and splitting at colon
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length != 2) {
                    invalidCards.add(line);
                    continue;
                }

                String cardName = parts[0].trim();
                String costStr = parts[1].trim();

                // Check card name
                if (cardName.isEmpty()) {
                    invalidCards.add(line);
                    continue;
                }

                // Validate cost
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
                generatePdfReport(deckId, totalCost, costFrequency, invalidCards);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function to generate text report
    private static void generateReport(int deckId, int totalCost, Map<Integer, Integer> costFrequency, List<String> invalidCards) throws IOException {
        FileWriter writer = new FileWriter("src/main/java/SpireDeck " + deckId + ".txt");

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

    // Function to generate void report
    private static void generateVoidReport(int deckId) throws IOException {
        FileWriter writer = new FileWriter("src/main/java/SpireDeck " + deckId + "(VOID).txt");

        writer.write("VOID\n");

        writer.close();
    }

    // Function to generate PDF report
    private static void generatePdfReport(int deckId, int totalCost, Map<Integer, Integer> costFrequency, List<String> invalidCards) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("src/main/java/SpireDeck " + deckId + ".pdf"));
            document.open();

            document.add(new Paragraph("Deck ID: " + deckId));
            document.add(new Paragraph("Total Cost: " + totalCost));
            document.add(new Paragraph("Energy Cost Histogram:"));

            // Create and add histogram image to PDF
            JFreeChart chart = createHistogram(costFrequency);
            File chartFile = new File("src/main/java/histogram.png");
            ChartUtils.saveChartAsPNG(chartFile, chart, 500, 300);
            Image img = Image.getInstance(chartFile.getAbsolutePath());
            document.add(img);

            document.add(new Paragraph("Invalid Cards:"));
            for (String invalidCard : invalidCards) {
                document.add(new Paragraph(invalidCard));
            }

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    // Function to create histogram chart
    private static JFreeChart createHistogram(Map<Integer, Integer> costFrequency) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<Integer, Integer> entry : costFrequency.entrySet()) {
            dataset.addValue(entry.getValue(), "Frequency", entry.getKey().toString());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Energy Cost Histogram",
                "Cost",
                "Frequency",
                dataset
        );

        return chart;
    }
}
