package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityAuthors;
import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<EntityUsers> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void registarNovoAutor(UserDTO dados) {

        EntityUsers user = new EntityUsers();
        user.setName(dados.getName());
        user.setEmail(dados.getEmail());
        user.setPassword(passwordEncoder.encode(dados.getPassword()));
        EntityAuthors author = new EntityAuthors();
        author.setAffiliation(dados.getAffiliation());
        author.setStatus(1); // Ativo por padrão
        author.setUser(user);
        user.setAuthor(author);

        userRepository.save(user);
    }

    public List<EntityUsers> searchByname(String user){
        if (user == null || user.trim().isEmpty()){
            return userRepository.findAll();
        }
        return userRepository.findByNameContainingIgnoreCase(user.trim());
    }



    public List<EntityUsers> findAll() {
        return userRepository.findAll();
    }

    public EntityUsers getUserById(int idUser) {
        return userRepository.findById(idUser).orElse(null);
    }


    @Transactional
    public EntityUsers save(EntityUsers user) {

        if (user.getIdUser() == 0 && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Este email já está registado no sistema.");
        }

        if (user.getIdUser() == 0 && user.getIsAdmin() == null) {
            user.setIsAdmin(false);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(int idUser) {
        if (userRepository.existsById(idUser)) {
            userRepository.deleteById(idUser);
        } else {
            throw new RuntimeException("Utilizador não encontrado.");
        }
    }

    // ESTATÍSTICAS

    public long countTotalUsers() {
        return userRepository.count();
    }

    public List<EntityUsers> findRecentUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    public boolean isAdmin(int idUser) {
        EntityUsers user = getUserById(idUser);
        return user != null && Boolean.TRUE.equals(user.getIsAdmin());
    }


}