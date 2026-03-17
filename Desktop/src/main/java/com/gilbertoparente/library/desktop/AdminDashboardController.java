package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.repositories.ArticleRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AdminDashboardController {

    @Autowired
    private ArticleRepository articleRepository;

    @FXML private TableView<EntityArticles> articlesTable;
    @FXML private TableColumn<EntityArticles, Integer> idColumn;
    @FXML private TableColumn<EntityArticles, String> titleColumn;
    @FXML private TableColumn<EntityArticles, BigDecimal> priceColumn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idArticle"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        refreshArticles();
    }

    @FXML
    private void refreshArticles() {
        articlesTable.setItems(FXCollections.observableArrayList(articleRepository.findAll()));
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) articlesTable.getScene().getWindow();
            MainApp.showLoginView(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}