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

@Component
public class ArticleFormController {

    @Autowired private ArticleService articleService;
    @Autowired private ThematicsService thematicsService;

    @FXML private TextField txtTitle;
    @FXML private TextArea txtResume;
    @FXML private TextField txtPrice;
    @FXML private ComboBox<Integer> comboVat;
    @FXML private CheckListView<EntityThematics> checkListThematics;
    @FXML private TextField txtFilePath;

    // CORREÇÃO: No formulário usamos DatePicker, não TableColumn
    @FXML private DatePicker dpPublicationDate;

    private EntityArticles currentArticle;
    private Stage stage;
    private File selectedFile;

    @FXML
    public void initialize() {
        // 1. Configurar o IVA
        comboVat.setItems(FXCollections.observableArrayList(6, 13, 23));

        // 2. Carregar as temáticas
        checkListThematics.setItems(FXCollections.observableArrayList(thematicsService.findAll()));

        // 3. Configurar as Checkboxes com o texto da descrição
        checkListThematics.setCellFactory(lv -> new javafx.scene.control.cell.CheckBoxListCell<>(item -> checkListThematics.getItemBooleanProperty(item)) {
            @Override
            public void updateItem(EntityThematics item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDescription());
            }
        });
    }

    public void setArticleData(EntityArticles article, Stage stage) {
        this.currentArticle = article;
        this.stage = stage;
        this.selectedFile = null;

        checkListThematics.getCheckModel().clearChecks();

        if (article != null) {
            // Modo Edição
            txtTitle.setText(article.getTitle());
            txtResume.setText(article.getResume());
            txtPrice.setText(article.getPrice() != null ? article.getPrice().toString() : "0.00");
            comboVat.setValue(article.getVatRate() != null ? article.getVatRate() : 6);
            txtFilePath.setText(article.getFilePath() != null ? article.getFilePath() : "");

            if (article.getPublicationDate() != null) {
                dpPublicationDate.setValue(article.getPublicationDate().toLocalDate());
            } else {
                dpPublicationDate.setValue(null);
            }

            if (article.getThematics() != null) {
                for (EntityThematics t : article.getThematics()) {
                    checkListThematics.getCheckModel().check(t);
                }
            }
        } else {
            // Modo Inserção
            txtTitle.clear();
            txtResume.clear();
            txtPrice.setText("0.00");
            comboVat.setValue(6);
            txtFilePath.clear();
            dpPublicationDate.setValue(null);
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (currentArticle == null) currentArticle = new EntityArticles();

            currentArticle.setTitle(txtTitle.getText());
            currentArticle.setResume(txtResume.getText());
            currentArticle.setPrice(new BigDecimal(txtPrice.getText()));
            currentArticle.setVatRate(comboVat.getValue());

            // Guardar a data
            if (dpPublicationDate.getValue() != null) {
                currentArticle.setPublicationDate(java.sql.Date.valueOf(dpPublicationDate.getValue()));
            }

            // Guardar temáticas
            currentArticle.setThematics(new ArrayList<>(checkListThematics.getCheckModel().getCheckedItems()));

            articleService.save(currentArticle, selectedFile);
            stage.close();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao guardar: " + e.getMessage()).show();
        }
    }

    @FXML
    private void handleSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            this.selectedFile = file;
            txtFilePath.setText(file.getName());
        }
    }

    @FXML private void handleCancel() { stage.close(); }
}