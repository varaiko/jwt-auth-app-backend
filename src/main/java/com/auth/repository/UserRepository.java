package com.auth.repository;

import com.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findById(long id);

    Boolean existsByUsername(String username);

    Page<User> findAll(Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCase(Pageable pageable, String keyword);

}
