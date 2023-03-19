package examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PrintKarateReports {

    public static void main(String[] args) {
        printReports();
    }

    public static void printReports() {
        String karateReportsPath = "target/karate-reports";
        File[] karateReportsFiles = new File(karateReportsPath).listFiles();

        // Loop through files in Karate reports folder and print their names
        for (File file : karateReportsFiles) {
            System.out.println("- " + file.getName());
        }

        // Create Slack report JSON object with Slack block
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode slackJson = mapper.createObjectNode();

        // Create Slack block object
        ObjectNode slackBlock = mapper.createObjectNode();
        slackBlock.put("type", "section");

        // Create Slack text object
        ObjectNode slackText = mapper.createObjectNode();
        slackText.put("type", "mrkdwn");
        slackText.put("text", "elapsed:   6.35 | threads:    8 | thread time: 1.94");

        // Add Slack text object to Slack block object
        slackBlock.set("text", slackText);

        // Add Slack block object to Slack report JSON object
        ArrayNode slackBlocks = mapper.createArrayNode();
        slackBlocks.add(slackBlock);
        slackJson.set("blocks", slackBlocks);

        // Write Slack report JSON object to file
        File slackReportFile = new File("target/slack-report.json");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(slackReportFile, slackJson);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Slack report to file.", e);
        }
    }
}
