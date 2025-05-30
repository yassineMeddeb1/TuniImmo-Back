package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.Role;
import com.pfe.BienImmobilier.entities.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(RoleType roleType);
}
