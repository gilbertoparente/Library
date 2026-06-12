package com.gilbertoparente.library.web.controllers;

import com.gilbertoparente.library.entities.EntityPurchases;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.PurchaseRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SalesReportController {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @GetMapping("/sales/reports")
    public String showSalesReport(HttpSession session, Model model) {
        EntityUsers currentUser = (EntityUsers) session.getAttribute("loggedUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<EntityPurchases> authorSales = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        try {
            List<EntityPurchases> allPurchases = purchaseRepository.findAll();

            if (allPurchases != null) {
                // Filtra as vendas cujo artigo tem este utilizador associado como autor
                authorSales = allPurchases.stream()
                        .filter(p -> p.getArticle() != null && p.getArticle().getAuthors() != null &&
                                p.getArticle().getAuthors().stream()
                                        .anyMatch(author -> author.getUser() != null && author.getUser().getIdUser() == currentUser.getIdUser()))
                        .toList();

                // Soma o montante faturado
                totalRevenue = authorSales.stream()
                        .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
        } catch (Exception e) {
            System.out.println("Erro ao gerar relatório de vendas: " + e.getMessage());
        }

        model.addAttribute("sales", authorSales);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("salesCount", authorSales.size());

        return "sales-report";
    }
}