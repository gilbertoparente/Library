package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityPurchases;
import com.gilbertoparente.library.repositories.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    // Listar todas as compras (para o Admin)
    public List<EntityPurchases> findAll() {
        return purchaseRepository.findAll();
    }

    // Buscar compra por ID
    public EntityPurchases findById(int id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    // Buscar compras de um utilizador específico
    public List<EntityPurchases> getPurchasesByUser(int userId) {
        return purchaseRepository.findByUser_IdUser(userId);
    }

    // Guardar uma nova compra
    @Transactional
    public EntityPurchases save(EntityPurchases purchase) {
        // Regra de negócio: o valor não pode ser zero ou negativo
        if (purchase.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor da compra deve ser superior a zero.");
        }
        return purchaseRepository.save(purchase);
    }

    // Atualizar status de pagamento (simplificado)
    @Transactional
    public void updatePaidStatus(int id, boolean paid) {
        EntityPurchases purchase = findById(id);
        if (purchase != null) {
            purchase.setPaid(paid);
            purchaseRepository.save(purchase);
        } else {
            throw new RuntimeException("Compra não encontrada.");
        }
    }

    // Eliminar compra
    @Transactional
    public void delete(int id) {
        if (purchaseRepository.existsById(id)) {
            purchaseRepository.deleteById(id);
        } else {
            throw new RuntimeException("Não é possível eliminar: Compra inexistente.");
        }
    }
}