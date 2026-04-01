package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.services.ThematicsService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ThematicFormController {

    @Autowired
    private ThematicsService thematicsService;
    @FXML private TextField txtDescription;
    private EntityThematics currentThematic;
    private Stage stage;


    public void setThematicData(EntityThematics thematic, Stage stage) {
        this.stage = stage;

        if (thematic != null) {
            this.currentThematic = thematic;
            txtDescription.setText(thematic.getDescription());
        } else {
            this.currentThematic = new EntityThematics();
            txtDescription.clear();
        }
    }

    @FXML
    private void handleSave() {
        try {
            String description = txtDescription.getText();

            if (description == null || description.trim().isEmpty()) {
                showAlert("Campo Obrigatório", "Por favor, insira uma descrição para a temática.", Alert.AlertType.WARNING);
                return;
            }


            currentThematic.setDescription(description.trim());

            thematicsService.save(currentThematic);


            stage.close();
        } catch (Exception e) {
            showAlert("Erro ao Gravar", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        if (stage != null) stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}