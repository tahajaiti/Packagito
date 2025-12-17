package com.kyojin.packagito.repository;

import com.kyojin.packagito.entity.enums.Role;
import com.kyojin.packagito.entity.enums.Specialty;
import com.kyojin.packagito.entity.user.Carrier;
import com.kyojin.packagito.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String login);

    boolean existsByUsername(String login);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("{ '_class': 'carrier', 'specialty': ?0 }")
    Page<Carrier> findCarriersBySpecialty(Specialty specialty, Pageable pageable);

    @Query("{ '_class': 'carrier' }")
    Page<Carrier> findAllCarriers(Pageable pageable);

    Page<User> findByActive(boolean active, Pageable pageable);
}