package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.*;
import com.pfe.BienImmobilier.exceptions.UserNotFoundException;
import com.pfe.BienImmobilier.mapper.UtilisateurMapper;
import com.pfe.BienImmobilier.model.*;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.services.inter.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UserRepository userRepository;
    private final UtilisateurMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getUsersPaginated(int page, int size, String sortField) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortField));
        return userRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserDTO> searchUsers(String query, String role) {
        return userRepository.findBySearchCriteria(query, role)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public AdminUserDTO toggleUserStatus(Long id) {
        Utilisateur user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setEnabled(!user.isEnabled());
        return mapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserReservationsDTO getUserReservations(Long userId) {
        Utilisateur user = userRepository.findByIdWithReservations(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return new UserReservationsDTO(userId, user.getReservations());
    }

    @Override
    @Transactional(readOnly = true)
    public UserAnnoncesDTO getUserAnnonces(Long userId) {
        Utilisateur user = userRepository.findByIdWithBiensImmobiliers(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return new UserAnnoncesDTO(userId, user.getBiensImmobiliers());
    }
}