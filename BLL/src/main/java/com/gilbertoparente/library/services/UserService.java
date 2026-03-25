package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Resolve o erro do "findAll"
    public List<EntityUsers> findAll() {
        return userRepository.findAll();
    }

    // Resolve o erro do "getUserById"
    public EntityUsers getUserById(int id) {
        // O Spring usa findById(id), que retorna um Optional.
        // .orElse(null) garante que se não encontrar, devolve null.
        return userRepository.findById(id).orElse(null);
    }

    // Resolve o erro do "save"
    @Transactional
    public EntityUsers save(EntityUsers user) {
        return userRepository.save(user);
    }

    // Resolve o erro do "deleteUser"
    @Transactional
    public void deleteUser(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Utilizador não encontrado com o ID: " + id);
        }
    }

    // Método extra útil para o login
    public EntityUsers findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}