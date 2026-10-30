package com.example.registration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class TestDocumentationGenerator {
    private static final Pattern TEST_CASE_ID = Pattern.compile("tc(\\d{2})", Pattern.CASE_INSENSITIVE);
    private static final List<TestCase> TEST_CASES = List.of(
            new TestCase("TC01", "Register with valid details",
                    "Registration succeeds and the student is saved."),
            new TestCase("TC02", "Submit an empty form",
                    "All four required-field errors are returned."),
            new TestCase("TC03", "Enter an invalid email",
                    "Invalid-email error is returned for john123."),
            new TestCase("TC04", "Enter a short password",
                    "Password minimum-length error is returned for pass12."),
            new TestCase("TC05", "Enter mismatched passwords",
                    "Password-mismatch error is returned."),
            new TestCase("TC06", "Register with a duplicate email",
                    "Duplicate email is rejected regardless of case."),
            new TestCase("TC07", "Leave the name empty",
                    "Name-required error is returned."),
            new TestCase("TC08", "Leave the email empty",
                    "Email-required error is returned."),
            new TestCase("TC09", "Use exactly eight password characters",
                    "The eight-character password is accepted."),
            new TestCase("TC10", "Use an uppercase email",
                    "Email is accepted and normalized to lowercase."),
            new TestCase("TC11", "Enter an email without a domain extension",
                    "Invalid-email error is returned for john@gmail."),
            new TestCase("TC12", "Register twice with the same email",
                    "First registration succeeds; second is rejected.")
    );

    private TestDocumentationGenerator() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected project directory and application version.");
        }

        Path projectDirectory = Path.of(args[0]);
        Path reportsDirectory = projectDirectory.resolve("target/surefire-reports");
        Map<String, TestResult> results = readTestResults(reportsDirectory);
        writeExecutionReport(projectDirectory, args[1], results);
        writeSummaryReport(projectDirectory, results);

        long failures = results.values().stream().filter(result -> result.status().equals("FAIL")).count();
        long notRun = results.values().stream().filter(result -> result.status().equals("NOT RUN")).count();
        if (failures > 0) {
            throw new IllegalStateException(
                    failures + " automated QA test case(s) failed. See test-documentation reports.");
        }
        if (notRun > 0) {
            throw new IllegalStateException(
                    notRun + " automated QA test case(s) were not run. See test-documentation reports.");
        }
    }

    private static Map<String, TestResult> readTestResults(Path reportsDirectory)
            throws IOException, ParserConfigurationException, SAXException {
        if (!Files.isDirectory(reportsDirectory)) {
            throw new IOException("Surefire report directory was not found: " + reportsDirectory);
        }

        Map<String, TestResult> results = new HashMap<>();
        try (var reportPaths = Files.list(reportsDirectory)) {
            List<Path> xmlReports = reportPaths
                    .filter(path -> path.getFileName().toString().startsWith("TEST-"))
                    .filter(path -> path.getFileName().toString().endsWith(".xml"))
                    .sorted(Comparator.naturalOrder())
                    .toList();
            if (xmlReports.isEmpty()) {
                throw new IOException("No Surefire XML test reports found in " + reportsDirectory);
            }

            for (Path xmlReport : xmlReports) {
                parseReport(xmlReport, results);
            }
        }
        return results;
    }

    private static void parseReport(Path reportPath, Map<String, TestResult> results)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        NodeList testCases = factory.newDocumentBuilder()
                .parse(reportPath.toFile())
                .getElementsByTagName("testcase");
        for (int index = 0; index < testCases.getLength(); index++) {
            Element testCase = (Element) testCases.item(index);
            Matcher idMatcher = TEST_CASE_ID.matcher(testCase.getAttribute("name"));
            if (!idMatcher.find()) {
                continue;
            }

            String id = "TC" + idMatcher.group(1);
            TestResult result = resultFrom(testCase);
            if (results.putIfAbsent(id, result) != null) {
                throw new IOException("More than one automated test is mapped to " + id);
            }
        }
    }

    private static TestResult resultFrom(Element testCase) {
        for (String outcome : List.of("failure", "error")) {
            NodeList entries = testCase.getElementsByTagName(outcome);
            if (entries.getLength() > 0) {
                Element entry = (Element) entries.item(0);
                String message = entry.getAttribute("message");
                if (message.isBlank()) {
                    message = entry.getTextContent().strip();
                }
                return new TestResult("FAIL", "Test failed: " + oneLine(message));
            }
        }
        if (testCase.getElementsByTagName("skipped").getLength() > 0) {
            return new TestResult("NOT RUN", "Test was skipped by JUnit.");
        }
        return new TestResult("PASS", null);
    }

    private static void writeExecutionReport(
            Path projectDirectory, String version, Map<String, TestResult> results) throws IOException {
        StringBuilder report = new StringBuilder("""
                # Test Execution Report

                This report is generated automatically during `mvn test` from Maven Surefire's JUnit XML results.

                ## Execution details

                """);
        report.append("- **Application version:** ").append(version).append('\n')
                .append("- **Execution time:** ")
                .append(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).append('\n')
                .append("- **Environment:** ")
                .append(System.getProperty("os.name")).append("; Java ")
                .append(System.getProperty("java.version")).append('\n')
                .append("- **Command:** `mvn test`\n")
                .append("- **Scope:** Automated registration-service tests; the console UI is not exercised.\n\n")
                .append("## Results\n\n")
                .append("| Test Case ID | Actual Result | Status (PASS/FAIL/NOT RUN) | Comments |\n")
                .append("|---|---|---|---|\n");

        for (TestCase testCase : TEST_CASES) {
            TestResult result = results.getOrDefault(
                    testCase.id(), new TestResult("NOT RUN", "No matching JUnit result was found."));
            String actualResult = result.status().equals("PASS")
                    ? testCase.passingResult()
                    : result.detail();
            report.append("| ").append(testCase.id())
                    .append(" | ").append(markdownCell(actualResult))
                    .append(" | ").append(result.status())
                    .append(" | ").append(result.status().equals("PASS")
                            ? "Automated JUnit test: " + testCase.scenario() + "."
                            : "See Maven Surefire output for details.")
                    .append(" |\n");
        }

        long passed = count(results, "PASS");
        long failed = count(results, "FAIL");
        long notRun = TEST_CASES.size() - passed - failed;
        report.append("\n**Automated run totals:** ")
                .append(TEST_CASES.size()).append(" cases; ")
                .append(passed).append(" passed; ")
                .append(failed).append(" failed; ")
                .append(notRun).append(" not run.\n");
        writeReport(projectDirectory, "test-execution-report.md", report.toString());
    }

    private static void writeSummaryReport(Path projectDirectory, Map<String, TestResult> results)
            throws IOException {
        long passed = count(results, "PASS");
        long failed = count(results, "FAIL");
        long notRun = TEST_CASES.size() - passed - failed;
        String overallResult = failed > 0 || notRun > 0 ? "FAIL" : "PASS";
        StringBuilder report = new StringBuilder("""
                # Test Summary Report

                This report is generated automatically during `mvn test` from Maven Surefire's JUnit XML results. It summarizes the automated registration-service tests; it is not a manual or console UI test.

                ## Execution totals

                """);
        report.append("- **Automated cases:** ").append(TEST_CASES.size()).append('\n')
                .append("- **Passed:** ").append(passed).append('\n')
                .append("- **Failed:** ").append(failed).append('\n')
                .append("- **Not run:** ").append(notRun).append('\n')
                .append("- **Overall result:** **").append(overallResult).append("**\n\n")
                .append("## Test cases\n\n");

        for (TestCase testCase : TEST_CASES) {
            TestResult result = results.getOrDefault(
                    testCase.id(), new TestResult("NOT RUN", "No matching JUnit result was found."));
            report.append("- **").append(testCase.id()).append(" — ")
                    .append(testCase.scenario()).append(":** ")
                    .append(result.status());
            if (!result.status().equals("PASS")) {
                report.append(" — ").append(markdownCell(result.detail()));
            }
            report.append('\n');
        }

        report.append("\n## Defects\n\n");
        if (failed == 0) {
            report.append("No automated test failures were reported in this run. A passing test run does not establish that no defects exist.\n");
        } else {
            report.append("Automated tests failed. Investigate the failure details before classifying them as application defects; no bug reports are created automatically.\n");
        }
        report.append("\n## Detailed results\n\n")
                .append("Maven Surefire XML and text reports are available under `target/surefire-reports/`.\n");
        writeReport(projectDirectory, "test-summary-report.md", report.toString());
    }

    private static long count(Map<String, TestResult> results, String status) {
        return results.values().stream().filter(result -> result.status().equals(status)).count();
    }

    private static String markdownCell(String value) {
        return value.replace("|", "\\|").replace("\r", " ").replace("\n", " ");
    }

    private static String oneLine(String value) {
        return value.replace("\r", " ").replace("\n", " ").strip();
    }

    private static void writeReport(Path projectDirectory, String fileName, String contents)
            throws IOException {
        Path report = projectDirectory.resolve("test-documentation").resolve(fileName);
        Files.writeString(report, contents);
    }

    private record TestCase(String id, String scenario, String passingResult) {
    }

    private record TestResult(String status, String detail) {
    }
}
