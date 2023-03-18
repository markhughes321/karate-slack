package examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        // Create JSON object with report contents in attachments array
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("channel", "D04UXS4QMGR");
        ArrayNode attachments = mapper.createArrayNode();
        String reportFilePath = karateReportsPath + "/karate-summary-json.txt";
        File reportFile = new File(reportFilePath);
        if (reportFile.exists()) {
            try {
                String reportContents = new String(Files.readAllBytes(reportFile.toPath()));
                ObjectNode attachment = mapper.createObjectNode();
                attachment.put("text", reportContents);
                attachments.add(attachment);
                json.set("attachments", attachments);
            } catch (IOException e) {
                System.out.println("Failed to read Karate report: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("Karate report file not found");
            return;
        }

        // Write JSON object to Slack report file
        File slackReportFile = new File("target/slack-report.json");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(slackReportFile, json);
            System.out.println("Slack report generated at " + slackReportFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to write Slack report: " + e.getMessage());
        }
    }
}
