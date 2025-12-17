package com.kyojin.packagito.service.impl;

import com.kyojin.packagito.core.exception.DuplicateResourceException;
import com.kyojin.packagito.core.exception.NotFoundException;
import com.kyojin.packagito.dto.request.CarrierRequest;
import com.kyojin.packagito.dto.response.CarrierDTO;
import com.kyojin.packagito.dto.response.UserDTO;
import com.kyojin.packagito.entity.enums.Specialty;
import com.kyojin.packagito.entity.user.Carrier;
import com.kyojin.packagito.entity.user.User;
import com.kyojin.packagito.mapper.AuthMapper;
import com.kyojin.packagito.mapper.CarrierMapper;
import com.kyojin.packagito.repository.UserRepository;
import com.kyojin.packagito.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CarrierMapper carrierMapper;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<CarrierDTO> getAllCarriers(Pageable pageable) {
        log.info("Fetching all carriers with pagination: {}", pageable);
        return userRepository.findAllCarriers(pageable)
                .map(carrierMapper::toDTO);
    }

    @Override
    public Page<CarrierDTO> getCarriersBySpecialty(Specialty specialty, Pageable pageable) {
        log.info("Fetching carriers by specialty: {} with pagination: {}", specialty, pageable);
        return userRepository.findCarriersBySpecialty(specialty, pageable)
                .map(carrierMapper::toDTO);
    }

    @Override
    public CarrierDTO getCarrierById(String id) {
        log.info("Fetching carrier by id: {}", id);
        Carrier carrier = findCarrierById(id);
        return carrierMapper.toDTO(carrier);
    }

    @Override
    public CarrierDTO createCarrier(CarrierRequest request) {
        log.info("Creating new carrier with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("Username '{}' is already taken", request.getUsername());
            throw new DuplicateResourceException("Username is already taken: " + request.getUsername());
        }

        Carrier carrier = carrierMapper.toCarrier(request);
        carrier.setPassword(passwordEncoder.encode(request.getPassword()));

        Carrier savedCarrier = userRepository.save(carrier);
        log.info("Carrier '{}' created successfully with id: {}", request.getUsername(), savedCarrier.getId());

        return carrierMapper.toDTO(savedCarrier);
    }

    @Override
    public CarrierDTO updateCarrier(String id, CarrierRequest request) {
        log.info("Updating carrier with id: {}", id);

        Carrier carrier = findCarrierById(id);

        if (!carrier.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            log.error("Username '{}' is already taken", request.getUsername());
            throw new DuplicateResourceException("Username is already taken: " + request.getUsername());
        }

        carrierMapper.updateCarrier(request, carrier);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            carrier.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Carrier updatedCarrier = userRepository.save(carrier);
        log.info("Carrier '{}' updated successfully", id);

        return carrierMapper.toDTO(updatedCarrier);
    }

    @Override
    public void deleteCarrier(String id) {
        log.info("Deleting carrier with id: {}", id);

        Carrier carrier = findCarrierById(id);
        userRepository.delete(carrier);

        log.info("Carrier '{}' deleted successfully", id);
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(authMapper::toDTO);
    }

    @Override
    public UserDTO getUserById(String id) {
        log.info("Fetching user by id: {}", id);
        User user = findUserById(id);
        return authMapper.toDTO(user);
    }

    @Override
    public UserDTO disableUser(String id) {
        log.info("Disabling user with id: {}", id);

        User user = findUserById(id);
        user.setActive(false);

        User updatedUser = userRepository.save(user);
        log.info("User '{}' has been disabled", id);

        return authMapper.toDTO(updatedUser);
    }

    @Override
    public UserDTO enableUser(String id) {
        log.info("Enabling user with id: {}", id);

        User user = findUserById(id);
        user.setActive(true);

        User updatedUser = userRepository.save(user);
        log.info("User '{}' has been enabled", id);

        return authMapper.toDTO(updatedUser);
    }

    private Carrier findCarrierById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Carrier not found with id: " + id));

        if (!(user instanceof Carrier)) {
            throw new NotFoundException("Carrier not found with id: " + id);
        }

        return (Carrier) user;
    }

    private User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }
}
