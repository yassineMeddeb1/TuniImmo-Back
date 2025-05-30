//package com.pfe.BienImmobilier.controllers;
//
//import com.pfe.BienImmobilier.entities.Utilisateur;
//
//import com.pfe.BienImmobilier.model.ContactDTO;
//import com.pfe.BienImmobilier.model.UtilisateurContactDTO;
//import com.pfe.BienImmobilier.services.impl.MessageServiceImpl;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/messages")
//public class ContactController {
//
//    private final MessageServiceImpl messageService;
//
//    public ContactController(MessageServiceImpl messageService) {
//        this.messageService = messageService;
//    }
//
//    @GetMapping("/contacts")
//    public ResponseEntity<List<ContactDTO>> getContacts() {
//        return ResponseEntity.ok(messageService.getContactsForCurrentUser());
//    }
//}
//
