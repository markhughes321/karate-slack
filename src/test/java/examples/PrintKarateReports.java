package examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PrintKarateReports {

    public static void printReports() {
        String karateReportsPath = "target/karate-reports";
        File karateReportsFolder = new File(karateReportsPath);
        if (!karateReportsFolder.exists() || !karateReportsFolder.isDirectory()) {
            System.out.println("No Karate reports found at " + karateReportsPath);
            return;
        }
        System.out.println("Karate reports found at " + karateReportsPath + ":");
        for (File file : karateReportsFolder.listFiles()) {
            System.out.println("- " + file.getName());
        }

        // Read contents of Karate report file
        String summaryFilePath = karateReportsPath + "/karate-summary-json.txt";
        File summaryFile = new File(summaryFilePath);
        if (summaryFile.exists()) {
            try {
                String reportContents = new String(Files.readAllBytes(summaryFile.toPath()));
                System.out.println("Karate report contents:\n" + reportContents);

                // Convert report contents to JSON object
                ObjectMapper mapper = new ObjectMapper();
                JsonNode reportJson = mapper.readTree(reportContents);

                // Create Slack report JSON object with report contents in attachments array
                ObjectNode slackJson = mapper.createObjectNode();
                slackJson.put("channel", "CBR2V3XEX");
                ArrayNode attachments = mapper.createArrayNode();
                ObjectNode attachment = mapper.createObjectNode();
                attachment.setAll((ObjectNode) reportJson);
                attachments.add(attachment);
                slackJson.set("attachments", attachments);

                // Write Slack report JSON object to file
                File slackReportFile = new File("target/slack-report.json");
                mapper.writerWithDefaultPrettyPrinter().writeValue(slackReportFile, slackJson);
                System.out.println("Slack report generated at " + slackReportFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Failed to read or write report: " + e.getMessage());
            }
        } else {
            System.out.println("Karate report file not found");
        }
    }
}
