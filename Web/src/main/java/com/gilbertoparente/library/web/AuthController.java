package com.gilbertoparente.library.web;

import com.gilbertoparente.library.entities.EntityAuthors;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.AuthorRepository; // Import do repositório correto
import com.gilbertoparente.library.services.AuthorService;
import com.gilbertoparente.library.services.UserService;
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

    @Autowired
    private AuthorRepository authorRepository; // A INJEÇÃO DEVE FICAR AQUI!

    // Pagina inicial
    @GetMapping("/")
    public String showLandingPage(HttpSession session) {
        if (session.getAttribute("loggedUser") != null) {
            return "redirect:/dashboard";
        }
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
            if (userService.findByEmail(user.getEmail()).isPresent()) {
                model.addAttribute("error", "Este email já se encontra registado.");
                return "register";
            }

            user.setPassword(encoder.encode(user.getPassword()));
            user.setIsAdmin(false);
            userService.save(user);

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

                    // Busca o autor associado ao ID do utilizador na BD
                    java.util.Optional<EntityAuthors> authorOpt = authorRepository.findByUser_IdUser(user.getIdUser());

                    if (authorOpt.isPresent()) {
                        EntityAuthors author = authorOpt.get();
                        System.out.println("LOG-AUTH: Utilizador é autor. ID User: " + user.getIdUser() + " | Status Autor: " + author.getStatus());

                        // Se o status for 0 e NÃO for administrador, barra imediatamente!
                        if (author.getStatus() == 0 && !Boolean.TRUE.equals(user.getIsAdmin())) {
                            System.out.println("LOG-AUTH: LOGIN BARRADO! Autor pendente de aprovação.");
                            return "redirect:/login?pending_approval";
                        }

                        // Se estiver aprovado (status != 0)
                        session.setAttribute("loggedUser", user);
                        session.setAttribute("userRole", "AUTHOR");
                        session.setAttribute("authorStatus", author.getStatus());

                    } else {
                        // Se não encontrar registo na tabela 'authors', é um READER (Leitor Comum)
                        System.out.println("LOG-AUTH: Utilizador comum (Reader) logado. ID User: " + user.getIdUser());
                        session.setAttribute("loggedUser", user);
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