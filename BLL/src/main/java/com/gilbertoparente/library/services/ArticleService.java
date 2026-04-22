package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    private static final String UPLOAD_DIR = "files";

    public List<EntityArticles> findAll() {
        return articleRepository.findAll();
    }

    public List<EntityArticles> findPublished() {
        return articleRepository.findByStatus("published");
    }

    public EntityArticles findById(int id) {
        return articleRepository.findById(id).orElse(null);
    }

    public List<EntityArticles> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return articleRepository.findAll();
        }
        return articleRepository.findByTitleContainingIgnoreCase(title.trim());
    }

    /**
     * Exemplo de uso da pesquisa por autor externo (caso precise no futuro)
     */
    public List<EntityArticles> searchByExternalAuthor(String authorName) {
        return articleRepository.findByExternalAuthorContainingIgnoreCase(authorName);
    }

    @Transactional
    public EntityArticles save(EntityArticles article, File fileToUpload) {
        validateArticle(article);

        if (article.getStatus() == null || article.getStatus().isEmpty()) {
            article.setStatus("draft");
        }

        if (fileToUpload != null) {
            try {
                Path rootPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(rootPath)) {
                    Files.createDirectories(rootPath);
                }

                String fileName = System.currentTimeMillis() + "_" + fileToUpload.getName().replaceAll("\\s+", "_");
                Path targetPath = rootPath.resolve(fileName);

                Files.copy(fileToUpload.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                if (article.getFilePath() != null) {
                    deletePhysicalFile(article.getFilePath());
                }

                article.setFilePath(targetPath.toString());

            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar ficheiro: " + e.getMessage());
            }
        }
        return articleRepository.save(article);
    }

    public void openArticleFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new RuntimeException("Este artigo não tem um ficheiro associado.");
        }

        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            File file = path.toFile();

            if (!file.exists()) {
                throw new RuntimeException("O ficheiro físico não foi encontrado no servidor.");
            }

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "start", "", path.toString()).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", path.toString()).start();
            } else {
                new ProcessBuilder("xdg-open", path.toString()).start();
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao abrir o ficheiro: " + e.getMessage());
        }
    }

    @Transactional
    public void delete(int id) {
        EntityArticles article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artigo não encontrado para eliminação."));

        if (article.getFilePath() != null) {
            deletePhysicalFile(article.getFilePath());
        }

        articleRepository.delete(article);
    }

    private void deletePhysicalFile(String pathStr) {
        try {
            Path filePath = Paths.get(pathStr);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Erro ao apagar ficheiro físico: " + e.getMessage());
        }
    }

    private void validateArticle(EntityArticles article) {
        //  Validação do Título
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new RuntimeException("O título do artigo não pode estar vazio.");
        }

        // Validação do Preço (Deve ser > 0)

        if (article.getPrice() == null || article.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O preço do artigo deve ser um valor positivo superior a zero.");
        }

        // Validação da Data (Não pode ser futura)

        if (article.getPublicationDate() != null) {
            java.util.Date hoje = new java.util.Date();

            if (article.getPublicationDate().after(hoje)) {
                throw new RuntimeException("A data de publicação não pode ser uma data futura.");
            }
        }
    }

    @Transactional
    public EntityArticles save(EntityArticles article) {
        validateArticle(article);
        return articleRepository.saveAndFlush(article);
    }

    public List<EntityArticles> searchAdvanced(String term) {
        if (term == null || term.trim().isEmpty()) {
            return articleRepository.findAll();
        }
        String query = term.trim();
        return articleRepository.findByTitleContainingIgnoreCaseOrDoiContainingIgnoreCaseOrKeywordsContainingIgnoreCase(query, query, query);
    }

    public boolean existsByDoi(String doi) {
        return articleRepository.existsByDoi(doi);
    }
}