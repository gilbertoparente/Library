package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.repositories.ThematicsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ThematicsService {

    @Autowired
    private ThematicsRepository thematicsRepository;

    public List<EntityThematics> findAll() {
        return thematicsRepository.findAllByOrderByDescriptionAsc();
    }

    public EntityThematics findById(int id) {
        return thematicsRepository.findById(id).orElse(null);
    }

    public List<EntityThematics> searchByTematic(String tematic){

        if (tematic == null || tematic.trim().isEmpty()){
            return thematicsRepository.findAll();
        }

        return thematicsRepository.findByDescriptionContainingIgnoreCase(tematic.trim());
    }

    @Transactional
    public EntityThematics save(EntityThematics thematic) {

        if (thematic.getDescription() == null || thematic.getDescription().trim().isEmpty()) {
            throw new RuntimeException("A descrição da temática não pode estar vazia.");
        }

        Optional<EntityThematics> existing = thematicsRepository.findByDescriptionIgnoreCase(thematic.getDescription().trim());
        if (existing.isPresent() && existing.get().getIdThematic() != thematic.getIdThematic()) {
            throw new RuntimeException("Já existe uma temática com o nome: " + thematic.getDescription());
        }

        thematic.setDescription(thematic.getDescription().trim());

        return thematicsRepository.save(thematic);
    }

    @Transactional
    public void delete(int id) {
        EntityThematics thematic = thematicsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Temática não encontrada."));

        if (thematic.getArticles() != null && !thematic.getArticles().isEmpty()) {
            throw new RuntimeException("Não é possível apagar: Existem " + thematic.getArticles().size() + " artigos associados a esta temática.");
        }

        thematicsRepository.delete(thematic);
    }
}