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
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommentsController {

    @Autowired private CommentService commentService;
    @FXML private TreeTableView<EntityComments> commentsTreeTable;
    @FXML private TreeTableColumn<EntityComments, String> colArticle;
    @FXML private TreeTableColumn<EntityComments, String> colUser;
    @FXML private TreeTableColumn<EntityComments, String> colContent;
    @FXML private TreeTableColumn<EntityComments, String> colDate;
    @FXML private TreeTableColumn<EntityComments, Integer> colStatus;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {

        colContent.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getContent()));

        colArticle.setCellValueFactory(param -> {
            var art = param.getValue().getValue().getArticle();
            return new SimpleStringProperty(art != null ? art.getTitle() : "---");
        });

        colUser.setCellValueFactory(param -> {
            var usr = param.getValue().getValue().getUser();
            return new SimpleStringProperty(usr != null ? usr.getName() : "Anónimo");
        });

        colDate.setCellValueFactory(param -> {
            LocalDateTime date = param.getValue().getValue().getCreatedAt();
            return new SimpleStringProperty(date != null ? date.format(formatter) : "");
        });

        colStatus.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue().getStatus()));


        colStatus.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    switch (item) {
                        case 0 -> { setText("PENDENTE"); setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;"); }
                        case 1 -> { setText("APROVADO"); setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); }
                        case 2 -> { setText("OCULTO"); setStyle("-fx-text-fill: #c0392b;"); }
                        default -> setText("DESCONHECIDO");
                    }
                }
            }
        });

        loadTreeData();
    }

    private void loadTreeData() {
        List<EntityComments> allComments = commentService.findAll();


        TreeItem<EntityComments> root = new TreeItem<>(new EntityComments());


        Map<Integer, List<EntityComments>> commentsByParent = allComments.stream()
                .filter(c -> c.getParentComment() != null)
                .collect(Collectors.groupingBy(c -> c.getParentComment().getIdComment()));

        allComments.stream()
                .filter(c -> c.getParentComment() == null)
                .forEach(parent -> root.getChildren().add(buildTreeItem(parent, commentsByParent)));

        commentsTreeTable.setRoot(root);
        commentsTreeTable.setShowRoot(false);
    }


    private TreeItem<EntityComments> buildTreeItem(EntityComments comment, Map<Integer, List<EntityComments>> map) {
        TreeItem<EntityComments> item = new TreeItem<>(comment);
        item.setExpanded(true);

        List<EntityComments> children = map.get(comment.getIdComment());
        if (children != null) {
            for (EntityComments child : children) {
                item.getChildren().add(buildTreeItem(child, map));
            }
        }
        return item;
    }

    @FXML
    private void handleApprove() {
        updateStatus(1, "Comentário aprovado com sucesso.");
    }

    @FXML
    private void handleHide() {
        updateStatus(2, "Comentário ocultado.");
    }

    private void updateStatus(int newStatus, String successMsg) {
        TreeItem<EntityComments> selectedItem = commentsTreeTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() != null) {
            try {
                commentService.updateStatus(selectedItem.getValue().getIdComment(), newStatus);
                loadTreeData();

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível atualizar o status: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Seleção necessária", "Por favor, selecione um comentário na tabela.");
        }
    }

    @FXML
    private void handleDelete() {
        TreeItem<EntityComments> selectedItem = commentsTreeTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Tem a certeza que deseja eliminar permanentemente este comentário?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirmar Eliminação");
            if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                commentService.delete(selectedItem.getValue().getIdComment());
                loadTreeData();
            }
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