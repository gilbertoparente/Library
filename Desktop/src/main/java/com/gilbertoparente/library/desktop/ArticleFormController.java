package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.entities.EntityAuthors;
import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.services.ArticleService;
import com.gilbertoparente.library.services.AuthorService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ArticleFormController {

    @Autowired private ArticleService articleService;
    @Autowired private ThematicsService thematicsService;
    @Autowired private AuthorService authorService;

    @FXML private TextField txtTitle;
    @FXML private TextArea txtResume;
    @FXML private TextField txtPrice;
    @FXML private ComboBox<Integer> comboVat;
    @FXML private ComboBox<String> comboStatus;
    @FXML private ComboBox<EntityAuthors> comboInternalAuthor;
    @FXML private CheckListView<EntityThematics> checkListThematics;
    @FXML private TextField txtFilePath;
    @FXML private DatePicker dpPublicationDate;
    @FXML private TextField txtDoi;
    @FXML private TextField txtKeywords;

    private EntityArticles currentArticle;
    private Stage stage;
    private File selectedFile;

    @FXML
    public void initialize() {

        comboVat.setItems(FXCollections.observableArrayList(6, 13, 23));
        comboStatus.setItems(FXCollections.observableArrayList("Rascunho", "Publicado", "Arquivado"));


        List<EntityAuthors> allAuthors = authorService.findAll();
        comboInternalAuthor.setItems(FXCollections.observableArrayList(allAuthors));


        comboInternalAuthor.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(EntityAuthors item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getUser().getName());
            }
        });
        comboInternalAuthor.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(EntityAuthors item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getUser().getName());
            }
        });

        // 3. Carregar Temáticas
        checkListThematics.setItems(FXCollections.observableArrayList(thematicsService.findAll()));
        checkListThematics.setCellFactory(lv -> new javafx.scene.control.cell.CheckBoxListCell<>(item -> checkListThematics.getItemBooleanProperty(item)) {
            @Override
            public void updateItem(EntityThematics item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getDescription());
            }
        });
    }

    public void setArticleData(EntityArticles article, Stage stage) {
        this.stage = stage;
        this.selectedFile = null;
        this.currentArticle = (article != null) ? article : new EntityArticles();

        // campos
        txtTitle.setText(currentArticle.getTitle());
        txtResume.setText(currentArticle.getResume());
        txtPrice.setText(currentArticle.getPrice() != null ? currentArticle.getPrice().toString() : "0.00");
        comboVat.setValue(currentArticle.getVatRate() != null ? currentArticle.getVatRate() : 6);
        comboStatus.setValue(currentArticle.getStatus() != null ? currentArticle.getStatus() : "Rascunho");
        txtFilePath.setText(currentArticle.getFilePath() != null ? currentArticle.getFilePath() : "");
        txtDoi.setText(currentArticle.getDoi());
        txtKeywords.setText(currentArticle.getKeywords());

        // autor se tiver
        if (currentArticle.getAuthors() != null && !currentArticle.getAuthors().isEmpty()) {
            EntityAuthors firstAuthor = currentArticle.getAuthors().iterator().next();

            comboInternalAuthor.getItems().stream()
                    .filter(a -> a.getIdAuthor() == firstAuthor.getIdAuthor())
                    .findFirst()
                    .ifPresent(a -> comboInternalAuthor.setValue(a));
        }

        //data
        if (currentArticle.getPublicationDate() != null) {
            dpPublicationDate.setValue(currentArticle.getPublicationDate().toLocalDate());
        }

        // tematicasa
        checkListThematics.getCheckModel().clearChecks(); // Limpa seleções anteriores

        if (currentArticle.getThematics() != null && !currentArticle.getThematics().isEmpty()) {
            for (EntityThematics thematicOfArticle : currentArticle.getThematics()) {
                // Procuramos na lista do componente o objeto que tem o mesmo ID
                checkListThematics.getItems().stream()
                        .filter(item -> item.getIdThematic() == thematicOfArticle.getIdThematic())
                        .findFirst()
                        .ifPresent(item -> checkListThematics.getCheckModel().check(item));
            }
        }
    }

    @FXML
    private void handleSave() {
        try {

            if (txtTitle.getText().trim().isEmpty()) throw new Exception("O título é obrigatório.");
            if (comboInternalAuthor.getValue() == null) throw new Exception("Deve selecionar um autor registado.");


            currentArticle.setTitle(txtTitle.getText().trim());
            currentArticle.setResume(txtResume.getText());
            currentArticle.setPrice(new BigDecimal(txtPrice.getText()));
            currentArticle.setVatRate(comboVat.getValue());
            currentArticle.setStatus(comboStatus.getValue());
            currentArticle.setDoi(txtDoi.getText().trim());
            currentArticle.setKeywords(txtKeywords.getText().trim());
            currentArticle.setExternalAuthor(null);

            if (dpPublicationDate.getValue() != null) {
                currentArticle.setPublicationDate(java.sql.Date.valueOf(dpPublicationDate.getValue()));
            }

            Set<EntityAuthors> authorsSet = new HashSet<>();
            authorsSet.add(comboInternalAuthor.getValue());
            currentArticle.setAuthors(authorsSet);


            currentArticle.setThematics(new HashSet<>(checkListThematics.getCheckModel().getCheckedItems()));

            // Persistir
            articleService.save(currentArticle, selectedFile);
            stage.close();

        } catch (NumberFormatException e) {
            showAlert("Erro de Formato", "O preço deve ser um número válido.");
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