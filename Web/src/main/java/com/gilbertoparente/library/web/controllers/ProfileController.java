package com.gilbertoparente.library.web.controllers;

import com.gilbertoparente.library.entities.EntityAuthors;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.services.AuthorService;
import com.gilbertoparente.library.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        EntityUsers loggedUser = (EntityUsers) session.getAttribute("loggedUser");

        if (loggedUser == null) return "redirect:/login";

        // Recarregamos o user do DB para garantir dados frescos
        EntityUsers user = userService.getUserById(loggedUser.getIdUser());
        model.addAttribute("user", user);

        if ("AUTHOR".equals(session.getAttribute("userRole"))) {
            // No teu AuthorService, findById usa repository.findById.
            // Se o ID do Autor for diferente do ID do User,
            // podes precisar de adicionar findByUserId no Service.
            EntityAuthors author = authorService.findById(user.getIdUser());
            model.addAttribute("authorData", author);
        }

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam(value = "affiliation", required = false) String affiliation,
                                @RequestParam(value = "newPassword", required = false) String newPassword,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        EntityUsers sessionUser = (EntityUsers) session.getAttribute("loggedUser");
        if (sessionUser == null) return "redirect:/login";

        try {
            EntityUsers user = userService.getUserById(sessionUser.getIdUser());
            user.setName(name);

            if (newPassword != null && !newPassword.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(newPassword));
            }

            userService.save(user);

            if ("AUTHOR".equals(session.getAttribute("userRole"))) {
                EntityAuthors author = authorService.findById(user.getIdUser());
                if (author != null) {
                    author.setAffiliation(affiliation);
                    authorService.save(author);
                }
            }

            // Atualiza a sessão
            session.setAttribute("loggedUser", user);
            redirectAttributes.addFlashAttribute("success", "Perfil atualizado com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}