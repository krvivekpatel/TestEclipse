import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class DashboardAppender {
    private static final String HTML_FILE = "dashboard.html";
    private static final String TEMPLATE_FILE = "dashboard_template.html";

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
            String statusColor = result.status.equalsIgnoreCase("Passed") ? "#2e7d32" : "#d32f2f";
            String bgColor = i % 2 == 0 ? "#f9f9f9" : "#f2f2f2";
            
            tableRows.append(String.format("<tr style=\"background-color: %s;\">", bgColor));
            if (i == 0) {
                tableRows.append(String.format(
                    "<td rowspan=\"%d\" style=\"vertical-align: middle; text-align: center; font-size: 16px; font-weight: bold; color: #333; padding: 12px; border: 1px solid #ddd;\">%s</td>",
                    testResults.size(), escapeHtml(environment)
                ));
            }
            tableRows.append(String.format(
                "<td style=\"color: %s; font-weight: bold; padding: 12px; text-align: left; border: 1px solid #ddd;\">%s</td>",
                statusColor, escapeHtml(result.status)
            ));
            tableRows.append(String.format(
                "<td style=\"font-style: italic; padding: 12px; text-align: left; border: 1px solid #ddd;\">%s</td>",
                escapeHtml(result.comment)
            ));
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
                content = content.replace("</table>", "<tbody>\n                " + tableRows.toString() + "</tbody>\n                </table>");
            }

            // Write updated content
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(HTML_FILE))) {
                writer.write(content);
            }
        } else {
            // Read the template
            String templateContent = new String(Files.readAllBytes(Paths.get(TEMPLATE_FILE)));

            // Replace placeholder with table rows
            String htmlContent = String.format(templateContent, tableRows.toString());

            // Write new HTML file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(HTML_FILE))) {
                writer.write(htmlContent);
            }
        }
    }

    // Utility method to escape HTML special characters
    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
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

        // Append another environment
        String environment2 = "Staging";
        List<TestResult> testResults2 = new ArrayList<>();
        testResults2.add(new TestResult("Failed", "Database connection timeout."));
        testResults2.add(new TestResult("Passed", "Security tests passed."));

        appendToDashboard(environment2, testResults2);
    }
}
