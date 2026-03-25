package com.gilbertoparente.library;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class ScientificLibraryApplication {

    public static void main(String[] args) {
        // Inicia o contexto do Spring e Hibernate
        SpringApplication.run(ScientificLibraryApplication.class, args);
    }

    //@Bean
//    public CommandLineRunner menuConsole(UserService userService) {
//        return args -> {
//            Scanner scanner = new Scanner(System.in);
//            int option = -1;
//
//            while (option != 0) {
//                System.out.println("\n===== USER MANAGEMENT (SPRING DATA) =====");
//                System.out.println("1 - Insert user");
//                System.out.println("2 - List users");
//                System.out.println("3 - Update user");
//                System.out.println("4 - Delete user");
//                System.out.println("0 - Exit");
//                System.out.print("Choose an option: ");
//
//                try {
//                    option = Integer.parseInt(scanner.nextLine());
//                } catch (NumberFormatException e) {
//                    continue;
//                }
//
//                switch (option) {
//                    // INSERT
//                    case 1:
//                        EntityUsers newUser = new EntityUsers();
//                        System.out.print("Name: ");
//                        newUser.setName(scanner.nextLine());
//                        System.out.print("Email: ");
//                        newUser.setEmail(scanner.nextLine());
//                        System.out.print("Password: ");
//                        newUser.setPassword(scanner.nextLine());
//                        System.out.print("Is admin (true/false): ");
//                        newUser.setAdmin(Boolean.parseBoolean(scanner.nextLine()));
//
//                        userService.save(newUser); // Usamos o Service!
//                        System.out.println("User inserted via Service!");
//                        break;
//
//                    // LIST
//                    case 2:
//                        List<EntityUsers> users = userService.findAll();
//                        System.out.println("\n------ REGISTERED USERS ------");
//                        for (EntityUsers u : users) {
//                            System.out.println("ID: " + u.getIdUser() +
//                                    " | Name: " + u.getName() +
//                                    " | Email: " + u.getEmail() +
//                                    " | Admin: " + u.getAdmin());
//                        }
//                        break;
//
//                    // UPDATE
//                    case 3:
//                        System.out.print("Enter user ID to update: ");
//                        int idUpdate = Integer.parseInt(scanner.nextLine());
//                        // O Service deve ter um método findById
//                        EntityUsers userUpdate = userService.getUserById(idUpdate);
//
//                        if (userUpdate == null) {
//                            System.out.println("User not found.");
//                            break;
//                        }
//
//                        System.out.print("New name (current: " + userUpdate.getName() + "): ");
//                        userUpdate.setName(scanner.nextLine());
//
//                        userService.save(userUpdate);
//                        System.out.println("User updated!");
//                        break;
//
//                    // DELETE
//                    case 4:
//                        System.out.print("Enter user ID to delete: ");
//                        int idDelete = Integer.parseInt(scanner.nextLine());
//                        try {
//                            userService.deleteUser(idDelete);
//                            System.out.println("User deleted!");
//                        } catch (Exception e) {
//                            System.out.println("Error: " + e.getMessage());
//                        }
//                        break;
//
//                    case 0:
//                        System.out.println("Exiting program.");
//                        System.exit(0); // Fecha a aplicação Spring
//                        break;
//                }
//            }
//            scanner.close();
//        };
//    }
}