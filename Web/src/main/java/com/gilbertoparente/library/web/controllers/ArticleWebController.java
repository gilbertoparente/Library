package com.gilbertoparente.library.web.controllers;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.entities.EntityPurchases;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.ArticleRepository;
import com.gilbertoparente.library.repositories.PurchaseRepository;
import com.gilbertoparente.library.services.ArticleService;
import com.gilbertoparente.library.services.AuthorService;
import com.gilbertoparente.library.services.ThematicsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource; // IMPORT CORRETO
import org.springframework.core.io.UrlResource; // IMPORT CORRETO
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ArticleWebController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ThematicsService thematicService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    // --- PESQUISA DE ARTIGOS ---
    @GetMapping("/articles/search")
    public String searchArticles(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "thematic", required = false) Integer thematicId,
            @RequestParam(value = "author", required = false) Integer authorId,
            Model model) {

        model.addAttribute("allThematics", thematicService.findAll());
        model.addAttribute("allAuthors", authorService.findAll());

        List<EntityArticles> results;

        if (thematicId != null && thematicId > 0) {
            results = articleRepository.findByThematics_IdThematic(thematicId);
        } else if (authorId != null && authorId > 0) {
            results = articleRepository.findByAuthors_User_IdUser(authorId);
        } else if (query != null && !query.trim().isEmpty()) {
            results = articleRepository.findByTitleContainingIgnoreCaseOrDoiContainingIgnoreCaseOrKeywordsContainingIgnoreCase(
                    query, query, query);
        } else {
            results = articleRepository.findAll();
        }

        model.addAttribute("articles", results);
        model.addAttribute("lastQuery", query);
        model.addAttribute("lastThematic", thematicId);
        model.addAttribute("lastAuthor", authorId);

        return "articles_search";
    }

    // --- DETALHES DO ARTIGO ---
    @GetMapping("/articles/details/{id}")
    public String showDetails(@PathVariable("id") int id, Model model, HttpSession session) {
        EntityArticles article = articleService.findById(id);
        if (article == null) return "redirect:/articles/search?error=notfound";

        EntityUsers loggedUser = (EntityUsers) session.getAttribute("loggedUser");

        boolean hasAccess = false;
        if (loggedUser != null) {
            // Verifica se existe compra com status 'pago' (ou 'concluido')
            hasAccess = purchaseRepository.existsByUser_IdUserAndArticle_IdArticleAndStatus(
                    loggedUser.getIdUser(), id, "pago");
        }

        model.addAttribute("article", article);
        model.addAttribute("hasAccess", hasAccess);

        return "article_details";
    }

    // --- ADQUIRIR ARTIGO GRÁTIS ---
    @GetMapping("/articles/acquire-free/{id}")
    public String acquireFreeArticle(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        EntityUsers user = (EntityUsers) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        EntityArticles article = articleService.findById(id);
        if (article == null) return "redirect:/articles/search";

        // Validação extra: preço deve ser zero
        if (article.getPrice() != null && article.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            redirectAttributes.addFlashAttribute("error", "Este artigo é pago.");
            return "redirect:/articles/details/" + id;
        }

        // Criar registo na tabela purchases
        EntityPurchases purchase = new EntityPurchases();
        purchase.setUser(user);
        purchase.setArticle(article);
        purchase.setAmount(BigDecimal.ZERO);
        purchase.setStatus("pago");

        purchaseRepository.save(purchase);

        redirectAttributes.addFlashAttribute("success", "Artigo adicionado à sua biblioteca!");
        return "redirect:/articles/details/" + id;
    }

    // --- VISUALIZAR PDF ---
    @GetMapping("/articles/view-pdf/{id}")
    public ResponseEntity<Resource> viewPdf(@PathVariable("id") int id, HttpSession session) {
        EntityUsers user = (EntityUsers) session.getAttribute("loggedUser");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // Segurança: Verificar se o user tem permissão
        boolean hasAccess = purchaseRepository.existsByUser_IdUserAndArticle_IdArticleAndStatus(
                user.getIdUser(), id, "pago");

        if (!hasAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            EntityArticles article = articleService.findById(id);
            if (article == null || article.getFilePath() == null) {
                return ResponseEntity.notFound().build();
            }

            // O teu Service guarda caminhos como "files/nome.pdf"
            Path path = Paths.get(article.getFilePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/articles/my-library")
    public String showMyLibrary(HttpSession session, Model model) {
        // 1. Verificar se o utilizador está logado
        EntityUsers user = (EntityUsers) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        // 2. Buscar as compras concluídas ("pago")
        List<EntityPurchases> myPurchases = purchaseRepository.findByUser_IdUserAndStatus(user.getIdUser(), "pago");

        // 3. Enviar para o Model
        model.addAttribute("purchases", myPurchases);

        return "my_library";
    }
}