package com.gilbertoparente.library.web;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // O repositório que já tens na BLL

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        EntityUsers user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado: " + email));

        // Aqui dizemos ao Spring quais as permissões do utilizador
        String role = user.getIsAdmin() ? "ADMIN" : "USER";

        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(role)
                .build();
    }
}