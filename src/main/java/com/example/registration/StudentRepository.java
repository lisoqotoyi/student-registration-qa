package com.example.registration;

public interface StudentRepository {
    boolean existsByEmail(String email);

    void save(Student student);
}
