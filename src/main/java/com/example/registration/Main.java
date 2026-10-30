package com.example.registration;

import java.util.Scanner;

public class Main {
    private Main() {
    }

    public static void main(String[] args) {
        StudentRegistrationService registrationService =
                new StudentRegistrationService(new InMemoryStudentRepository());

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Student Registration System");

            boolean registerAnother = true;
            while (registerAnother) {
                registerStudent(scanner, registrationService);
                registerAnother = askToRegisterAnother(scanner);
            }

            System.out.println("Goodbye.");
        }
    }

    private static void registerStudent(
            Scanner scanner, StudentRegistrationService registrationService) {
        System.out.println("\nEnter your registration details:");
        String fullName = prompt(scanner, "Full name: ");
        String email = prompt(scanner, "Email address: ");
        String password = prompt(scanner, "Password: ");
        String passwordConfirmation = prompt(scanner, "Confirm password: ");

        if (fullName == null || email == null || password == null || passwordConfirmation == null) {
            return;
        }

        try {
            Student student = registrationService.register(
                    fullName, email, password, passwordConfirmation);
            System.out.println("Registration successful for " + student.fullName() + ".");
        } catch (RegistrationValidationException exception) {
            System.out.println("Registration failed:");
            exception.getErrors().forEach(error -> System.out.println("- " + error));
        }
    }

    private static String prompt(Scanner scanner, String label) {
        System.out.print(label);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    private static boolean askToRegisterAnother(Scanner scanner) {
        String answer = prompt(scanner, "\nRegister another student? (y/n): ");
        return answer != null && answer.trim().equalsIgnoreCase("y");
    }
}
