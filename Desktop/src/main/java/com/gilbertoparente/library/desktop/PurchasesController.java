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

// ... (teus imports anteriores)

@Component
public class PurchasesController {

    @Autowired
    private PurchaseService purchaseService;

    // --- CORREÇÃO 1: DECLARAR OS CAMPOS DE PESQUISA ---
    @FXML private TextField txtSearchUser;
    @FXML private TextField txtSearchArticle;
    @FXML private ComboBox<String> comboStatus;
    @FXML private DatePicker dpStart, dpEnd;

    // Garante que o nome aqui bate com os listeners (usei purchasesTable)
    @FXML private TableView<EntityPurchases> purchasesTable;
    @FXML private TableColumn<EntityPurchases, Integer> colId;
    @FXML private TableColumn<EntityPurchases, String> colUser;
    @FXML private TableColumn<EntityPurchases, String> colArticle;
    @FXML private TableColumn<EntityPurchases, String> colDate;
    @FXML private TableColumn<EntityPurchases, BigDecimal> colAmount;
    @FXML private TableColumn<EntityPurchases, String> colStatus;
    @FXML private Label lblTotalAmount;
    @FXML private Label lblTotalPending;
    @FXML private Label lblCountPending;

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

                getStyleClass().removeAll("compra-paga", "compra-pendente", "compra-cancelada");
                setText(null);

                if (!empty && status != null) {
                    // Agora o setText é direto, o status já vem em PT da DB
                    setText(status.toUpperCase());

                    // O switch serve apenas para decidir a COR do CSS
                    switch (status) {
                        case "Pago" -> getStyleClass().add("compra-paga");
                        case "Pendente" -> getStyleClass().add("compra-pendente");
                        case "Cancelado" -> getStyleClass().add("compra-cancelada");
                    }
                }
            }
        });


        comboStatus.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) -> {
            if (newValue == null || "Todos".equals(newValue)) {
                loadData();
            } else {
                purchasesTable.setItems(FXCollections.observableArrayList(purchaseService.filterByStatus(newValue)));
            }
        });

        comboStatus.setItems(FXCollections.observableArrayList("Todos", "Pago", "Pendente", "Cancelado"));

       txtSearchUser.textProperty().addListener((obs, old, newValue) -> {
            purchasesTable.setItems(FXCollections.observableArrayList(purchaseService.searchByUserName(newValue)));
        });

        txtSearchArticle.textProperty().addListener((obs, old, newValue) -> {
            purchasesTable.setItems(FXCollections.observableArrayList(purchaseService.serachByArticleTitle(newValue)));
        });

        comboStatus.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) -> {
            if ("Todos".equals(newValue)) {
                loadData();
            } else {
                purchasesTable.setItems(FXCollections.observableArrayList(purchaseService.filterByStatus(newValue)));
            }
        });

        loadData();
    }

      private void updateTable() {
        String user = txtSearchUser.getText();
        String article = txtSearchArticle.getText();
        String status = comboStatus.getValue();

        if (!user.isEmpty()) {
            purchasesTable.setItems(FXCollections.observableArrayList(purchaseService.searchByUserName(user)));
        } else if (!article.isEmpty()) {
            purchasesTable.setItems(FXCollections.observableArrayList(purchaseService.serachByArticleTitle(article)));
        } else {
            purchasesTable.setItems(FXCollections.observableArrayList(purchaseService.filterByStatus(status)));
        }
    }

    @FXML
    private void handleClearFilters() {
        txtSearchUser.clear();
        txtSearchArticle.clear();
        comboStatus.getSelectionModel().select("Todos");
        dpStart.setValue(null);
        dpEnd.setValue(null);

        loadData();
    }

    private void loadData() {
        List<EntityPurchases> list = purchaseService.findAll();
        purchasesTable.setItems(FXCollections.observableArrayList(list));

        // Receita Total
        BigDecimal totalPaid = list.stream()
                .filter(p -> "Pago".equalsIgnoreCase(p.getStatus()))
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total por Receber (Tudo o que está PENDENTE)
        BigDecimal totalPending = list.stream()
                .filter(p -> "Pendente".equalsIgnoreCase(p.getStatus()))
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Contagem de faturas pendentes
        long countPending = list.stream()
                .filter(p -> "Pendente".equalsIgnoreCase(p.getStatus()))
                .count();

        // Atualizar a Interface
        lblTotalAmount.setText(String.format("%.2f €", totalPaid));
        lblTotalPending.setText(String.format("%.2f €", totalPending));
        lblCountPending.setText(String.valueOf(countPending));
    }

    @FXML
    private void handleConfirmPayment() {
        EntityPurchases selected = purchasesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if ("Pago".equalsIgnoreCase(selected.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Aviso", "Esta compra já se encontra liquidada.");
                return;
            }

            purchaseService.updateStatus(selected.getIdPurchase(), "Pago");
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