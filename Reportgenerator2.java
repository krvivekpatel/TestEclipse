import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlDashboardGenerator {
    // Data structure to represent a test result
    static class TestResult {
        String status;
        String comment;

        TestResult(String status, String comment) {
            this.status = status;
            this.comment = comment;
        }
    }

    // Data structure to represent an environment
    static class Environment {
        String name;
        List<TestResult> tests;

        Environment(String name, List<TestResult> tests) {
            this.name = name;
            this.tests = tests;
        }
    }

    public static String generateHtml(List<Environment> environments) {
        // HTML header
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
            .append("<html lang=\"en\">\n")
            .append("<head>\n")
            .append("    <meta charset=\"UTF-8\">\n")
            .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
            .append("    <title>Test Environment Dashboard</title>\n")
            .append("</head>\n")
            .append("<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f9;\">\n")
            .append("    <!-- Wrapper table to control width and centering -->\n")
            .append("    <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px; margin: 0 auto; background-color: #fff; padding: 20px; border-radius: 8px;\">\n")
            .append("        <tr>\n")
            .append("            <td align=\"center\" style=\"padding: 20px;\">\n")
            .append("                <h1 style=\"color: #333; margin: 0 0 20px 0; font-size: 24px;\">Test Environment Dashboard</h1>\n")
            .append("                <!-- Main content table -->\n")
            .append("                <table border=\"1\" cellpadding=\"12\" cellspacing=\"0\" style=\"width: 100%; max-width: 600px; border-collapse: collapse; border: 1px solid #ddd;\">\n")
            .append("                    <thead>\n")
            .append("                        <tr style=\"background-color: #4CAF50; color: white;\">\n")
            .append("                            <th style=\"padding: 12px; text-align: left; font-weight: bold;\">Environment</th>\n")
            .append("                            <th style=\"padding: 12px; text-align: left; font-weight: bold;\">Status</th>\n")
            .append("                            <th style=\"padding: 12px; text-align: left; font-weight: bold;\">Comment</th>\n")
            .append("                        </tr>\n")
            .append("                    </thead>\n")
            .append("                    <tbody>\n");

        // Generate dynamic rows
        for (Environment env : environments) {
            int rowspan = env.tests.size();
            for (int i = 0; i < env.tests.size(); i++) {
                TestResult test = env.tests.get(i);
                String statusColor = test.status.equalsIgnoreCase("Passed") ? "#2e7d32" : "#d32f2f";
                String bgColor = i % 2 == 0 ? "#f9f9f9" : "#f2f2f2";

                html.append("                        <tr style=\"background-color: ").append(bgColor).append(";\">\n");
                
                // Add environment cell with rowspan for the first row of each environment
                if (i == 0) {
                    html.append("                            <td rowspan=\"").append(rowspan)
                        .append("\" style=\"vertical-align: middle; text-align: center; font-size: 16px; font-weight: bold; color: #333; padding: 12px;\">")
                        .append(escapeHtml(env.name)).append("</td>\n");
                }

                // Add status and comment cells
                html.append("                            <td style=\"color: ").append(statusColor)
                    .append("; font-weight: bold; padding: 12px;\">").append(escapeHtml(test.status)).append("</td>\n")
                    .append("                            <td style=\"font-style: italic; padding: 12px;\">")
                    .append(escapeHtml(test.comment)).append("</td>\n")
                    .append("                        </tr>\n");
            }
        }

        // HTML footer
        html.append("                    </tbody>\n")
            .append("                </table>\n")
            .append("            </td>\n")
            .append("        </tr>\n")
            .append("    </table>\n")
            .append("</body>\n")
            .append("</html>\n");

        return html.toString();
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
    public static void main(String[] args) {
        // Sample data
        List<Environment> environments = new ArrayList<>();
        
        List<TestResult> prodTests = new ArrayList<>();
        prodTests.add(new TestResult("Passed", "All tests executed successfully."));
        prodTests.add(new TestResult("Failed", "Issue with login module."));
        prodTests.add(new TestResult("Passed", "Performance tests completed."));
        
        List<TestResult> stagingTests = new ArrayList<>();
        stagingTests.add(new TestResult("Failed", "Database connection timeout."));
        stagingTests.add(new TestResult("Passed", "Security tests passed."));
        
        environments.add(new Environment("Production", prodTests));
        environments.add(new Environment("Staging", stagingTests));

        // Generate HTML
        String html = generateHtml(environments);

        // Save to file (or use in email)
        try (FileWriter writer = new FileWriter("dashboard_updated.html")) {
            writer.write(html);
            System.out.println("HTML file generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
