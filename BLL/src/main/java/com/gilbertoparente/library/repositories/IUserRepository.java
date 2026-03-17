package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityUsers;
import java.util.List;

public interface IUserRepository {

    EntityUsers findById(int id);

    EntityUsers findByEmail(String email);

    List<EntityUsers> findAll();

    void updateName(int id, String name);

    void updateEmail(int id, String email);

    void updatePassword(int id, String password);

    void updateAdminStatus(int id, boolean isAdmin);

    void deleteById(int id);
}