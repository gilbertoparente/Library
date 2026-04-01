package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.services.ArticleService;
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
import java.util.stream.Collectors;

@Component
public class ArticlesController {

    @Autowired private ArticleService articleService;
    @Autowired private ConfigurableApplicationContext springContext;

    @FXML private TableView<EntityArticles> articlesTable;
    @FXML private TableColumn<EntityArticles, String> titleColumn;
    @FXML private TableColumn<EntityArticles, Collection<EntityThematics>> thematicsColumn;
    @FXML private TableColumn<EntityArticles, BigDecimal> priceColumn;
    @FXML private TableColumn<EntityArticles, String> statusColumn;
    @FXML private TableColumn<EntityArticles, java.sql.Date> dpPublicationDate;

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dpPublicationDate.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));
        dpPublicationDate.setCellFactory(column -> new TableCell<>() {
            private final java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
            @Override
            protected void updateItem(java.sql.Date item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatter.format(item));
            }
        });


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


        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toUpperCase());
                    if (item.equals("published")) setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    else if (item.equals("draft")) setStyle("-fx-text-fill: orange;");
                    else setStyle("-fx-text-fill: gray;");
                }
            }
        });

        refreshArticles();
    }

    @FXML
    public void refreshArticles() {
        articlesTable.setItems(FXCollections.observableArrayList(articleService.findAll()));
    }

    @FXML
    private void handleAddArticle() {
        showArticleForm(null);
    }

    @FXML
    private void handleEditArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showArticleForm(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um artigo para editar.");
        }
    }

    @FXML
    private void handleOpenArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                articleService.openArticleFile(selected.getFilePath());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro ao abrir ficheiro", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione um artigo para abrir o PDF.");
        }
    }

    @FXML
    private void handleDeleteArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (confirmDelete(selected.getTitle())) {
                try {
                    articleService.delete(selected.getIdArticle());
                    refreshArticles();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erro ao eliminar", e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione um artigo para eliminar.");
        }
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
            showAlert(Alert.AlertType.ERROR, "Erro de Interface", "Não foi possível carregar o formulário.");
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
        alert.setTitle("Confirmar Eliminação");
        alert.setHeaderText("Vai eliminar o artigo: " + title);
        alert.setContentText("Esta ação não pode ser desfeita e removerá o ficheiro físico.");
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }
}