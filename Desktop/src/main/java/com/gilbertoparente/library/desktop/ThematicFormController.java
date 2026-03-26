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
@Scope("prototype") // Garante que o formulário é "limpo" cada vez que abre
public class ThematicFormController {

    @Autowired
    private ThematicsService thematicsService;

    @FXML private TextField txtDescription;

    private EntityThematics currentThematic;
    private Stage stage;

    /**
     * Prepara os dados para o formulário.
     * Se thematic for null, o formulário assume que é uma nova inserção.
     */
    public void setThematicData(EntityThematics thematic, Stage stage) {
        this.currentThematic = thematic;
        this.stage = stage;

        if (thematic != null) {
            txtDescription.setText(thematic.getDescription());
        } else {
            txtDescription.clear();
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Se for novo, criamos a instância agora
            if (currentThematic == null) {
                currentThematic = new EntityThematics();
            }

            currentThematic.setDescription(txtDescription.getText());

            // O service já tem as validações (não vazio, não duplicado)
            thematicsService.save(currentThematic);

            stage.close();
        } catch (Exception e) {
            showAlert("Erro ao Gravar", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}