# Test Execution Report

This report is generated automatically during `mvn test` from Maven Surefire's JUnit XML results.

## Execution details

- **Application version:** 1.0.0
- **Execution time:** 2026-10-30T13:36:08.494665285+02:00
- **Environment:** Linux; Java 21.0.12.1
- **Command:** `mvn test`
- **Scope:** Automated registration-service tests; the console UI is not exercised.

## Results

| Test Case ID | Actual Result | Status (PASS/FAIL/NOT RUN) | Comments |
|---|---|---|---|
| TC01 | Registration succeeds and the student is saved. | PASS | Automated JUnit test: Register with valid details. |
| TC02 | All four required-field errors are returned. | PASS | Automated JUnit test: Submit an empty form. |
| TC03 | Invalid-email error is returned for john123. | PASS | Automated JUnit test: Enter an invalid email. |
| TC04 | Password minimum-length error is returned for pass12. | PASS | Automated JUnit test: Enter a short password. |
| TC05 | Password-mismatch error is returned. | PASS | Automated JUnit test: Enter mismatched passwords. |
| TC06 | Duplicate email is rejected regardless of case. | PASS | Automated JUnit test: Register with a duplicate email. |
| TC07 | Name-required error is returned. | PASS | Automated JUnit test: Leave the name empty. |
| TC08 | Email-required error is returned. | PASS | Automated JUnit test: Leave the email empty. |
| TC09 | The eight-character password is accepted. | PASS | Automated JUnit test: Use exactly eight password characters. |
| TC10 | Email is accepted and normalized to lowercase. | PASS | Automated JUnit test: Use an uppercase email. |
| TC11 | Invalid-email error is returned for john@gmail. | PASS | Automated JUnit test: Enter an email without a domain extension. |
| TC12 | First registration succeeds; second is rejected. | PASS | Automated JUnit test: Register twice with the same email. |

**Automated run totals:** 12 cases; 12 passed; 0 failed; 0 not run.
