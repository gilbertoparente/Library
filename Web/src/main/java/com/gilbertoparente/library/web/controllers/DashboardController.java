package com.gilbertoparente.library.web.controllers;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository; // Reutiliza o que está na BLL

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session) {
        // 1. Obter o email de quem fez login através do Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // 2. Procurar os dados completos do utilizador
        Optional<EntityUsers> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            EntityUsers user = userOpt.get();

            // 3. Guardar na Sessão para o HTML (dashboard.html) conseguir ler
            session.setAttribute("loggedUser", user);

            // Definir o Role para o HTML mostrar os menus corretos
            // Se isAdmin na BD for true, tratamos como AUTHOR para este dashboard
            String role = Boolean.TRUE.equals(user.getIsAdmin()) ? "AUTHOR" : "READER";
            session.setAttribute("userRole", role);

            // Status de autor (1 para Ativo, 0 para Pendente)
            session.setAttribute("authorStatus", 1);
        }

        return "dashboard"; // Nome do ficheiro dashboard.html em /templates
    }
}