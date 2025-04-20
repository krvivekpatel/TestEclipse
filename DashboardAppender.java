import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class DashboardAppender {
    private static final String HTML_FILE = "dashboard.html";
    private static final String CSS = """
        body { font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 20px; display: flex; justify-content: center; align-items: center; min-height: 100vh; }
        .dashboard-container { max-width: 800px; width: 100%; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }
        h1 { text-align: center; color: #333; margin-bottom: 20px; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }
        th { background-color: #4CAF50; color: white; font-weight: bold; }
        td { background-color: #f9f9f9; }
        tr:nth-child(even) td { background-color: #f2f2f2; }
        tr:hover td { background-color: #e0e0e0; }
        .merged-cell { vertical-align: middle; text-align: center; font-size: 16px; font-weight: bold; color: #333; }
        .status-passed { color: #2e7d32; font-weight: bold; }
        .status-failed { color: #d32f2f; font-weight: bold; }
        .comment { font-style: italic; }
    """;

    public static class TestResult {
        String status;
        String comment;

        public TestResult(String status, String comment) {
            this.status = status;
            this.comment = comment;
        }
    }

    public static void appendToDashboard(String environment, List<TestResult> testResults) throws IOException {
        File file = new File(HTML_FILE);
        StringBuilder tableRows = new StringBuilder();

        // Generate table rows for the environment
        for (int i = 0; i < testResults.size(); i++) {
            TestResult result = testResults.get(i);
            tableRows.append("<tr>");
            if (i == 0) {
                tableRows.append(String.format("<td rowspan=\"%d\" class=\"merged-cell\">%s</td>", testResults.size(), environment));
            }
            tableRows.append(String.format("<td class=\"status-%s\">%s</td>", result.status.toLowerCase(), result.status));
            tableRows.append(String.format("<td class=\"comment\">%s</td>", result.comment));
            tableRows.append("</tr>\n                ");
        }

        if (file.exists()) {
            // Read existing HTML content
            String content = new String(Files.readAllBytes(Paths.get(HTML_FILE)));

            // Append rows to tbody
            if (content.contains("</tbody>")) {
                content = content.replace("</tbody>", tableRows.toString() + "</tbody>");
            } else {
                // If tbody is missing, append to table
                content = content.replace("</table>", "<tbody>\n                " + tableRows.toString() + "</tbody>\n        </table>");
            }

            // Write updated content
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(HTML_FILE))) {
                writer.write(content);
            }
        } else {
            // Create new HTML file
            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Test Environment Dashboard</title>
                    <style>
                        %s
                    </style>
                </head>
                <body>
                    <div class="dashboard-container">
                        <h1>Test Environment Dashboard</h1>
                        <table>
                            <thead>
                                <tr>
                                    <th>Environment</th>
                                    <th>Status</th>
                                    <th>Comment</th>
                                </tr>
                            </thead>
                            <tbody>
                                %s
                            </tbody>
                        </table>
                    </div>
                </body>
                </html>
                """, CSS, tableRows.toString());

            // Write new HTML file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(HTML_FILE))) {
                writer.write(htmlContent);
            }
        }
    }

    // Example usage
    public static void main(String[] args) throws IOException {
        String environment = "Production";
        List<TestResult> testResults = new ArrayList<>();
        testResults.add(new TestResult("Passed", "All tests executed successfully."));
        testResults.add(new TestResult("Failed", "Issue with login module."));
        testResults.add(new TestResult("Passed", "Performance tests completed."));
        testResults.add(new TestResult("Failed", "Database connection timeout."));
        testResults.add(new TestResult("Passed", "Security tests passed."));
        testResults.add(new TestResult("Passed", "Final validation completed."));

        appendToDashboard(environment, testResults);
    }
}
