package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityAuthors;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.services.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardController {

    @Autowired private PurchaseService purchaseService;
    @Autowired private CommentService commentService;
    @Autowired private UserService userService;
    @Autowired private AuthorService authorService;

    @FXML private Label lblPendingPurchases;
    @FXML private Label lblPendingComments;
    @FXML private Label lblTotalUsers;
    @FXML private Label lblPendingAuthors;

    @FXML private ListView<String> listRecentUsers;
    @FXML private TableView<EntityAuthors> tblPendingAuthors;
    @FXML private TableColumn<EntityAuthors, String> colAuthorName;
    @FXML private TableColumn<EntityAuthors, String> colAffiliation;

    @FXML
    public void initialize() {

        colAuthorName.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getUser() != null) {
                return new SimpleStringProperty(cellData.getValue().getUser().getName());
            }
            return new SimpleStringProperty("Desconhecido");
        });

        colAffiliation.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue() != null ? cellData.getValue().getAffiliation() : "---"));

        refreshData();
    }

    @FXML
    private void handleApproveAuthor() {
        EntityAuthors selected = tblPendingAuthors.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {

                authorService.approveAuthor(selected.getUser().getIdUser());

                showAlert(Alert.AlertType.INFORMATION, "Sucesso",
                        "O autor " + selected.getUser().getName() + " foi aprovado com sucesso!");

                refreshData();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao aprovar autor: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Seleção Necessária", "Por favor, selecione um autor na lista.");
        }
    }

    @FXML
    public void refreshData() {

        lblPendingPurchases.setText(String.valueOf(purchaseService.countPendingPayments()));
        lblPendingComments.setText(String.valueOf(commentService.countPendingComments()));
        lblTotalUsers.setText(String.valueOf(userService.countTotalUsers()));


        List<EntityAuthors> pending = authorService.findPendingAuthors();
        lblPendingAuthors.setText(String.valueOf(pending.size()));
        tblPendingAuthors.setItems(FXCollections.observableArrayList(pending));


        listRecentUsers.getItems().clear();
        List<EntityUsers> recent = userService.findRecentUsers();
        for (EntityUsers u : recent) {
            String status = Boolean.TRUE.equals(u.getIsAdmin()) ? "[ADMIN] " : "[USER] ";
            listRecentUsers.getItems().add(status + u.getName() + " - " + u.getEmail());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}