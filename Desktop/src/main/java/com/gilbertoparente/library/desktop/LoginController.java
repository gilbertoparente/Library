package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.IUserRepository;
import com.gilbertoparente.library.repositories.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    // O Spring injeta o repositório automaticamente aqui
    @Autowired
     private IUserRepository userRepository;

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

        try {
            // O Spring Data JPA trata da ligação à BD
            EntityUsers user = userRepository.findByEmail(email);

            if (user != null) {
                // Compara a password (Dica: no futuro usa BCrypt, mas para o projeto assim chega)
                if (user.getPassword().equals(password)) {
                    if (Boolean.TRUE.equals(user.getAdmin())) {
                        Stage stage = (Stage) emailField.getScene().getWindow();
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
            e.printStackTrace(); // Ajuda a ver erros de BD no console
            errorLabel.setText("Erro de ligação à base de dados.");
        }
    }
}