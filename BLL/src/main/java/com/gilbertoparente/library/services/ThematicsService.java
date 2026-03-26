package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.repositories.ThematicsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThematicsService {

    @Autowired
    private ThematicsRepository thematicsRepository;

    public List<EntityThematics> findAll() {
        // Retorna todas as categorias por ordem alfabética se quiseres,
        // mas o findAll padrão já resolve para começar.
        return thematicsRepository.findAll();
    }

    public EntityThematics findById(int id) {
        return thematicsRepository.findById(id).orElse(null);
    }

    @Transactional
    public EntityThematics save(EntityThematics thematic) {
        var existing = thematicsRepository.findByDescriptionIgnoreCase(thematic.getDescription());


        if (existing.isPresent() && existing.get().getIdThematic() != thematic.getIdThematic()) {
            throw new RuntimeException("Esta temática já existe!");
        }

        if (thematic.getDescription() == null || thematic.getDescription().trim().isEmpty()) {
            throw new RuntimeException("O nome da temática não pode estar vazio.");
        }

        return thematicsRepository.save(thematic);
    }

    @Transactional
    public void delete(int id) {
        // Antes de apagar, poderias verificar se existem artigos associados
        // para evitar erros de integridade referencial.
        if (thematicsRepository.existsById(id)) {
            thematicsRepository.deleteById(id);
        } else {
            throw new RuntimeException("Temática não encontrada.");
        }
    }
}