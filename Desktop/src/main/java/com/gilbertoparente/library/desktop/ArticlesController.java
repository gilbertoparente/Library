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
    @FXML private TableColumn<EntityArticles, BigDecimal> fullPriceColumn;

    @FXML private TableColumn<EntityArticles, java.sql.Date> dpPublicationDate;

    @FXML
    public void initialize() {
        // 1. Configurar as colunas básicas
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        fullPriceColumn.setCellValueFactory(new PropertyValueFactory<>("fullPrice"));

        // 2. CONFIGURAÇÃO DA DATA (O que faltava!)
        // Faz a ligação ao atributo 'publicationDate' da tua EntityArticles
        dpPublicationDate.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));

        // Formata a data para o padrão europeu (dd/MM/yyyy)
        dpPublicationDate.setCellFactory(column -> new TableCell<>() {
            private final java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
            @Override
            protected void updateItem(java.sql.Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // 3. Renderizar as temáticas como String
        thematicsColumn.setCellValueFactory(new PropertyValueFactory<>("thematics"));
        thematicsColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Collection<EntityThematics> items, boolean empty) {
                super.updateItem(items, empty);
                if (empty || items == null) {
                    setText(null);
                } else {
                    setText(items.stream()
                            .map(EntityThematics::getDescription)
                            .collect(Collectors.joining(", ")));
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
            showAlert("Seleção Necessária", "Selecione um artigo para editar.");
        }
    }

    @FXML
    private void handleOpenArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                // Chamas o serviço que criámos antes
                articleService.openArticleFile(selected.getFilePath());
            } catch (Exception e) {
                showAlert("Erro", e.getMessage());
            }
        } else {
            showAlert("Aviso", "Selecione um artigo na tabela para abrir o ficheiro.");
        }
    }

    @FXML
    private void handleDeleteArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            // Confirmação para o utilizador
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminação");
            alert.setHeaderText("Deseja eliminar o artigo: " + selected.getTitle() + "?");
            alert.setContentText("Esta ação irá apagar o registo e o ficheiro PDF associado.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    // Chama o método que acabámos de criar no Service
                    articleService.delete(selected.getIdArticle());

                    // Atualiza a tabela
                    refreshArticles();
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Erro ao eliminar: " + e.getMessage()).show();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Selecione um artigo para eliminar.").show();
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
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean confirmDelete(String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("Eliminar Artigo");
        alert.setContentText("Tem a certeza que deseja eliminar: " + title + "?");
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }
}