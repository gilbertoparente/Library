package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFormController {

    @Autowired
    private UserRepository userRepository;

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox adminCheckBox;

    private EntityUsers currentUser; // Guardamos o utilizador que estamos a editar

    /**
     * Este método é chamado pelo UsersController antes de mostrar a janela
     */
    public void setUserData(EntityUsers user) {
        this.currentUser = user;

        if (user != null) {
            titleLabel.setText("Editar Utilizador");
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword());
            adminCheckBox.setSelected(user.getIsAdmin() != null && user.getIsAdmin());
        } else {
            titleLabel.setText("Novo Utilizador");
            clearFields();
        }
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            if (currentUser == null) {
                currentUser = new EntityUsers();
            }

            currentUser.setName(nameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPassword(passwordField.getText());
            currentUser.setIsAdmin(adminCheckBox.isSelected());

            try {
                userRepository.save(currentUser); // Grava ou Atualiza no Postgres
                closeWindow();
            } catch (Exception e) {
                showAlert("Erro ao Gravar", "Não foi possível guardar o utilizador: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().isEmpty()) errorMessage += "Nome inválido!\n";
        if (emailField.getText() == null || emailField.getText().isEmpty()) errorMessage += "Email inválido!\n";
        if (passwordField.getText() == null || passwordField.getText().isEmpty()) errorMessage += "Password obrigatória!\n";

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Campos Inválidos", errorMessage, Alert.AlertType.ERROR);
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