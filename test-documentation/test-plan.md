# Test Plan

## Purpose

This plan describes how to check the Student Registration System's required registration behavior. It is intended for QA practice and supports functional, negative, validation, and boundary-value testing.

## Test objectives

- Confirm valid student details can be registered.
- Confirm required fields, email format, password length, and password confirmation are validated.
- Confirm an email address cannot be registered twice, including when its letter case differs.
- Check the minimum password-length boundary at exactly eight characters and below eight characters.
- Confirm the service returns understandable validation errors and success results.

## Scope

### In scope

- Full-name and email required-field validation.
- Email syntax validation, including a missing domain extension.
- Password and password-confirmation required fields.
- Minimum password length of eight characters.
- Password confirmation matching.
- Duplicate email rejection, case-insensitively.
- Automated execution of all 12 documented registration scenarios against the core service.
- Optional exploratory testing through the console application.

### Out of scope

- Database or file persistence; registrations are kept in memory only for the current run.
- Password authentication, password recovery, or password-storage security testing.
- Load, performance, accessibility, and deployment testing.

## Test approach

1. Run `mvn test` to execute all 12 cases in [test-cases.md](./test-cases.md).
2. Maven automatically refreshes [test-execution-report.md](./test-execution-report.md) and [test-summary-report.md](./test-summary-report.md) from Surefire results.
3. Check Maven's detailed reports under `target/surefire-reports/`.
4. If a test fails and confirms a reproducible defect, document it using [bug-report-template.md](./bug-report-template.md). Test failures do not create bug reports automatically.

No manual data entry is needed to run the documented test cases. The automated tests call the registration service directly; optional console testing can check the interactive presentation separately.

## Test environment

- Java 17 or newer.
- Maven 3.8 or newer.
- A terminal capable of running Maven.

## Entry criteria

- The project compiles successfully.
- The test environment has Java and Maven available.

## Exit criteria

- All 12 automated test cases complete with results recorded.
- Any failures have a corresponding bug report or an explanation in the comments.
- The test summary is completed with actual execution totals and outstanding issues.

## Risks and assumptions

- Each automated test uses a fresh in-memory repository, so case results are independent.
- Email format validation is a practical application check and does not verify that an address can receive email.
- Testers should not use real personal passwords or sensitive information.
