package com.gilbertoparente.library.web.controllers;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.entities.EntityAuthors;
import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.services.ArticleService;
import com.gilbertoparente.library.services.AuthorService;
import com.gilbertoparente.library.services.ThematicsService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/articles")
public class ArticlePublishController {

    @Autowired
    private ArticleService articleService; // Serviço da BLL

    @Autowired
    private AuthorService authorService;

    @Autowired
    private ThematicsService thematicsService;

    @GetMapping("/publish")
    public String showPublishForm(HttpSession session, Model model) {
        EntityUsers currentUser = (EntityUsers) session.getAttribute("loggedUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("article", new EntityArticles());

        // Carrega todas as temáticas reais da BLL para mostrar no formulário Web
        List<EntityThematics> allThematics = thematicsService.findAll();
        model.addAttribute("allThematics", allThematics);

        return "publish-article";
    }

    @PostMapping("/publish")
    public String processPublish(@ModelAttribute("article") EntityArticles article,
                                 @RequestParam("pdfFile") MultipartFile multipartFile,
                                 @RequestParam(value = "selectedThematics", required = false) List<Integer> selectedThematicsIds,
                                 HttpSession session) {

        EntityUsers currentUser = (EntityUsers) session.getAttribute("loggedUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        File tempFile = null;

        try {
            // 1. Validações Obrigatórias Básicas
            if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
                return "redirect:/articles/publish?error=title_required";
            }
            if (multipartFile.isEmpty()) {
                return "redirect:/articles/publish?error=file_required";
            }

            // 2. Configurações Padrão de Autor da BLL (Mesma lógica do Desktop)
            article.setStatus("Rascunho");
            article.setExternalAuthor(null);

            // 3. Procurar o EntityAuthors correspondente através do idUser da sessão
            Optional<EntityAuthors> authorOpt = authorService.findByUser_IdUser(currentUser.getIdUser());
            if (authorOpt.isPresent()) {
                Set<EntityAuthors> authorsSet = new HashSet<>();
                authorsSet.add(authorOpt.get());
                article.setAuthors(authorsSet);
            } else {
                return "redirect:/articles/publish?error=not_an_author";
            }

            // 4. Mapear as Temáticas selecionadas nos Checkboxes para o Artigo
            if (selectedThematicsIds != null && !selectedThematicsIds.isEmpty()) {
                Set<EntityThematics> thematicsSet = new HashSet<>();
                List<EntityThematics> allThematics = thematicsService.findAll();

                for (Integer id : selectedThematicsIds) {
                    allThematics.stream()
                            .filter(t -> t.getIdThematic() == id)
                            .findFirst()
                            .ifPresent(thematicsSet::add);
                }
                article.setThematics(thematicsSet);
            }

            // 5. Tratamento de Upload de Ficheiro (Conversão MultipartFile -> java.io.File)
            String tempDir = System.getProperty("java.io.tmpdir");
            tempFile = new File(tempDir + File.separator + multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);

            // 6. Chamar a regra de negócio centralizada na tua BLL!
            articleService.save(article, tempFile);

        } catch (Exception e) {
            System.out.println("LOG-PUBLISH-WEB: Erro crítico ao guardar artigo: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/articles/publish?error=failed_saving";
        } finally {
            // Eliminar o lixo do ficheiro temporário após o processo terminar
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        return "redirect:/dashboard?success_publish";
    }
}