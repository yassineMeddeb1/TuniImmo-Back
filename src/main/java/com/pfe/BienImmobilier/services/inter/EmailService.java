package com.pfe.BienImmobilier.services.inter;

public interface EmailService {
    void envoyerEmail(String to, String subject, String content);
}
