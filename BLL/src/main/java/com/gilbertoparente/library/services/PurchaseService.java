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
    public List<EntityPurchases> findAll() {
        return purchaseRepository.findAllByOrderByPurchaseDateDesc();
    }

    public EntityPurchases findById(int id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    public List<EntityPurchases> getPurchasesByUser(int idUser) {
        return purchaseRepository.findByUser_IdUser(idUser);
    }

    @Transactional
    public EntityPurchases save(EntityPurchases purchase) {
        // Validação de preço
        if (purchase.getAmount() == null || purchase.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor da compra deve ser superior a zero.");
        }

        if (purchase.getIdPurchase() == 0 && purchase.getStatus() == null) {
            purchase.setStatus("pending");
        }

        return purchaseRepository.save(purchase);
    }


    @Transactional
    public void updateStatus(int id, String newStatus) {
        EntityPurchases purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada ID: " + id));

        purchase.setStatus(newStatus);
        purchaseRepository.save(purchase);
    }

    @Transactional
    public void delete(int id) {
        if (!purchaseRepository.existsById(id)) {
            throw new RuntimeException("Não é possível eliminar: Compra inexistente.");
        }
        purchaseRepository.deleteById(id);
    }


    public long countPendingPayments() {
        return purchaseRepository.findByStatus("pending").size();
    }


    @Transactional
    public void refundPurchase(int id) {
        EntityPurchases purchase = findById(id);
        if (purchase != null && "paid".equals(purchase.getStatus())) {
            purchase.setStatus("refunded");
            purchaseRepository.save(purchase);
        } else {
            throw new RuntimeException("Apenas compras pagas podem ser reembolsadas.");
        }
    }
}