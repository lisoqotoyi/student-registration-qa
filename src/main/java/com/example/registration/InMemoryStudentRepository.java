package com.example.registration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InMemoryStudentRepository implements StudentRepository {
    private final Map<String, Student> studentsByEmail = new HashMap<>();

    @Override
    public boolean existsByEmail(String email) {
        return studentsByEmail.containsKey(normalizeEmail(email));
    }

    @Override
    public void save(Student student) {
        studentsByEmail.put(normalizeEmail(student.email()), student);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
