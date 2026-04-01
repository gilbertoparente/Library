package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityPurchases;
import com.gilbertoparente.library.services.PurchaseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PurchasesController {

    @Autowired
    private PurchaseService purchaseService;

    @FXML private TableView<EntityPurchases> purchasesTable;
    @FXML private TableColumn<EntityPurchases, Integer> colId;
    @FXML private TableColumn<EntityPurchases, String> colUser;
    @FXML private TableColumn<EntityPurchases, String> colArticle;
    @FXML private TableColumn<EntityPurchases, String> colDate;
    @FXML private TableColumn<EntityPurchases, BigDecimal> colAmount;
    @FXML private TableColumn<EntityPurchases, String> colStatus;
    @FXML private Label lblTotalAmount;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("idPurchase"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colUser.setCellValueFactory(cellData -> {
            var user = cellData.getValue().getUser();
            return new SimpleStringProperty(user != null ? user.getName() : "Utilizador Removido");
        });

        colArticle.setCellValueFactory(cellData -> {
            var article = cellData.getValue().getArticle();
            return new SimpleStringProperty(article != null ? article.getTitle() : "Artigo Removido");
        });


        colDate.setCellValueFactory(cellData -> {
            var date = cellData.getValue().getPurchaseDate();
            return new SimpleStringProperty(date != null ? date.format(formatter) : "---");
        });

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toUpperCase());
                    switch (status.toLowerCase()) {
                        case "paid" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        case "pending" -> setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                        case "refunded" -> setStyle("-fx-text-fill: #2980b9; -fx-font-style: italic;");
                        case "failed" -> setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
                        default -> setStyle("-fx-text-fill: #7f8c8d;");
                    }
                }
            }
        });

        loadData();
    }

    private void loadData() {
        List<EntityPurchases> list = purchaseService.findAll();
        purchasesTable.setItems(FXCollections.observableArrayList(list));


        BigDecimal total = list.stream()
                .filter(p -> "paid".equalsIgnoreCase(p.getStatus()))
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        lblTotalAmount.setText(String.format("%.2f €", total));
    }

    @FXML
    private void handleConfirmPayment() {
        EntityPurchases selected = purchasesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if ("paid".equalsIgnoreCase(selected.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Aviso", "Esta compra já se encontra liquidada.");
                return;
            }

            purchaseService.updateStatus(selected.getIdPurchase(), "paid");
            loadData();
        } else {
            showAlert(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione uma transação na tabela.");
        }
    }

    @FXML
    private void handleDelete() {
        EntityPurchases selected = purchasesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Deseja eliminar este registo de venda? \n\nNota: Isto deve ser feito apenas em caso de erro de inserção.",
                    ButtonType.YES, ButtonType.NO);
            alert.setHeaderText("Confirmar Eliminação");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        purchaseService.delete(selected.getIdPurchase());
                        loadData();
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
                    }
                }
            });
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