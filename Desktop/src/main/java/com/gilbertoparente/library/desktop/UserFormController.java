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
    @FXML private TextField passwordTextField;
    @FXML private ToggleButton btnShowPassword;
    @FXML private CheckBox adminCheckBox;
    @FXML private Label errorLabel;

    private EntityUsers currentUser;

    @FXML
    public void initialize() {
        // Sincroniza os dois campos de password
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void togglePassword() {
        if (btnShowPassword.isSelected()) {
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
            btnShowPassword.setText("🙈");
        } else {
            passwordTextField.setVisible(false);
            passwordField.setVisible(true);
            btnShowPassword.setText("👁");
        }
    }

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

            // Lógica de encriptação da password
            if (currentUser.getIdUser() == 0) {
                currentUser.setPassword(encoder.encode(plainPassword));
            } else if (plainPassword != null && !plainPassword.trim().isEmpty()) {
                currentUser.setPassword(encoder.encode(plainPassword));
            }

            userService.save(currentUser);
            closeWindow();
        } catch (Exception e) {
            emailField.getStyleClass().add("input-error");
            showAlert("Erro", "Não foi possível gravar. Verifique se o e-mail já existe.", Alert.AlertType.ERROR);
        }
    }

    private boolean isInputValid() {
        resetStyles();
        errorLabel.setText("");
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        // 1. Validação de Nome (Sem caracteres especiais)
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        if (name.isEmpty() || !name.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            nameField.getStyleClass().add("input-error");
            errorMsg.append("- Nome inválido (use apenas letras).\n");
            isValid = false;
        }

        // 2. Validação de Email
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            emailField.getStyleClass().add("input-error");
            errorMsg.append("- Formato de e-mail inválido.\n");
            isValid = false;
        }

        // 3. Validação de Password (Mínimo 5 caracteres)
        String password = passwordField.getText();
        if (currentUser.getIdUser() == 0) { // Novo Utilizador
            if (password == null || password.length() < 5) {
                passwordField.getStyleClass().add("input-error");
                passwordTextField.getStyleClass().add("input-error");
                errorMsg.append("- Password deve ter pelo menos 5 caracteres.\n");
                isValid = false;
            }
        } else if (password != null && !password.isEmpty() && password.length() < 5) { // Edição
            passwordField.getStyleClass().add("input-error");
            passwordTextField.getStyleClass().add("input-error");
            errorMsg.append("- A nova password deve ter pelo menos 5 caracteres.\n");
            isValid = false;
        }

        if (!isValid) {
            errorLabel.setText(errorMsg.toString());
        }

        return isValid;
    }

    private void resetStyles() {
        nameField.getStyleClass().remove("input-error");
        emailField.getStyleClass().remove("input-error");
        passwordField.getStyleClass().remove("input-error");
        passwordTextField.getStyleClass().remove("input-error");
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        adminCheckBox.setSelected(false);
    }

    private void closeWindow() {
        if (nameField.getScene() != null) {
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}