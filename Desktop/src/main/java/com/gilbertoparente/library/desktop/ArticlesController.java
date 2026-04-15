package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.entities.EntityAuthors;
import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.services.ArticleService;
import javafx.beans.property.SimpleObjectProperty;
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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArticlesController {

    @Autowired private ArticleService articleService;
    @Autowired private ConfigurableApplicationContext springContext;

    @FXML private TableView<EntityArticles> articlesTable;
    @FXML private TableColumn<EntityArticles, String> titleColumn;
    @FXML private TableColumn<EntityArticles, Collection<EntityAuthors>> authorsColumn;
    @FXML private TableColumn<EntityArticles, Collection<EntityThematics>> thematicsColumn;
    @FXML private TableColumn<EntityArticles, String> doiColumn;
    @FXML private TableColumn<EntityArticles, BigDecimal> priceColumn;
    @FXML private TableColumn<EntityArticles, BigDecimal> fullPriceColumn;
    @FXML private TableColumn<EntityArticles, String> statusColumn;
    @FXML private TableColumn<EntityArticles, java.sql.Date> dpPublicationDate;
    @FXML private TextField txtSearch;

    @FXML private ComboBox<String> comboFilterStatus;

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        doiColumn.setCellValueFactory(new PropertyValueFactory<>("doi"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dpPublicationDate.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));


        authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        authorsColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Collection<EntityAuthors> items, boolean empty) {
                super.updateItem(items, empty);
                EntityArticles article = getTableRow().getItem();

                if (empty || article == null) {
                    setText(null);
                } else {

                    if (items != null && !items.isEmpty()) {
                        String names = items.stream()
                                .map(a -> a.getUser().getName())
                                .collect(Collectors.joining(", "));
                        setText(names);
                    }
                    // Se não houver, tenta o Autor Externo (Texto livre)
                    else if (article.getExternalAuthor() != null && !article.getExternalAuthor().isEmpty()) {
                        setText(article.getExternalAuthor());
                    } else {
                        setText("---");
                    }
                }
            }
        });

        // PREÇO TOTAL (Com cálculo de IVA)
        fullPriceColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getFullPrice()));

        fullPriceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", item));
                }
            }
        });

        // TEMÁTICAS
        thematicsColumn.setCellValueFactory(new PropertyValueFactory<>("thematics"));
        thematicsColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Collection<EntityThematics> items, boolean empty) {
                super.updateItem(items, empty);
                if (empty || items == null || items.isEmpty()) {
                    setText(null);
                } else {
                    setText(items.stream()
                            .map(EntityThematics::getDescription)
                            .collect(Collectors.joining(", ")));
                }
            }
        });

        //  STATUS
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                // 1. Limpar sempre o estilo anterior para evitar bugs de reciclagem de células
                getStyleClass().removeAll("status-publicado", "status-rascunho", "status-arquivado");
                setText(null);

                if (!empty && item != null) {
                    // 2. Definir o texto e aplicar a classe CSS correspondente
                    // Usamos equalsIgnoreCase para garantir que funciona com "published" ou "Publicado"
                    if (item.equalsIgnoreCase("Publicado") || item.equalsIgnoreCase("published")) {
                        setText("PUBLICADO");
                        getStyleClass().add("status-publicado");
                    }
                    else if (item.equalsIgnoreCase("Rascunho") || item.equalsIgnoreCase("draft")) {
                        setText("RASCUNHO");
                        getStyleClass().add("status-rascunho");
                    }
                    else {
                        setText("ARQUIVADO");
                        getStyleClass().add("status-arquivado");
                    }
                }
            }
        });

        // PESQUISA AVANÇADA
        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, old, newValue) -> {
                articlesTable.setItems(FXCollections.observableArrayList(articleService.searchAdvanced(newValue)));
            });
        }
        if (comboFilterStatus != null) {
            comboFilterStatus.setItems(FXCollections.observableArrayList("Todos", "Rascunho", "Publicado", "Arquivado"));
            comboFilterStatus.setValue("Todos");

            comboFilterStatus.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }

        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }

        refreshArticles();
    }

    private void applyFilters() {
        String searchText = txtSearch.getText().toLowerCase();
        String statusFilter = comboFilterStatus.getValue();

        List<EntityArticles> allArticles = articleService.findAll();

        List<EntityArticles> filtered = allArticles.stream()
                .filter(article -> {

                    boolean matchesText = article.getTitle().toLowerCase().contains(searchText) ||
                            (article.getDoi() != null && article.getDoi().toLowerCase().contains(searchText)) ||
                            (article.getExternalAuthor() != null && article.getExternalAuthor().toLowerCase().contains(searchText)) ||
                            article.getAuthors().stream().anyMatch(a -> a.getUser().getName().toLowerCase().contains(searchText));


                    boolean matchesStatus = statusFilter.equals("Todos") || article.getStatus().equals(statusFilter);

                    return matchesText && matchesStatus;
                })
                .collect(Collectors.toList());

        articlesTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void refreshArticles() {
        articlesTable.setItems(FXCollections.observableArrayList(articleService.findAll()));
    }

    private void showArticleForm(EntityArticles article) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("article-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            ArticleFormController controller = loader.getController();
            Stage stage = new Stage();
            controller.setArticleData(article, stage);

            stage.setTitle(article == null ? "Novo Artigo" : "Editar Artigo");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(articlesTable.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshArticles();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar o formulário.");
        }
    }

    @FXML private void handleAddArticle() { showArticleForm(null); }
    @FXML private void handleEditArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected != null) showArticleForm(selected);
    }
    @FXML private void handleClearSearch() { txtSearch.clear(); refreshArticles(); }
    @FXML private void handleOpenArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected != null) articleService.openArticleFile(selected.getFilePath());
    }
    @FXML private void handleDeleteArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDelete(selected.getTitle())) {
            articleService.delete(selected.getIdArticle());
            refreshArticles();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean confirmDelete(String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("Eliminar: " + title);
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }
}