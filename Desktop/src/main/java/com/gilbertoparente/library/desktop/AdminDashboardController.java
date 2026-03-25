package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.repositories.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AdminDashboardController {

    @Autowired private ArticleRepository articleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ThematicsRepository thematicsRepository;
    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private CommentRepository commentRepository;

    // Injeção do contexto do Spring para carregar outros Controllers
    @Autowired private ConfigurableApplicationContext springContext;

    @FXML private AnchorPane mainContent;
    @FXML private TableView<EntityArticles> articlesTable;
    @FXML private TableColumn<EntityArticles, Integer> idColumn;
    @FXML private TableColumn<EntityArticles, String> titleColumn;
    @FXML private TableColumn<EntityArticles, BigDecimal> priceColumn;


    @FXML
    public void initialize() {
        // Inicializar ecrâ de boas vindas
        javafx.application.Platform.runLater(() -> {
            loadSection("home-view.fxml");
        });
    }

    // --- MÉTODOS DE NAVEGAÇÃO (Chamados pelos botões do menu) ---

    @FXML
    private void showUsersSection() {
        loadSection("users-view.fxml");
    }

    @FXML
    private void showHomeSection() {
        loadSection("home-view.fxml"); // Onde vais pôr os gráficos depois
    }

    @FXML
    private void showArticlesSection() {
        // Se quiser modularizar, pode criar um articles-view.fxml
        // Por agora, recarregamos a lógica da tabela principal se necessário
        refreshArticles();
    }

    @FXML
    private void showThematicsSection() {
        loadSection("thematics-view.fxml");
    }

    @FXML
    private void showPurchasesSection() {
        loadSection("purchases-view.fxml");
    }

    @FXML
    private void showCommentsSection() {
        loadSection("comments-view.fxml");
    }

    // --- LÓGICA DE CARREGAMENTO DINÂMICO ---

    private void loadSection(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            // Dizemos ao JavaFX para usar o Spring para criar o Controller da nova view
            loader.setControllerFactory(springContext::getBean);
            Parent view = loader.load();

            // Ajustamos a nova vista para ocupar todo o espaço do AnchorPane central
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            mainContent.getChildren().setAll(view);
        } catch (Exception e) {
            System.err.println("Erro ao carregar a secção: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshArticles() {
        if (articlesTable != null && articleRepository != null) {
            articlesTable.setItems(FXCollections.observableArrayList(articleRepository.findAll()));
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) mainContent.getScene().getWindow();
            MainApp.showLoginView(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}