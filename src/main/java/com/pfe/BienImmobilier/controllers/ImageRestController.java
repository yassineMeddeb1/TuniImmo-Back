package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.entities.Image;
import com.pfe.BienImmobilier.services.inter.IimagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4300", allowedHeaders = {"Content-Type"})
@RestController
@RequestMapping("/api/images")
public class ImageRestController {

    @Autowired
    private IimagesService imageService;

    @PostMapping("/upload/{bienId}")
    public Image uploadImageForBien(@RequestParam("image") MultipartFile file, @PathVariable("bienId") Long bienId) throws IOException {
        return imageService.uploadImageForBien(file, bienId);
    }

    @GetMapping("/getImagesForBien/{bienId}")
    public ResponseEntity<List<Image>> getImagesForBien(@PathVariable Long bienId) {
        List<Image> images = imageService.getImagesForBien(bienId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/load/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) throws IOException {
        return imageService.getImage(id);
    }
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Image uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        return imageService.uploadImage(file);
    }
    @PostMapping("/user/uploadImageUser/{userId}")
    public ResponseEntity<Map<String, String>> uploadUserImage(@RequestParam("image") MultipartFile image,
                                                               @PathVariable Long userId) {
        try {
            imageService.uploadImageForUser(image, userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Image utilisateur téléversée avec succès !");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors du téléversement de l’image : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long userId) {
        byte[] image = imageService.getUserImage(userId);
        if (image != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // ou PNG selon format stocké
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/delete/{id}")
    public void deleteImage(@PathVariable("id") Long id) {
        imageService.deleteImage(id);
    }
}