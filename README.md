# Student Registration System — QA Practice Project

A small Java 17 console application for practicing functional, negative, validation, and boundary-value testing. Registration rules are kept in a service separate from console input/output, and automated unit tests cover the core validation logic.

## Requirements

- Java 17 or newer
- Maven 3.8 or newer

## Run automated tests and refresh QA reports

```bash
mvn test
```

Every `mvn test` run automatically regenerates `test-documentation/test-execution-report.md` and `test-documentation/test-summary-report.md` from the JUnit results. The build remains failed if any mapped test fails or is not run; the reports are written before Maven reports that failure. Detailed Surefire text and XML reports are also available under `target/surefire-reports/`.

## Run the application

```bash
mvn compile exec:java
```

Enter a full name, email, password, and matching password confirmation when prompted. The application prints a success message or one or more validation errors. It then asks whether you want to register another student, which allows duplicate-email scenarios to be tested in one run. Enter `y` to continue or another response to exit.

Registrations are held in memory and are cleared when the application exits. Email addresses are treated case-insensitively. Passwords are checked for length and confirmation but are not stored.

## Project structure

```text
src/
  main/java/com/example/registration/   Application and registration logic
  test/java/com/example/registration/   Automated unit tests
test-documentation/
  test-plan.md
  test-cases.md
  bug-report-template.md
  test-execution-report.md
  test-summary-report.md
pom.xml
```

## QA documentation

The 12 QA scenarios are automated, so you do not need to enter data by hand to run them. Start with the [test plan](test-documentation/test-plan.md) and [test cases](test-documentation/test-cases.md), then run `mvn test`. The [test execution report](test-documentation/test-execution-report.md) contains the latest results, and Maven writes detailed results to `target/surefire-reports/`. The [test summary report](test-documentation/test-summary-report.md) summarizes the run; use the [bug report template](test-documentation/bug-report-template.md) only if a test reveals a reproducible defect.

These automated tests cover registration business rules through the service layer; they do not simulate the console UI.
