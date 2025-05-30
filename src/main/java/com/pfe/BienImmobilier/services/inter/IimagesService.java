package com.pfe.BienImmobilier.services.inter;


import com.pfe.BienImmobilier.entities.Image;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IimagesService {
    Image uploadImage(MultipartFile file) throws IOException;
    Image uploadImageForBien(MultipartFile file, Long bienId) throws IOException;
    List<Image> getImagesForBien(Long bienId);
    ResponseEntity<byte[]> getImage(Long id) throws IOException;
    void uploadImageForUser(MultipartFile file, Long userId) throws IOException;
    byte[] getUserImage(Long userId);
    void deleteImage(Long id);
}