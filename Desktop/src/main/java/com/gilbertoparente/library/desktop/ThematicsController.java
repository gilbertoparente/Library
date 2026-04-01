package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.services.ThematicsService;
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

import java.util.Optional;

@Component
public class ThematicsController {

    @Autowired private ThematicsService thematicsService;
    @Autowired private ConfigurableApplicationContext springContext;
    @FXML private TableView<EntityThematics> thematicsTable;
    @FXML private TableColumn<EntityThematics, Integer> idColumn;
    @FXML private TableColumn<EntityThematics, String> descColumn;

    @FXML
    public void initialize() {

        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        refreshTable();
    }


    @FXML
    public void refreshTable() {
        try {
            var lista = thematicsService.findAll();
            thematicsTable.setItems(FXCollections.observableArrayList(lista));
            thematicsTable.refresh();
        } catch (Exception e) {
            showAlert("Erro", "Erro ao atualizar tabela: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddThematic() {
        showThematicForm(null);
    }

    @FXML
    private void handleEditThematic() {
        EntityThematics selected = thematicsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showThematicForm(selected);
        } else {
            showAlert("Seleção Necessária", "Selecione uma temática para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDeleteThematic() {
        EntityThematics selected = thematicsTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Eliminação");
            confirm.setHeaderText("Deseja eliminar a temática: " + selected.getDescription() + "?");
            confirm.setContentText("Atenção: A operação falhará se existirem artigos associados a esta temática.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    thematicsService.delete(selected.getIdThematic());
                    refreshTable();
                } catch (Exception e) {
                    showAlert("Erro ao Eliminar", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Seleção Necessária", "Selecione uma temática para eliminar.", Alert.AlertType.WARNING);
        }
    }

    private void showThematicForm(EntityThematics thematic) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("thematic-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            ThematicFormController controller = loader.getController();
            Stage stage = new Stage();

            controller.setThematicData(thematic, stage);

            stage.setTitle(thematic == null ? "Nova Temática" : "Editar Temática");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(thematicsTable.getScene().getWindow());
            stage.setScene(new Scene(root));

            stage.showAndWait();
            refreshTable();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro de Interface", "Não foi possível carregar o formulário.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}