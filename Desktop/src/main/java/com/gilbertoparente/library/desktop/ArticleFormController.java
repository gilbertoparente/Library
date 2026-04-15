package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.services.ArticleService;
import com.gilbertoparente.library.services.ThematicsService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashSet; // Importar HashSet
import java.util.List;
import java.util.Set;     // Importar Set

@Component
public class ArticleFormController {

    @Autowired private ArticleService articleService;
    @Autowired private ThematicsService thematicsService;

    @FXML private TextField txtTitle;
    @FXML private TextArea txtResume;
    @FXML private TextField txtPrice;
    @FXML private ComboBox<Integer> comboVat;
    @FXML private ComboBox<String> comboStatus;
    @FXML private CheckListView<EntityThematics> checkListThematics;
    @FXML private TextField txtFilePath;
    @FXML private DatePicker dpPublicationDate;
    @FXML private TextField txtDoi;
    @FXML private TextField txtKeywords;
    @FXML private TextField txtExternalAuthor;

    private EntityArticles currentArticle;
    private Stage stage;
    private File selectedFile;

    @FXML
    public void initialize() {
        comboVat.setItems(FXCollections.observableArrayList(6, 13, 23));
        comboStatus.setItems(FXCollections.observableArrayList("Rascunho", "Publicado", "Arquivado"));
        checkListThematics.setItems(FXCollections.observableArrayList(thematicsService.findAll()));

        checkListThematics.setCellFactory(lv -> new javafx.scene.control.cell.CheckBoxListCell<>(item -> checkListThematics.getItemBooleanProperty(item)) {
            @Override
            public void updateItem(EntityThematics item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescription());
                }
            }
        });
    }

    public void setArticleData(EntityArticles article, Stage stage) {
        this.stage = stage;
        this.selectedFile = null;

        this.currentArticle = (article != null) ? article : new EntityArticles();

        txtTitle.setText(currentArticle.getTitle());
        txtResume.setText(currentArticle.getResume());
        txtPrice.setText(currentArticle.getPrice() != null ? currentArticle.getPrice().toString() : "0.00");
        comboVat.setValue(currentArticle.getVatRate() != null ? currentArticle.getVatRate() : 6);
        comboStatus.setValue(currentArticle.getStatus() != null ? currentArticle.getStatus() : "Rascunho");
        txtFilePath.setText(currentArticle.getFilePath() != null ? currentArticle.getFilePath() : "");
        txtDoi.setText(currentArticle.getDoi());
        txtKeywords.setText(currentArticle.getKeywords());
        txtExternalAuthor.setText(currentArticle.getExternalAuthor());

        if (currentArticle.getPublicationDate() != null) {
            dpPublicationDate.setValue(currentArticle.getPublicationDate().toLocalDate());
        } else {
            dpPublicationDate.setValue(null);
        }

        // Tratar as Temáticas na Checklist
        checkListThematics.getCheckModel().clearChecks();
        if (currentArticle.getThematics() != null) {
            List<EntityThematics> allThematics = checkListThematics.getItems();
            for (EntityThematics t : currentArticle.getThematics()) {
                allThematics.stream()
                        .filter(item -> item.getIdThematic() == t.getIdThematic())
                        .findFirst()
                        .ifPresent(item -> checkListThematics.getCheckModel().check(item));
            }
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (txtTitle.getText().trim().isEmpty()) {
                throw new Exception("O título é obrigatório.");
            }

            currentArticle.setTitle(txtTitle.getText().trim());
            currentArticle.setResume(txtResume.getText());
            currentArticle.setPrice(new BigDecimal(txtPrice.getText()));
            currentArticle.setVatRate(comboVat.getValue());
            currentArticle.setStatus(comboStatus.getValue());
            currentArticle.setDoi(txtDoi.getText().trim());
            currentArticle.setKeywords(txtKeywords.getText().trim());
            currentArticle.setExternalAuthor(txtExternalAuthor.getText().trim());

            if (dpPublicationDate.getValue() != null) {
                currentArticle.setPublicationDate(java.sql.Date.valueOf(dpPublicationDate.getValue()));
            }

            currentArticle.setThematics(new HashSet<>(checkListThematics.getCheckModel().getCheckedItems()));

            articleService.save(currentArticle, selectedFile);
            stage.close();
        } catch (NumberFormatException e) {
            showAlert("Erro de Formato", "O preço deve ser um número válido (ex: 10.50).");
        } catch (Exception e) {
            showAlert("Erro ao Guardar", e.getMessage());
        }
    }

    @FXML
    private void handleSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Artigo Científico (PDF)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            this.selectedFile = file;
            txtFilePath.setText(file.getAbsolutePath());
        }
    }

    @FXML private void handleCancel() { stage.close(); }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}