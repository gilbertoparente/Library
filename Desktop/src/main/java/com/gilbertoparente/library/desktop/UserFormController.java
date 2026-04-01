package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class UserFormController {

    @Autowired private UserService userService;
    @Autowired private BCryptPasswordEncoder encoder;
    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox adminCheckBox;

    private EntityUsers currentUser;

    public void setUserData(EntityUsers user) {
        this.currentUser = user;

        if (user != null) {
            titleLabel.setText("Editar Utilizador");
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            passwordField.setPromptText("Deixe em branco para não alterar");
            adminCheckBox.setSelected(Boolean.TRUE.equals(user.getIsAdmin()));
        } else {
            titleLabel.setText("Novo Utilizador");
            this.currentUser = new EntityUsers();
            clearFields();
            passwordField.setPromptText("Password obrigatória");
        }
    }

    @FXML
    private void handleSave() {
        if (!isInputValid()) return;

        try {
            currentUser.setName(nameField.getText().trim());
            currentUser.setEmail(emailField.getText().trim());
            currentUser.setIsAdmin(adminCheckBox.isSelected());


            String plainPassword = passwordField.getText();

            if (currentUser.getIdUser() == 0) {

                currentUser.setPassword(encoder.encode(plainPassword));
            } else if (plainPassword != null && !plainPassword.trim().isEmpty()) {
                currentUser.setPassword(encoder.encode(plainPassword));
            }


            userService.save(currentUser);
            closeWindow();
        } catch (Exception e) {
            showAlert("Erro ao Gravar", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty())
            errorMessage.append("Nome obrigatório!\n");

        if (emailField.getText() == null || !emailField.getText().contains("@"))
            errorMessage.append("Email inválido!\n");

        if (currentUser.getIdUser() == 0 && (passwordField.getText() == null || passwordField.getText().isEmpty())) {
            errorMessage.append("Password obrigatória para novos utilizadores!\n");
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showAlert("Dados Inválidos", errorMessage.toString(), Alert.AlertType.WARNING);
            return false;
        }
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        adminCheckBox.setSelected(false);
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}