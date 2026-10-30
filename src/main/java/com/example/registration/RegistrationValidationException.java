package com.example.registration;

import java.util.List;

public class RegistrationValidationException extends Exception {
    private final List<String> errors;

    public RegistrationValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
