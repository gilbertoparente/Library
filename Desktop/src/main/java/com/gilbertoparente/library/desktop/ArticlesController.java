package com.gilbertoparente.library.desktop;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.services.ArticleService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class ArticlesController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ApplicationContext applicationContext;

    @FXML private TableView<EntityArticles> articlesTable;
    @FXML private TableColumn<EntityArticles, String> titleColumn;
    @FXML private TableColumn<EntityArticles, String> doiColumn; // ADICIONADO: Resolvido o erro de compilação
    @FXML private TableColumn<EntityArticles, String> authorsColumn;
    @FXML private TableColumn<EntityArticles, BigDecimal> priceColumn;
    @FXML private TableColumn<EntityArticles, String> statusColumn;
    @FXML private TableColumn<EntityArticles, String> thematicsColumn;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> comboFilterStatus;
    @FXML private ComboBox<String> comboTypeFilter;

    @FXML private VBox detailsContainer;

    private FilteredList<EntityArticles> filteredData;

    @FXML
    public void initialize() {
        configureColumns();
        setupFilters();
        setupSelectionListener();
        refreshArticles();
    }

    private void configureColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        // Agora funciona porque a variável foi declarada acima
        if (doiColumn != null) {
            doiColumn.setCellValueFactory(new PropertyValueFactory<>("doi"));
        }

        authorsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDisplayAuthors()));

        if (thematicsColumn != null) {
            thematicsColumn.setCellValueFactory(cellData -> {
                String thematicNames = cellData.getValue().getThematics().stream()
                        .map(t -> t.getDescription())
                        .collect(Collectors.joining(", "));
                return new SimpleStringProperty(thematicNames.isEmpty() ? "---" : thematicNames);
            });
        }

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.compareTo(BigDecimal.ZERO) == 0 ? "Grátis" : String.format("%.2f €", item));
                }
            }
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupFilters() {
        if (comboFilterStatus != null) {
            comboFilterStatus.setItems(FXCollections.observableArrayList("Todos", "Publicado", "Rascunho", "Arquivado"));
            comboFilterStatus.setValue("Todos");
            comboFilterStatus.setOnAction(e -> applyFilters());
        }

        if (comboTypeFilter != null) {
            comboTypeFilter.setItems(FXCollections.observableArrayList("Todos", "Pago", "Grátis"));
            comboTypeFilter.setValue("Todos");
            comboTypeFilter.setOnAction(e -> applyFilters());
        }

        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, old, newValue) -> applyFilters());
        }
    }

    private void setupSelectionListener() {
        articlesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateDetailsPane(newVal);
            } else {
                closeDetailsPane();
            }
        });
    }

    private void applyFilters() {
        if (filteredData == null) return;

        filteredData.setPredicate(article -> {
            String searchText = txtSearch.getText().toLowerCase();
            boolean matchesSearch = searchText.isEmpty() ||
                    (article.getTitle() != null && article.getTitle().toLowerCase().contains(searchText)) ||
                    (article.getDoi() != null && article.getDoi().toLowerCase().contains(searchText));

            String statusFilter = comboFilterStatus.getValue();
            String dbStatus = article.getStatus() != null ? article.getStatus().toLowerCase() : "";
            boolean matchesStatus = "Todos".equals(statusFilter) ||
                    ("Publicado".equals(statusFilter) && dbStatus.contains("pub")) ||
                    ("Rascunho".equals(statusFilter) && (dbStatus.contains("ras") || dbStatus.contains("dra"))) ||
                    ("Arquivado".equals(statusFilter) && (dbStatus.contains("arq") || dbStatus.contains("arc")));

            boolean matchesType = true;
            if (comboTypeFilter != null && !"Todos".equals(comboTypeFilter.getValue())) {
                String typeFilter = comboTypeFilter.getValue();
                BigDecimal price = article.getPrice();
                boolean isFree = (price == null || price.compareTo(BigDecimal.ZERO) == 0);

                if ("Pago".equals(typeFilter)) matchesType = !isFree;
                else if ("Grátis".equals(typeFilter)) matchesType = isFree;
            }

            return matchesSearch && matchesStatus && matchesType;
        });
    }

    private void updateDetailsPane(EntityArticles article) {
        if (detailsContainer == null) return;
        detailsContainer.getChildren().clear();

        // TÍTULO
        Label title = new Label(article.getTitle());
        title.getStyleClass().add("detalhe-titulo"); // Usa classe do style.css

        // INFORMAÇÕES GERAIS
        Label info = new Label("Autor(es): " + article.getDisplayAuthors() + "\n" +
                "DOI: " + (article.getDoi() != null ? article.getDoi() : "---") + "\n" +
                "Estado: " + article.getStatus() + "\n" +
                "Preço: " + (article.getPrice().compareTo(BigDecimal.ZERO) == 0 ? "Grátis" : article.getPrice() + " €"));
        info.getStyleClass().add("detalhe-info");
        info.setWrapText(true);

        Separator sep = new Separator();

        // RESUMO
        Label resumeTitle = new Label("Resumo:");
        resumeTitle.getStyleClass().add("detalhe-subtitulo");

        TextArea resumeText = new TextArea(article.getResume() != null ? article.getResume() : "Nenhum resumo disponível.");
        resumeText.getStyleClass().add("detalhe-textarea");
        resumeText.setWrapText(true);
        resumeText.setEditable(false);
        resumeText.setPrefRowCount(8);

        // BOTÃO
        Button btnOpen = new Button("Visualizar Artigo");
        btnOpen.getStyleClass().add("button-info");
        btnOpen.setMaxWidth(Double.MAX_VALUE);
        btnOpen.setOnAction(e -> handleOpenArticle());

        detailsContainer.getChildren().addAll(title, info, sep, resumeTitle, resumeText, btnOpen);
    }

    @FXML
    public void closeDetailsPane() {
        if (detailsContainer != null) {
            detailsContainer.getChildren().clear();
            Label placeholder = new Label("Selecione um artigo para ver os detalhes.");
            placeholder.getStyleClass().add("label-placeholder"); // Centralizado no CSS
            detailsContainer.getChildren().add(placeholder);
        }
        articlesTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void refreshArticles() {
        try {
            ObservableList<EntityArticles> allArticles = FXCollections.observableArrayList(articleService.findAll());
            filteredData = new FilteredList<>(allArticles, p -> true);
            articlesTable.setItems(filteredData);

            articlesTable.refresh();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao carregar artigos: " + e.getMessage());
        }
    }

    @FXML
    public void handleOpenArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                articleService.openArticleFile(selected.getFilePath());
            } catch (Exception e) {
                showAlert(Alert.AlertType.WARNING, "Documento", "Erro ao abrir ficheiro: " + e.getMessage());
            }
        }
    }

    @FXML public void handleAddArticle() { showArticleForm(null); }

    @FXML
    public void handleEditArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showArticleForm(selected);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Seleção", "Por favor, selecione um artigo para editar.");
        }
    }

    private void showArticleForm(EntityArticles article) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gilbertoparente/library/desktop/article-form.fxml"));
            loader.setControllerFactory(applicationContext::getBean);

            Parent root = loader.load();
            ArticleFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(article == null ? "Novo Artigo" : "Editar Artigo");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            controller.setArticleData(article, stage);

            stage.showAndWait();
            refreshArticles();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro de Interface", "Não foi possível abrir o formulário: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteArticle() {
        EntityArticles selected = articlesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deseja eliminar o artigo?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                articleService.delete(selected.getIdArticle());
                refreshArticles();
                closeDetailsPane();
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}