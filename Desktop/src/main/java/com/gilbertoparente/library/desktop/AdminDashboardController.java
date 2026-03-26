package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.repositories.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AdminDashboardController {

    @Autowired private ConfigurableApplicationContext springContext;

    // Repositórios para o Dashboard (usarás para as estatísticas depois)
    @Autowired private ArticleRepository articleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PurchaseRepository purchaseRepository;

    @FXML private AnchorPane mainContent; // Certifica-te que o FX:ID no FXML é este

    @FXML
    public void initialize() {
        // Carrega a home por defeito assim que o dashboard abre
        Platform.runLater(() -> showHomeSection());
    }

    // --- MÉTODOS DE NAVEGAÇÃO ---

    @FXML
    private void showHomeSection() {
        loadSection("home-view.fxml");
    }

    @FXML
    private void showUsersSection() {
        loadSection("users-view.fxml");
    }

    @FXML
    private void showThematicsSection() {
        loadSection("thematics-view.fxml");
    }

    @FXML
    private void showArticlesSection() {
        loadSection("articles-view.fxml");
    }

    @FXML
    private void showPurchasesSection() {
        loadSection("purchases-view.fxml");
    }

    @FXML
    private void showCommentsSection() {
        loadSection("comments-view.fxml");
    }

    // --- LÓGICA DE CARREGAMENTO DINÂMICO (Único Método) ---

    private void loadSection(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            loader.setControllerFactory(springContext::getBean);
            Parent view = loader.load();

            // Ajusta a vista para preencher o AnchorPane central
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            mainContent.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro de Navegação", "Não foi possível carregar: " + fxmlFile);
        }
    }

    @FXML
    private void handleLogout() {
        // Lógica para voltar ao ecrã de Login
        System.out.println("Saindo...");
        // Aqui chamarias o método da tua MainApp para mudar a Scene
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}