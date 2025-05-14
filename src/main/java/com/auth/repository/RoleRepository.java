package com.auth.repository;

import com.auth.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    boolean existsByName(String name);

    Page<Role> findByNameContainingIgnoreCase(Pageable pageable, String keyword);

}
