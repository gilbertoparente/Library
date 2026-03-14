package com.gilbertoparente.library;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.GenericRepository;

import java.util.List;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        GenericRepository<EntityUsers> userRepo = new GenericRepository<>(EntityUsers.class);

        int option = -1;

        while (option != 0) {
            System.out.println("\n===== USER MANAGEMENT =====");
            System.out.println("1 - Insert user");
            System.out.println("2 - List users");
            System.out.println("3 - Update user");
            System.out.println("4 - Delete user");
            System.out.println("0 - Exit");
            System.out.print("Choose an option: ");

            option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {

                // INSERT
                case 1:
                    EntityUsers newUser = new EntityUsers();
                    System.out.print("Name: ");
                    newUser.setName(scanner.nextLine());
                    System.out.print("Email: ");
                    newUser.setEmail(scanner.nextLine());
                    System.out.print("Password: ");
                    newUser.setPassword(scanner.nextLine());
                    System.out.print("Is admin (true/false): ");
                    newUser.setAdmin(scanner.nextBoolean());
                    scanner.nextLine();

                    userRepo.save(newUser);
                    System.out.println("User inserted!");
                    break;

                // LIST
                case 2:
                    List<EntityUsers> users = userRepo.findAll();
                    System.out.println("\n------ USERS ------");
                    for (EntityUsers u : users) {
                        System.out.println("ID: " + u.getIdUser() +
                                " | Name: " + u.getName() +
                                " | Email: " + u.getEmail() +
                                " | Admin: " + u.getAdmin());
                    }
                    break;

                // UPDATE
                case 3:
                    System.out.print("Enter user ID to update: ");
                    int idUpdate = scanner.nextInt();
                    scanner.nextLine();
                    EntityUsers userUpdate = userRepo.findById(idUpdate);
                    if (userUpdate == null) {
                        System.out.println("User not found.");
                        break;
                    }

                    System.out.println("Update field: 1-Name 2-Email 3-Password 4-IsAdmin");
                    int field = scanner.nextInt();
                    scanner.nextLine();

                    switch (field) {
                        case 1:
                            System.out.print("New name: ");
                            userUpdate.setName(scanner.nextLine());
                            break;
                        case 2:
                            System.out.print("New email: ");
                            userUpdate.setEmail(scanner.nextLine());
                            break;
                        case 3:
                            System.out.print("New password: ");
                            userUpdate.setPassword(scanner.nextLine());
                            break;
                        case 4:
                            System.out.print("Is admin (true/false): ");
                            userUpdate.setAdmin(scanner.nextBoolean());
                            scanner.nextLine();
                            break;
                        default:
                            System.out.println("Invalid option.");
                            break;
                    }

                    userRepo.update(userUpdate);
                    System.out.println("User updated!");
                    break;

                // DELETE
                case 4:
                    System.out.print("Enter user ID to delete: ");
                    int idDelete = scanner.nextInt();
                    scanner.nextLine();
                    EntityUsers userDelete = userRepo.findById(idDelete);
                    if (userDelete != null) {
                        userRepo.delete(userDelete);
                        System.out.println("User deleted!");
                    } else {
                        System.out.println("User not found.");
                    }
                    break;

                case 0:
                    System.out.println("Exiting program.");
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }
}