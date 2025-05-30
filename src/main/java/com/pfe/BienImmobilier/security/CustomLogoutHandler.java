package com.pfe.BienImmobilier.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Implémente ici la logique de nettoyage, comme la suppression des tokens JWT, etc.
        System.out.println("Logging out...");
        // Par exemple, tu pourrais invalider un JWT ici ou supprimer des cookies
    }
}
