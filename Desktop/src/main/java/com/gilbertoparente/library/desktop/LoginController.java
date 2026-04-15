package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository;
import com.gilbertoparente.library.services.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSession userSession;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

        emailField.textProperty().addListener((obs, old, newValue) -> errorLabel.setText(""));
        passwordField.textProperty().addListener((obs, old, newValue) -> errorLabel.setText(""));
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Preencha todos os campos.");
            return;
        }
        errorLabel.setText("A autenticar...");

        try {
            Optional<EntityUsers> userOpt = userRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                EntityUsers user = userOpt.get();

                if (encoder.matches(password, user.getPassword())) {

                    if (Boolean.TRUE.equals(user.getIsAdmin())) {
                        userSession.setLoggedUser(user);

                        Stage stage = (Stage) emailField.getScene().getWindow();
                        MainApp.showDashboardView(stage);

                        System.out.println("Login efetuado com sucesso: " + user.getName());
                    } else {
                        showError("Acesso restrito a administradores.");
                    }
                } else {
                    showError("Email ou Password incorretos.");
                }
            } else {
                showError("Email ou Password incorretos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro de ligação ao servidor.");
        }
    }

    private void showError(String message) {
        if (!errorLabel.getStyleClass().contains("label-erro-login")) {
            errorLabel.getStyleClass().add("label-erro-login");
        }

        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    @FXML
    private void handleExit() {
        Platform.exit();
    }
}