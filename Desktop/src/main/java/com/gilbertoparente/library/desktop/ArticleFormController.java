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
import java.util.ArrayList;
import java.util.List;

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

    private EntityArticles currentArticle;
    private Stage stage;
    private File selectedFile;

    @FXML
    public void initialize() {

        comboVat.setItems(FXCollections.observableArrayList(6, 13, 23));
        comboStatus.setItems(FXCollections.observableArrayList("draft", "published", "archived"));
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
        checkListThematics.getCheckModel().clearChecks();

        if (article != null) {
            this.currentArticle = article;
            txtTitle.setText(article.getTitle());
            txtResume.setText(article.getResume());
            txtPrice.setText(article.getPrice() != null ? article.getPrice().toString() : "0.00");
            comboVat.setValue(article.getVatRate() != null ? article.getVatRate() : 6);
            comboStatus.setValue(article.getStatus() != null ? article.getStatus() : "draft");
            txtFilePath.setText(article.getFilePath() != null ? article.getFilePath() : "");

            if (article.getPublicationDate() != null) {
                dpPublicationDate.setValue(article.getPublicationDate().toLocalDate());
            }

            if (article.getThematics() != null) {
                List<EntityThematics> allThematics = checkListThematics.getItems();
                for (EntityThematics articleThematic : article.getThematics()) {

                    allThematics.stream()
                            .filter(t -> t.getIdThematic() == articleThematic.getIdThematic())
                            .findFirst()
                            .ifPresent(t -> checkListThematics.getCheckModel().check(t));
                }
            }
        } else {

            this.currentArticle = new EntityArticles();
            txtTitle.clear();
            txtResume.clear();
            txtPrice.setText("0.00");
            comboVat.setValue(6);
            comboStatus.setValue("draft");
            txtFilePath.clear();
            dpPublicationDate.setValue(null);
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

            if (dpPublicationDate.getValue() != null) {
                currentArticle.setPublicationDate(java.sql.Date.valueOf(dpPublicationDate.getValue()));
            }

            currentArticle.setThematics(new ArrayList<>(checkListThematics.getCheckModel().getCheckedItems()));
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