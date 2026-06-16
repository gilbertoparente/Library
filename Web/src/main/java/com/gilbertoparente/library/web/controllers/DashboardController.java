package com.gilbertoparente.library.web.controllers;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.entities.EntityPurchases;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository;
import com.gilbertoparente.library.repositories.PurchaseRepository;
import com.gilbertoparente.library.repositories.ArticleRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        long totalCatalogArticles = 0;
        int totalMyPurchases = 0;
        List<EntityPurchases> recentPurchases = new ArrayList<>();
        List<EntityArticles> latestArticles = new ArrayList<>();

        // 1. Carregar Novidades e Biblioteca Geral
        try {
            if (articleRepository != null) {
                totalCatalogArticles = articleRepository.count();
                List<EntityArticles> allArticles = articleRepository.findAll();
                if (allArticles != null) {
                    latestArticles = allArticles.stream()
                            .sorted((a1, a2) -> Integer.compare(a2.getIdArticle(), a1.getIdArticle()))
                            .limit(3)
                            .toList();
                }
            }
        } catch (Exception e) {
            System.out.println("LOG-DASHBOARD: Erro ao carregar catálogo: " + e.getMessage());
        }

        // Utilizador (Security vs HttpSession)
        EntityUsers currentUser = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String email = auth.getName();
            Optional<EntityUsers> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                System.out.println("LOG-DASHBOARD: Utilizador capturado via Spring Security: " + currentUser.getEmail());
            }
        }

        // Se o Security falhou/anónimo, tentar recuperar o utilizador diretamente da Sessão HTTP
        if (currentUser == null && session != null) {
            Object loggedUserObj = session.getAttribute("loggedUser");
            if (loggedUserObj instanceof EntityUsers) {
                currentUser = (EntityUsers) loggedUserObj;
                System.out.println("LOG-DASHBOARD: Utilizador recuperado via HttpSession: " + currentUser.getEmail());
            }
        }



        if (currentUser != null) {
            session.setAttribute("loggedUser", currentUser);

            // CONTROLO DE ACESSO INTELIGENTE
            String role = "READER";
            int authorStatus = 0;

            if (Boolean.TRUE.equals(currentUser.getIsAdmin())) {

                role = "READER";
                authorStatus = 0;
            } else {


                role = "AUTHOR";
                authorStatus = 1;

                System.out.println("LOG-DASHBOARD: Utilizador comum detetado. Atribuído papel de AUTHOR para testes.");
            }

            // sessão para o Thymeleaf
            session.setAttribute("userRole", role);
            session.setAttribute("authorStatus", authorStatus);

            // Carregar as compra
            try {
                if (purchaseRepository != null) {
                    List<EntityPurchases> userPurchases = purchaseRepository.findByUser_IdUser(currentUser.getIdUser());

                    if (userPurchases != null) {
                        totalMyPurchases = userPurchases.size();

                        recentPurchases = userPurchases.stream()
                                .sorted((p1, p2) -> Integer.compare(p2.getIdPurchase(), p1.getIdPurchase()))
                                .limit(3)
                                .toList();
                    }
                }
            } catch (Exception e) {
                System.out.println("LOG-DASHBOARD: Erro ao carregar compras: " + e.getMessage());
            }
        } else {
            System.out.println("LOG-DASHBOARD: ERRO Crítico! Nenhum utilizador foi detetado.");
        }

        // 4. Injetar com segurança no Model
        model.addAttribute("totalArticles", totalCatalogArticles);
        model.addAttribute("totalMyPurchases", totalMyPurchases);
        model.addAttribute("recentPurchases", recentPurchases);
        model.addAttribute("latestArticles", latestArticles);

        return "dashboard";
    }
}