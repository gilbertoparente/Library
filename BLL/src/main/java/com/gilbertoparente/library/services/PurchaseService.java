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


        if (purchase.getIdPurchase() == 0 && purchase.getStatus() == null) {
            purchase.setStatus("Pendente");
        }

        return purchaseRepository.save(purchase);
    }

    @Transactional
    public void updateStatus(int id, String newStatus) {
        EntityPurchases purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada"));

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

    //pesquisar por nome
    public List<EntityPurchases> searchByUserName(String name){
        if (name == null || name.trim().isEmpty()){
            return  purchaseRepository.findAllByOrderByPurchaseDateDesc();
        }
        return  purchaseRepository.findByUser_NameContainingIgnoreCase(name.trim());
    }

    //pesquisar por titulo
    public List<EntityPurchases> serachByArticleTitle (String  title){
        if (title == null || title.trim().isEmpty()){
            return purchaseRepository.findAllByOrderByPurchaseDateDesc();
        }
        return purchaseRepository.findByArticle_TitleContainingIgnoreCase(title.trim());
    }

    // pesquisar pir estado do pagamento
    public List<EntityPurchases> filterByStatus (String status){
        if (status == null || status.equalsIgnoreCase("Todos")){
            return  purchaseRepository.findAllByOrderByPurchaseDateDesc();
        }
        return purchaseRepository.findByStatus(status);
    }
}