package com.gilbertoparente.library.web;

import com.gilbertoparente.library.entities.EntityAuthors;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository;
import com.gilbertoparente.library.services.AuthorService;
import com.gilbertoparente.library.services.UserService; // Recomendado usar Service
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private AuthorService authorService;

    //pagina inicial

    @GetMapping("/")
    public String showLandingPage(HttpSession session) {
        // Se o utilizador já tiver sessão, manda-o para o dashboard
        if (session.getAttribute("loggedUser") != null) {
            return "redirect:/dashboard";
        }
        // Caso contrário, mostra a landing page (index.html)
        return "index";
    }

    // --- REGISTO ---

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new EntityUsers());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("user") EntityUsers user,
                                 @RequestParam(value = "isAuthor", defaultValue = "false") boolean isAuthor,
                                 @RequestParam(value = "affiliation", required = false) String affiliation,
                                 Model model) {
        try {
            // Verificar se o email já existe
            if (userService.findByEmail(user.getEmail()).isPresent()) {
                model.addAttribute("error", "Este email já se encontra registado.");
                return "register";
            }

            // 1. Cifrar Password e definir papel padrão
            user.setPassword(encoder.encode(user.getPassword()));
            user.setIsAdmin(false);
            userService.save(user); // Salva o utilizador primeiro para gerar o ID

            // 2. Se for autor, cria o perfil pendente
            if (isAuthor) {
                EntityAuthors author = new EntityAuthors();
                author.setUser(user);
                author.setAffiliation(affiliation);
                author.setStatus(0); // 0 = Pendente de aprovação pelo Admin Desktop
                authorService.save(author);
            }

            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao criar conta. Tente novamente.");
            return "register";
        }
    }

    // --- LOGIN ---

    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {

        if (session.getAttribute("loggedUser") != null) {
            return "redirect:/dashboard";
        }
        return "login-web";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              HttpSession session,
                              Model model) {

        return userService.findByEmail(email)
                .filter(user -> encoder.matches(password, user.getPassword()))
                .map(user -> {
                    session.setAttribute("loggedUser", user);
                    EntityAuthors author = authorService.findById(user.getIdUser());
                    if (author != null) {
                        session.setAttribute("userRole", "AUTHOR");
                        session.setAttribute("authorStatus", author.getStatus());
                    } else {
                        session.setAttribute("userRole", "READER");
                    }

                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Email ou palavra-passe incorretos.");
                    return "login-web";
                });
    }

    // --- LOGOUT & DASHBOARD ---

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

}