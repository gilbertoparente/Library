package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.services.CommentService;
import com.gilbertoparente.library.services.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import java.io.IOException;

@Component
public class AdminDashboardController {

    @Autowired private UserSession userSession;
    @Autowired private CommentService commentService;
    @Autowired private ConfigurableApplicationContext springContext;

    @FXML private Label lblAdminName;
    @FXML private Label lblPendingAlert;
    @FXML private AnchorPane mainContent;

    @FXML
    private Button btnLogout;

    @FXML
    public void initialize() {

        if (userSession != null && userSession.getLoggedUser() != null) {
            lblAdminName.setText("Olá, " + userSession.getLoggedUser().getName());
        }

        updateBadges();

        Platform.runLater(this::showHomeSection);
    }

    private void updateBadges() {
        long pending = commentService.countPendingComments();
        if (lblPendingAlert != null) {
            lblPendingAlert.setText(pending > 0 ? String.valueOf(pending) : "");
            lblPendingAlert.setVisible(pending > 0);
        }
    }

    // --- MÉTODOS DE NAVEGAÇÃO ---

    @FXML private void showHomeSection() { loadSection("home-view.fxml"); }
    @FXML private void showUsersSection() { loadSection("users-view.fxml"); }
    @FXML private void showThematicsSection() {loadSection("thematics-view.fxml");
    }
    @FXML private void showArticlesSection() { loadSection("articles-view.fxml"); }
    @FXML private void showPurchasesSection() { loadSection("purchases-view.fxml"); }
    @FXML private void showCommentsSection() {
        loadSection("comments-view.fxml");
        updateBadges();
    }


    private void loadSection(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            loader.setControllerFactory(springContext::getBean);
            Parent view = loader.load();


            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            mainContent.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro de Navegação", "Erro ao carregar a vista: " + fxmlPath);
        }
    }


        @FXML
        private void handleLogout(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gilbertoparente/library/desktop/login-view.fxml"));
                loader.setControllerFactory(springContext::getBean);
                Parent loginRoot = loader.load();

                Stage currentStage = (Stage) btnLogout.getScene().getWindow();

                Scene loginScene = new Scene(loginRoot, 800, 600);

                currentStage.setScene(loginScene);
                currentStage.setTitle("Login - Biblioteca Científica");


                currentStage.setResizable(false);
                currentStage.centerOnScreen();
                currentStage.show();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erro", "Não foi possível carregar o login.");
            }
        }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Library Management - Login");
            stage.centerOnScreen();
        } catch (IOException e) {
            showAlert("Erro", "Não foi possível voltar ao Login.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}