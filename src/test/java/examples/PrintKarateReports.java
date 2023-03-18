// PrintKarateReports.java
package examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        // Rename karate-summary-json.txt to slack-report.json and export to target folder
        String summaryFilePath = karateReportsPath + "/karate-summary-json.txt";
        File summaryFile = new File(summaryFilePath);
        if (summaryFile.exists()) {
            Path sourcePath = Paths.get(summaryFilePath);
            Path targetPath = Paths.get("target/slack-report.json");
            try {
                Files.move(sourcePath, targetPath);
                System.out.println("Renamed and exported summary file to " + targetPath.toString());
            } catch (IOException e) {
                System.out.println("Failed to rename and export summary file: " + e.getMessage());
            }
        }
    }
}
