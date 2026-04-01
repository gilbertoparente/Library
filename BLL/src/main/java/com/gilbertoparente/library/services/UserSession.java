package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityUsers;
import org.springframework.stereotype.Service;

@Service
public class UserSession {
    private EntityUsers loggedUser;

    public EntityUsers getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(EntityUsers user) {
        this.loggedUser = user;
    }

    public void logout() {
        this.loggedUser = null;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }
}