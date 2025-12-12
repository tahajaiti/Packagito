package com.kyojin.packagito.repository;

import com.kyojin.packagito.entity.enums.Role;
import com.kyojin.packagito.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String login);

    boolean existsByUsername(String login);

    Page<User> findByRole(Role role, Pageable pageable);

//    Page<User> findByRoleAndSpecialty(Role role, Specialty specialty, Pageable pageable);

    Page<User> findByActive(boolean active, Pageable pageable);
}