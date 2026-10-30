# Registration Test Cases

These 12 scenarios are automated as JUnit tests mapped by their TC IDs in `StudentRegistrationServiceTest`. Run them with `mvn test`; no manual data entry is needed. The tests call the registration service directly, so they verify validation behavior and returned errors, not the presentation or keyboard handling of the console.

| Test ID | Test Scenario | Test Data / Preconditions | Automated check | Expected Result |
|---|---|---|---|---|
| TC01 | Register with valid details | John Doe; `john@gmail.com`; `Password123`; confirmation `Password123` | Call registration and verify the returned student and repository entry. | Registration succeeds. |
| TC02 | Submit empty form | All fields empty | Call registration with all four fields blank and compare the complete error list. | Name, email, password, and password-confirmation required errors are returned. |
| TC03 | Enter invalid email | Name `John Doe`; email `john123`; matching valid passwords | Call registration and compare the validation error. | Invalid-email error is returned; registration is rejected. |
| TC04 | Enter short password | Name `John Doe`; email `short@example.com`; password and confirmation `pass12` | Call registration and compare the validation error. | Password minimum-length error is returned; registration is rejected. |
| TC05 | Passwords do not match | Name `John Doe`; email `mismatch@example.com`; password `Password123`; confirmation `Password456` | Call registration and compare the validation error. | Password-mismatch error is returned; registration is rejected. |
| TC06 | Register with duplicate email | First register `existing@example.com`, then use `EXISTING@EXAMPLE.COM` with otherwise valid details. | Register twice using email values that differ only by case. | The second registration is rejected with a duplicate-email error. |
| TC07 | Leave name empty | Name empty; email `noname@example.com`; password and confirmation `Password123` | Call registration with a whitespace-only name. | Name-required error is returned; registration is rejected. |
| TC08 | Leave email empty | Name `John Doe`; email empty; password and confirmation `Password123` | Call registration with a whitespace-only email. | Email-required error is returned; registration is rejected. |
| TC09 | Use exactly 8 password characters | Name `John Doe`; email `boundary@example.com`; password and confirmation `Pass1234` | Call registration with the eight-character password. | Registration succeeds. |
| TC10 | Use valid details with uppercase email | Name `John Doe`; email `JOHN@GMAIL.COM`; password and confirmation `Password123` | Call registration and verify the returned canonical email. | Registration succeeds and email is normalized to lowercase. |
| TC11 | Enter email without domain extension | Name `John Doe`; email `john@gmail`; password and confirmation `Password123` | Call registration and compare the validation error. | Invalid-email error is returned; registration is rejected. |
| TC12 | Register twice with the same email | Use the same otherwise-valid email for both attempts. | Register once, then attempt a second registration with that exact email. | First registration succeeds; second returns a duplicate-email error. |

## Notes

- Each test gets a fresh in-memory repository, so scenarios do not depend on execution order or previous runs.
- The Java tests compare exact error messages, including checking that empty-form validation returns all required-field errors and no unexpected errors.
- The automated tests verify the service layer. They do not simulate manual console interaction.
