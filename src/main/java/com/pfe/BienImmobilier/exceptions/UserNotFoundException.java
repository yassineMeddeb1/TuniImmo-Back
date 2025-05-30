package com.pfe.BienImmobilier.exceptions;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Utilisateur non trouvé avec l'id: " + id);
    }
}

