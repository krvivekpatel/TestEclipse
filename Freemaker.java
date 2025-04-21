import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlDashboardGenerator {
    // Data structure for a test result
    static class TestResult {
        String status;
        String comment;

        TestResult(String status, String comment) {
            this.status = status;
            this.comment = comment;
        }

        // Getters for FreeMarker
        public String getStatus() {
            return status;
        }

        public String getComment() {
            return comment;
        }
    }

    // Data structure for an environment
    static class Environment {
        String name;
        List<TestResult> tests;

        Environment(String name, List<TestResult> tests) {
            this.name = name;
            this.tests = tests;
        }

        // Getters for FreeMarker
        public String getName() {
            return name;
        }

        public List<TestResult> getTests() {
            return tests;
        }
    }

    public static String generateHtml(List<Environment> environments) throws IOException, TemplateException {
        // Configure FreeMarker
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setClassForTemplateLoading(HtmlDashboardGenerator.class, "/");
        cfg.setDefaultEncoding("UTF-8");

        // Load the template
        Template template = cfg.getTemplate("dashboard_template.html");

        // Prepare data model
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("environments", environments);

        // Process the template
        StringWriter out = new StringWriter();
        template.process(dataModel, out);
        return out.toString();
    }

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

        try {
            // Generate HTML
            String html = generateHtml(environments);

            // Save to file for testing
            try (FileWriter writer = new FileWriter("dashboard_updated.html")) {
                writer.write(html);
                System.out.println("HTML file generated successfully.");
            }

            // Optionally, use the HTML in an email (example with JavaMail)
            // sendEmail(html);

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    // Optional: Method to send email (requires JavaMail dependency)
    /*
    public static void sendEmail(String htmlContent) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.example.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your-email@example.com", "your-password");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("your-email@example.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("recipient@example.com"));
        message.setSubject("Test Environment Dashboard");
        message.setContent(htmlContent, "text/html; charset=utf-8");

        Transport.send(message);
    }
    */
}
