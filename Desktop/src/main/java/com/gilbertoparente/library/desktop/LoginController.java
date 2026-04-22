package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository;
import com.gilbertoparente.library.services.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; // IMPORT CORRETO AQUI
import javafx.scene.Scene;
import javafx.scene.control.Alert; // ADICIONADO
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext; // ADICIONADO
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSession userSession;

    @Autowired
    private BCryptPasswordEncoder encoder;

    // Injetamos o contexto diretamente aqui para evitar erros de acesso ao MainApp
    @Autowired
    private ConfigurableApplicationContext springContext;

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

    @FXML
    private void handleOpenRegister(ActionEvent event) {
        try {
            // Nome corrigido para coincidir com o teu ficheiro real
            var resource = getClass().getResource("/com/gilbertoparente/library/desktop/user-form.fxml");

            if (resource == null) {
                showError("Erro: Ficheiro user-form.fxml não encontrado!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // Configura o controlador para modo "Novo Utilizador"
            UserFormController controller = loader.getController();
            controller.setUserData(null);

            Stage stage = new Stage();
            stage.setTitle("Registo de Novo Utilizador");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // Opcional: Garante que a janela não fica demasiado pequena
            stage.setMinWidth(400);
            stage.setMinHeight(500);

            stage.showAndWait();

            Platform.runLater(() -> emailField.requestFocus());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro de Sistema", "Falha ao carregar o formulário: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().remove("label-info");
        errorLabel.getStyleClass().add("label-erro");
    }

    // Adicionado o método que faltava
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }
}