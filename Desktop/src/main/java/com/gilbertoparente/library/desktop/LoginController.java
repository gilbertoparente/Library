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

        // Reset de estilos e mensagens
        emailField.getStyleClass().remove("input-error");
        passwordField.getStyleClass().remove("input-error");
        errorLabel.getStyleClass().remove("label-erro");
        errorLabel.getStyleClass().remove("label-info");
        errorLabel.setText("");

        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) emailField.getStyleClass().add("input-error");
            if (password.isEmpty()) passwordField.getStyleClass().add("input-error");
            showError("Preencha todos os campos.");
            return;
        }

        // Estilo de processamento (Informativo)
        errorLabel.setText("A autenticar...");
        errorLabel.getStyleClass().add("label-info");

        try {
            Optional<EntityUsers> userOpt = userRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                EntityUsers user = userOpt.get();
                if (encoder.matches(password, user.getPassword())) {
                    if (Boolean.TRUE.equals(user.getIsAdmin())) {
                        userSession.setLoggedUser(user);
                        Stage stage = (Stage) emailField.getScene().getWindow();
                        MainApp.showDashboardView(stage);
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
        errorLabel.setText(message);
        errorLabel.getStyleClass().remove("label-info");
        errorLabel.getStyleClass().add("label-erro");
    }


    @FXML
    private void handleExit() {
        Platform.exit();
    }
}