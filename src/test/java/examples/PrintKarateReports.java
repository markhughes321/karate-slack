package examples;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PrintKarateReports {

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

        // Convert Slack report JSON object to string
        String slackJsonString;
        try {
            slackJsonString = mapper.writeValueAsString(slackJson);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert Slack report to JSON string.", e);
        }

        // Send Slack report as HTTP POST request to Slack webhook URL using OkHttp
        String slackWebhookUrl = "https://hooks.slack.com/services/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";
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, slackJsonString);
        Request request = new Request.Builder()
          .url(slackWebhookUrl)
          .post(body)
          .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println("Slack report sent. Response: " + response.body().string());
        } catch (IOException e) {
            throw new RuntimeException("Failed to send Slack report as HTTP POST request.", e);
        }
    }
}
