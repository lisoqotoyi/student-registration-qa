# Test Summary Report

This report is generated automatically during `mvn test` from Maven Surefire's JUnit XML results. It summarizes the automated registration-service tests; it is not a manual or console UI test.

## Execution totals

- **Automated cases:** 12
- **Passed:** 12
- **Failed:** 0
- **Not run:** 0
- **Overall result:** **PASS**

## Test cases

- **TC01 — Register with valid details:** PASS
- **TC02 — Submit an empty form:** PASS
- **TC03 — Enter an invalid email:** PASS
- **TC04 — Enter a short password:** PASS
- **TC05 — Enter mismatched passwords:** PASS
- **TC06 — Register with a duplicate email:** PASS
- **TC07 — Leave the name empty:** PASS
- **TC08 — Leave the email empty:** PASS
- **TC09 — Use exactly eight password characters:** PASS
- **TC10 — Use an uppercase email:** PASS
- **TC11 — Enter an email without a domain extension:** PASS
- **TC12 — Register twice with the same email:** PASS

## Defects

No automated test failures were reported in this run. A passing test run does not establish that no defects exist.

## Detailed results

Maven Surefire XML and text reports are available under `target/surefire-reports/`.
WTC-BF5Z4PT3