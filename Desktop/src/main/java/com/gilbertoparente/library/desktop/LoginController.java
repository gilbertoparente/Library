package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository; // Importa o novo Repo
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoginController {

    @Autowired
    private UserRepository userRepository; // Usa o repositório do Spring Data

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Preencha todos os campos.");
            return;
        }

        try {
            // O findByEmail agora devolve um Optional
            Optional<EntityUsers> userOpt = userRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                EntityUsers user = userOpt.get();

                // Verificação de Password
                if (user.getPassword().equals(password)) {

                    // Verificação de Admin
                    if (Boolean.TRUE.equals(user.getIsAdmin())) {
                        Stage stage = (Stage) emailField.getScene().getWindow();
                        // Certifica-se que o MainApp consegue carregar a próxima view via Spring
                        MainApp.showDashboardView(stage);
                    } else {
                        errorLabel.setText("Acesso restrito a administradores.");
                    }
                } else {
                    errorLabel.setText("Password incorreta.");
                }
            } else {
                errorLabel.setText("Utilizador não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erro crítico no sistema.");
        }
    }
}