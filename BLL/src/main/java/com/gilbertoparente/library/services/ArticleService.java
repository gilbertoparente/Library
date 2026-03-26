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

    // Listar todos para a TableView do JavaFX
    public List<EntityArticles> findAll() {
        return articleRepository.findAll();
    }

    // Buscar um artigo específico por ID
    public EntityArticles findById(int id) {
        return articleRepository.findById(id).orElse(null);
    }

    // Pesquisar por título
    public List<EntityArticles> searchByTitle(String title) {
        return articleRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public EntityArticles save(EntityArticles article, File fileToUpload) {
        validateArticle(article);

        if (fileToUpload != null) {
            try {
                // 1. Criar o caminho para a pasta 'files'
                Path rootPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(rootPath)) {
                    Files.createDirectories(rootPath);
                }

                // 2. Gerar nome único para evitar conflitos
                String fileName = System.currentTimeMillis() + "_" + fileToUpload.getName();
                Path targetPath = rootPath.resolve(fileName);

                // 3. Copiar o ficheiro para a pasta /files
                Files.copy(fileToUpload.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // 4. Guardar o caminho relativo na base de dados (ex: files\123_artigo.pdf)
                article.setFilePath(targetPath.toString());

            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar ficheiro: " + e.getMessage());
            }
        }
        return articleRepository.save(article);
    }

    // Método para abrir o ficheiro (usado pelo Controller da lista)
    public void openArticleFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new RuntimeException("Este artigo não tem um ficheiro associado.");
        }

        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            File file = path.toFile();

            if (!file.exists()) {
                throw new RuntimeException("Ficheiro não encontrado em: " + path.toString());
            }

            // TENTATIVA 1: Método Padrão Java
            if (java.awt.Desktop.isDesktopSupported()) {
                try {
                    java.awt.Desktop.getDesktop().open(file);
                    return; // Se funcionou, sai do método
                } catch (Exception e) {
                    System.out.println("Desktop.open falhou, a tentar via linha de comandos...");
                }
            }

            // TENTATIVA 2: Forçar via Windows Command (CMD / START)
            // O comando 'start' do Windows abre o ficheiro com o programa padrão
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "start", "", path.toString()).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", path.toString()).start();
            } else {
                // Linux
                new ProcessBuilder("xdg-open", path.toString()).start();
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao abrir o ficheiro: " + e.getMessage());
        }
    }

    // Método auxiliar para não repetires código de validação
    private void validateArticle(EntityArticles article) {
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new RuntimeException("O título do artigo não pode estar vazio.");
        }
        if (article.getPrice() == null || article.getPrice().doubleValue() < 0) {
            throw new RuntimeException("O preço não pode ser negativo.");
        }
        if (article.getVatRate() == null || article.getVatRate() < 0) {
            article.setVatRate(6);
        }
    }

    //metodo para eliminar

    @Transactional
    public void delete(int id) {
        // 1. Verificar se o artigo existe
        EntityArticles article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artigo não encontrado para eliminação."));

        // 2. Tentar apagar o ficheiro físico primeiro (opcional, mas recomendado)
        if (article.getFilePath() != null) {
            try {
                Path filePath = Paths.get(article.getFilePath());
                Files.deleteIfExists(filePath); // Apaga o ficheiro da pasta /files
            } catch (IOException e) {
                // Log do erro, mas continuamos para apagar o registo na BD
                System.err.println("Aviso: Não foi possível apagar o ficheiro físico: " + e.getMessage());
            }
        }

        // 3. Apagar o registo da base de dados
        articleRepository.delete(article);
    }

    // Mantemos o teu método original para quando editamos sem mudar o ficheiro
    @Transactional
    public EntityArticles save(EntityArticles article) {
        validateArticle(article);
        return articleRepository.save(article);
    }
}