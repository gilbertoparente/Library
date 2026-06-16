package com.gilbertoparente.library.web.controllers;

import com.gilbertoparente.library.entities.*;
import com.gilbertoparente.library.repositories.*;
import com.gilbertoparente.library.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleWebController {

    @Autowired private ArticleService articleService;
    @Autowired private ThematicsService thematicService;
    @Autowired private AuthorService authorService;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private CommentService commentService;

    // --- PESQUISA DE ARTIGOS ---
    @GetMapping("/search")
    public String searchArticles(@RequestParam(value = "query", required = false) String query,
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
            results = articleRepository.findByTitleContainingIgnoreCaseOrDoiContainingIgnoreCaseOrKeywordsContainingIgnoreCase(query, query, query);
        } else {
            results = articleRepository.findAll();
        }
        model.addAttribute("articles", results);
        return "articles_search";
    }

    // --- DETALHES E COMENTÁRIOS ---
    @GetMapping("/details/{id}")
    public String showDetails(@PathVariable("id") int id, Model model, HttpSession session) {
        EntityArticles article = articleService.findById(id);
        if (article == null) return "redirect:/articles/search?error=notfound";

        EntityUsers loggedUser = (EntityUsers) session.getAttribute("loggedUser");
        boolean hasAccess = (loggedUser != null) &&
                purchaseRepository.existsByUser_IdUserAndArticle_IdArticleAndStatus(loggedUser.getIdUser(), id, "pago");

        model.addAttribute("article", article);
        model.addAttribute("hasAccess", hasAccess);
        model.addAttribute("comments", commentService.getApprovedCommentsByArticle(id));
        return "article_details";
    }

    @PostMapping("/details/{id}/comment")
    public String addComment(@PathVariable("id") int id, @RequestParam("content") String content, HttpSession session, RedirectAttributes redirectAttributes) {
        EntityUsers loggedUser = (EntityUsers) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        try {
            EntityComments newComment = new EntityComments();
            newComment.setContent(content);
            newComment.setArticle(articleService.findById(id));
            newComment.setUser(loggedUser);
            commentService.save(newComment);
            redirectAttributes.addFlashAttribute("success", "Comentário publicado!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao publicar: " + e.getMessage());
        }
        return "redirect:/articles/details/" + id;
    }

    // --- ADQUIRIR ARTIGO GRÁTIS ---
    @GetMapping("/acquire-free/{id}")
    public String acquireFreeArticle(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        EntityUsers user = (EntityUsers) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        EntityArticles article = articleService.findById(id);
        if (article == null) return "redirect:/articles/search";

        if (article.getPrice() != null && article.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            redirectAttributes.addFlashAttribute("error", "Este artigo é pago.");
            return "redirect:/articles/details/" + id;
        }

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
    @GetMapping("/view-pdf/{id}")
    public ResponseEntity<Resource> viewPdf(@PathVariable("id") int id, HttpSession session) {
        EntityUsers user = (EntityUsers) session.getAttribute("loggedUser");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!purchaseRepository.existsByUser_IdUserAndArticle_IdArticleAndStatus(user.getIdUser(), id, "pago")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            EntityArticles article = articleService.findById(id);
            if (article == null || article.getFilePath() == null) return ResponseEntity.notFound().build();

            Path path = Paths.get(article.getFilePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- BIBLIOTECA PESSOAL ---
    @GetMapping("/my-library")
    public String showMyLibrary(HttpSession session, Model model) {
        EntityUsers user = (EntityUsers) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("purchases", purchaseRepository.findByUser_IdUserAndStatus(user.getIdUser(), "pago"));
        return "my_library";
    }

    // --- COMPRA PAGA ---
    @GetMapping("/buy/{id}")
    public String startPurchase(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        EntityUsers user = (EntityUsers) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        EntityArticles article = articleService.findById(id);
        if (article == null) return "redirect:/articles/search";

        if (purchaseRepository.existsByUser_IdUserAndArticle_IdArticleAndStatus(user.getIdUser(), id, "pago")) {
            redirectAttributes.addFlashAttribute("info", "Já adquiriu este artigo.");
            return "redirect:/articles/details/" + id;
        }

        model.addAttribute("article", article);
        return "checkout";
    }

    @PostMapping("/checkout/pay/{id}")
    public String processPayment(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        EntityUsers user = (EntityUsers) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        EntityArticles article = articleService.findById(id);
        if (article == null) return "redirect:/articles/search";

        EntityPurchases purchase = new EntityPurchases();
        purchase.setUser(user);
        purchase.setArticle(article);
        purchase.setAmount(article.getFullPrice());
        purchase.setStatus("pago");

        purchaseRepository.save(purchase);
        redirectAttributes.addFlashAttribute("success", "Pagamento confirmado!");
        return "redirect:/articles/details/" + id;
    }
}