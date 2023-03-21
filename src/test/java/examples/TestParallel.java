import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

public class TestParallel {

    @Test
    void testParallel() throws IOException {
        Results results = Runner.path("classpath:examples")
                .outputCucumberJson(true)
                .parallel(10);
        generateReport(results.getReportDir());
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    public static void generateReport(String karateOutputPath) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[] { "json" }, true);
        List<String> jsonPaths = new ArrayList<>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        Configuration config = new Configuration(new File("target"), "Slack Report Repo");
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
        
        // Copy cucumber report to new directory
        try {
            copyCucumberReportToNewDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyCucumberReportToNewDirectory() throws IOException {
        // Create new directory
        String projectName = "HWC";
        String dateStr = new SimpleDateFormat("ddMMyy").format(new Date());
        File newDir = new File("target/" + projectName + "/" + dateStr);
        newDir.mkdirs();
        
        // Copy files from old directory to new directory
        FileUtils.copyDirectory(new File("target/cucumber-html-reports"), newDir);
    }

}
