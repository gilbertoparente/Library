package com.gilbertoparente.library.desktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class MainApp extends Application {

    private static ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(DesktopApplication.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        showLoginView(stage);
    }

    public static void showLoginView(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();

        stage.setTitle("Open Library - Login");
        stage.setScene(new Scene(root, 600, 400));

        stage.show();
        stage.centerOnScreen();
    }

    public static void showDashboardView(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("dashboard-view.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();

        stage.setTitle("Open Library - Administrador");

        // Cria o ecra
        Scene scene = new Scene(root, 1100, 700);
        stage.setScene(scene);

        // janela
        stage.setResizable(true);
        stage.show();
        stage.centerOnScreen();


        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}