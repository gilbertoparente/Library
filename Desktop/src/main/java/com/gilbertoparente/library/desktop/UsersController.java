package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.services.UserService;
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

import java.util.List;
import java.util.Optional;

@Component
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @FXML private TableView<EntityUsers> usersTable;
    @FXML private TableColumn<EntityUsers, String> nameColumn;
    @FXML private TableColumn<EntityUsers, String> emailColumn;
    @FXML private TableColumn<EntityUsers, String> passwordColumn;
    @FXML private TableColumn<EntityUsers, Boolean> adminColumn;

    @FXML
    private TextField txtSearch;


    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        adminColumn.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

        passwordColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : "••••••••");
                setStyle("-fx-alignment: CENTER;");
            }
        });

        adminColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean isAdmin, boolean empty) {
                super.updateItem(isAdmin, empty);

                // 1. Limpeza de classes e texto
                getStyleClass().removeAll("user-admin", "user-comum");
                setText(null);
                setGraphic(null);

                if (!empty && isAdmin != null) {
                    if (isAdmin) {
                        setText("ADMIN");
                        getStyleClass().add("user-admin");
                    } else {
                        setText("UTILIZADOR");
                        getStyleClass().add("user-comum");
                    }
                }
            }
        });

        refreshUsers();

        if (txtSearch != null){
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                List<EntityUsers> resultados = userService.searchByname(newValue);
                usersTable.setItems(FXCollections.observableArrayList(resultados));
            });
        }
    }

    @FXML
    private void handleSearch(){
        String termo = txtSearch.getText();
        List<EntityUsers> resultado = userService.searchByname(termo);
        usersTable.setItems(FXCollections.observableArrayList(resultado));
    }

    @FXML
    private void handleClearSearch() {

        txtSearch.clear();
        refreshUsers();
    }


    @FXML
    public void refreshUsers() {
        try {
            List<EntityUsers> users = userService.findAll();
            usersTable.setItems(FXCollections.observableArrayList(users));
            usersTable.refresh();
            System.out.println("Tabela atualizada com " + users.size() + " utilizadores.");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            showAlert("Seleção Necessária", "Selecione um utilizador na tabela para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDeleteUser() {
        EntityUsers selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Eliminação");
            confirm.setHeaderText("Eliminar utilizador: " + selected.getName() + "?");
            confirm.setContentText("Esta ação não pode ser desfeita.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    userService.deleteUser(selected.getIdUser());
                    refreshUsers();
                } catch (Exception e) {
                    showAlert("Erro ao Eliminar", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }

    private void showUserForm(EntityUsers user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setUserData(user);

            Stage stage = new Stage();
            stage.setTitle(user == null ? "Registar Utilizador" : "Editar Perfil");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(usersTable.getScene().getWindow());
            stage.setScene(new Scene(root));

            stage.showAndWait();
            refreshUsers();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro de Interface", "Não foi possível abrir o formulário.", Alert.AlertType.ERROR);
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