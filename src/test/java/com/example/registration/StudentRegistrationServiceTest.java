package com.example.registration;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentRegistrationServiceTest {
    private InMemoryStudentRepository repository;
    private StudentRegistrationService registrationService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryStudentRepository();
        registrationService = new StudentRegistrationService(repository);
    }

    @Test
    @DisplayName("TC01 - register with valid details")
    void tc01RegistersWithValidDetails() throws RegistrationValidationException {
        Student student = registrationService.register(
                "John Doe", "john@gmail.com", "Password123", "Password123");

        assertEquals("John Doe", student.fullName());
        assertEquals("john@gmail.com", student.email());
        assertTrue(repository.existsByEmail("john@gmail.com"));
    }

    @Test
    @DisplayName("TC02 - reject an empty form with every required-field error")
    void tc02RejectsAnEmptyFormWithAllRequiredFieldErrors() {
        RegistrationValidationException exception = assertThrows(
                RegistrationValidationException.class,
                () -> registrationService.register("", "", "", ""));

        assertEquals(List.of(
                "Full name is required.",
                "Email is required.",
                "Password is required.",
                "Password confirmation is required."
        ), exception.getErrors());
    }

    @Test
    @DisplayName("TC03 - reject an invalid email")
    void tc03RejectsAnInvalidEmail() {
        assertValidationErrors(
                "john123",
                "Password123",
                "Password123",
                "Please enter a valid email address.");
    }

    @Test
    @DisplayName("TC04 - reject a password shorter than eight characters")
    void tc04RejectsAShortPassword() {
        assertValidationErrors(
                "short@example.com",
                "pass12",
                "pass12",
                "Password must contain at least 8 characters.");
    }

    @Test
    @DisplayName("TC05 - reject passwords that do not match")
    void tc05RejectsMismatchedPasswords() {
        assertValidationErrors(
                "mismatch@example.com",
                "Password123",
                "Password456",
                "Passwords do not match.");
    }

    @Test
    @DisplayName("TC06 - reject a duplicate email address regardless of case")
    void tc06RejectsDuplicateEmailRegardlessOfCase() throws RegistrationValidationException {
        registrationService.register(
                "John Doe", "existing@example.com", "Password123", "Password123");

        RegistrationValidationException exception = assertThrows(
                RegistrationValidationException.class,
                () -> registrationService.register(
                        "Jane Doe", "EXISTING@EXAMPLE.COM", "Password456", "Password456"));

        assertEquals(List.of("An account with this email address already exists."),
                exception.getErrors());
    }

    @Test
    @DisplayName("TC07 - reject a missing name")
    void tc07RejectsAnEmptyName() {
        RegistrationValidationException exception = assertThrows(
                RegistrationValidationException.class,
                () -> registrationService.register(
                        "  ", "noname@example.com", "Password123", "Password123"));

        assertEquals(List.of("Full name is required."), exception.getErrors());
    }

    @Test
    @DisplayName("TC08 - reject a missing email")
    void tc08RejectsAnEmptyEmail() {
        RegistrationValidationException exception = assertThrows(
                RegistrationValidationException.class,
                () -> registrationService.register(
                        "John Doe", " ", "Password123", "Password123"));

        assertEquals(List.of("Email is required."), exception.getErrors());
    }

    @Test
    @DisplayName("TC09 - accept a password with exactly eight characters")
    void tc09AcceptsAPasswordWithExactlyEightCharacters()
            throws RegistrationValidationException {
        Student student = registrationService.register(
                "John Doe", "boundary@example.com", "Pass1234", "Pass1234");

        assertEquals("boundary@example.com", student.email());
    }

    @Test
    @DisplayName("TC10 - accept and normalize an uppercase email")
    void tc10AcceptsUppercaseEmail() throws RegistrationValidationException {
        Student student = registrationService.register(
                "John Doe", "JOHN@GMAIL.COM", "Password123", "Password123");

        assertEquals("john@gmail.com", student.email());
        assertTrue(repository.existsByEmail("john@gmail.com"));
    }

    @Test
    @DisplayName("TC11 - reject an email without a domain extension")
    void tc11RejectsEmailWithoutDomainExtension() {
        assertValidationErrors(
                "john@gmail",
                "Password123",
                "Password123",
                "Please enter a valid email address.");
    }

    @Test
    @DisplayName("TC12 - reject a second registration using the same email")
    void tc12RejectsSecondRegistrationUsingSameEmail() throws RegistrationValidationException {
        registrationService.register(
                "John Doe", "same@example.com", "Password123", "Password123");

        RegistrationValidationException exception = assertThrows(
                RegistrationValidationException.class,
                () -> registrationService.register(
                        "Jane Doe", "same@example.com", "Password456", "Password456"));

        assertEquals(List.of("An account with this email address already exists."),
                exception.getErrors());
    }

    private void assertValidationErrors(
            String email, String password, String confirmation, String expectedError) {
        RegistrationValidationException exception = assertThrows(
                RegistrationValidationException.class,
                () -> registrationService.register(
                        "John Doe", email, password, confirmation));

        assertEquals(List.of(expectedError), exception.getErrors());
    }
}
