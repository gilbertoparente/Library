package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityComments;
import com.gilbertoparente.library.services.CommentService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CommentsController {

    @Autowired private CommentService commentService;

    // IMPORTANTE: Mudar tudo para TreeTableView e TreeTableColumn
    @FXML private TreeTableView<EntityComments> commentsTreeTable;
    @FXML private TreeTableColumn<EntityComments, String> colArticle;
    @FXML private TreeTableColumn<EntityComments, String> colUser;
    @FXML private TreeTableColumn<EntityComments, String> colContent;
    @FXML private TreeTableColumn<EntityComments, String> colDate;
    @FXML private TreeTableColumn<EntityComments, Integer> colStatus;

    @FXML
    public void initialize() {
        // 1. Coluna de Conteúdo
        colContent.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getValue().getContent()));

        // 2. Coluna de Artigo
        colArticle.setCellValueFactory(param -> {
            EntityComments c = param.getValue().getValue();
            return new SimpleStringProperty(c.getArticle() != null ? c.getArticle().getTitle() : "N/A");
        });

        // 3. Coluna de Utilizador
        colUser.setCellValueFactory(param -> {
            EntityComments c = param.getValue().getValue();
            return new SimpleStringProperty(c.getUser() != null ? c.getUser().getName() : "Anónimo");
        });

        // 4. Coluna de Data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colDate.setCellValueFactory(param -> {
            LocalDateTime date = param.getValue().getValue().getCreatedAt();
            return new SimpleStringProperty(date != null ? date.format(formatter) : "");
        });

        // 5. Coluna de Status
        colStatus.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getValue().getStatus()));

        colStatus.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    switch (item) {
                        case 0 -> { setText("Pendente"); setStyle("-fx-text-fill: orange;"); }
                        case 1 -> { setText("Aprovado"); setStyle("-fx-text-fill: green;"); }
                        case 2 -> { setText("Oculto"); setStyle("-fx-text-fill: red;"); }
                    }
                }
            }
        });

        loadTreeData();
    }

    private void loadTreeData() {
        List<EntityComments> allComments = commentService.findAll();
        TreeItem<EntityComments> root = new TreeItem<>(new EntityComments());

        // Filtra os pais e adiciona os filhos
        allComments.stream()
                .filter(c -> c.getParentComment() == null)
                .forEach(parent -> {
                    TreeItem<EntityComments> parentItem = new TreeItem<>(parent);
                    parentItem.setExpanded(true);

                    allComments.stream()
                            .filter(c -> c.getParentComment() != null && c.getParentComment().getIdComment() == parent.getIdComment())
                            .forEach(child -> parentItem.getChildren().add(new TreeItem<>(child)));

                    root.getChildren().add(parentItem);
                });

        commentsTreeTable.setRoot(root);
        commentsTreeTable.setShowRoot(false);
    }

    @FXML private void handleApprove() { updateStatus(1); }
    @FXML private void handleHide() { updateStatus(2); }

    private void updateStatus(int newStatus) {
        TreeItem<EntityComments> selectedItem = commentsTreeTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() != null) {
            try {
                commentService.updateStatus(selectedItem.getValue().getIdComment(), newStatus);
                loadTreeData();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Erro: " + e.getMessage()).show();
            }
        }
    }

    @FXML private void handleDelete() {
        TreeItem<EntityComments> selectedItem = commentsTreeTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            commentService.delete(selectedItem.getValue().getIdComment());
            loadTreeData();
        }
    }

    @FXML private void handleClose() {
        commentsTreeTable.getScene().getWindow().hide();
    }
}