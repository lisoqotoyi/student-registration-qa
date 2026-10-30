package com.example.registration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class StudentRegistrationService {
    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+"
                    + "(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
                    + "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?"
                    + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$");

    private final StudentRepository studentRepository;

    public StudentRegistrationService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student register(
            String fullName, String email, String password, String passwordConfirmation)
            throws RegistrationValidationException {
        List<String> errors = new ArrayList<>();

        String normalizedName = fullName == null ? "" : fullName.trim();
        String normalizedEmail = email == null ? "" : email.trim();
        String providedPassword = password == null ? "" : password;
        String confirmation = passwordConfirmation == null ? "" : passwordConfirmation;

        if (normalizedName.isEmpty()) {
            errors.add("Full name is required.");
        }
        if (normalizedEmail.isEmpty()) {
            errors.add("Email is required.");
        } else if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            errors.add("Please enter a valid email address.");
        }
        if (providedPassword.isEmpty()) {
            errors.add("Password is required.");
        } else if (providedPassword.length() < MINIMUM_PASSWORD_LENGTH) {
            errors.add("Password must contain at least 8 characters.");
        }
        if (confirmation.isEmpty()) {
            errors.add("Password confirmation is required.");
        } else if (!providedPassword.equals(confirmation)) {
            errors.add("Passwords do not match.");
        }

        if (!normalizedEmail.isEmpty()
                && EMAIL_PATTERN.matcher(normalizedEmail).matches()
                && studentRepository.existsByEmail(normalizedEmail)) {
            errors.add("An account with this email address already exists.");
        }

        if (!errors.isEmpty()) {
            throw new RegistrationValidationException(errors);
        }

        String canonicalEmail = normalizedEmail.toLowerCase(Locale.ROOT);
        Student student = new Student(normalizedName, canonicalEmail);
        studentRepository.save(student);
        return student;
    }
}
