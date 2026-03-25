package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.desktop.UserFormController;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @FXML private TableView<EntityUsers> usersTable;
    @FXML private TableColumn<EntityUsers, Integer> idColumn;
    @FXML private TableColumn<EntityUsers, String> nameColumn;
    @FXML private TableColumn<EntityUsers, String> emailColumn;
    @FXML private TableColumn<EntityUsers, String> passwordColumn;
    @FXML private TableColumn<EntityUsers, Boolean> adminColumn;

    @FXML
    public void initialize() {
        // 1. Colunas simples (Texto direto)
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // 2. Coluna Password (IMPORTANTE: Adicionei a ValueFactory aqui)
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordColumn.setCellFactory(column -> new TableCell<EntityUsers, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Como já temos o 'item' vindo da ValueFactory, usamos o tamanho dele
                    setText("•".repeat(item.length()));
                }
            }
        });

        // 3. Coluna Administrador
        adminColumn.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));
        adminColumn.setCellFactory(column -> new TableCell<EntityUsers, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Sim" : "Não");
                    // Define a cor: Verde para Sim, Cinza para Não
                    String color = item ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;" : "-fx-text-fill: #7f8c8d;";
                    setStyle(color + "-fx-alignment: CENTER;");
                }
            }
        });

        refreshUsers();
    }
    @FXML
    private void refreshUsers() {
        usersTable.setItems(FXCollections.observableArrayList(userRepository.findAll()));
    }

    @FXML
    private void handleAddUser() {
        showUserForm(null);
    }

    @FXML
    private void handleEditUser() {
        EntityUsers selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showUserForm(selected);
        } else {
            showAlert("Seleção Necessária", "Selecione um utilizador para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDeleteUser() {
        EntityUsers selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar");
            confirm.setHeaderText("Eliminar " + selected.getName() + "?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                userRepository.delete(selected);
                refreshUsers();
            }
        }
    }

    private void showUserForm(EntityUsers user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // LIGAÇÃO CRUCIAL AQUI:
            UserFormController controller = loader.getController();
            controller.setUserData(user); // Este método existe no teu UserFormController

            Stage stage = new Stage();
            stage.setTitle(user == null ? "Novo Utilizador" : "Editar Utilizador");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(usersTable.getScene().getWindow());
            stage.setScene(new Scene(root));

            stage.showAndWait();
            refreshUsers(); // Atualiza a tabela quando a janela fecha

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao carregar formulário: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}