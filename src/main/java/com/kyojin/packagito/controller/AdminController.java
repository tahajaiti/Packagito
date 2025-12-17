package com.kyojin.packagito.controller;

import com.kyojin.packagito.dto.request.CarrierRequest;
import com.kyojin.packagito.dto.response.CarrierDTO;
import com.kyojin.packagito.dto.response.UserDTO;
import com.kyojin.packagito.entity.enums.Specialty;
import com.kyojin.packagito.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /**
     * List all carriers with pagination.
     * Optional filter by specialty.
     */
    @GetMapping("/carriers")
    public ResponseEntity<Page<CarrierDTO>> getAllCarriers(
            @RequestParam(required = false) Specialty specialty,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<CarrierDTO> carriers;
        if (specialty != null) {
            carriers = adminService.getCarriersBySpecialty(specialty, pageable);
        } else {
            carriers = adminService.getAllCarriers(pageable);
        }
        return ResponseEntity.ok(carriers);
    }

    /**
     * Get a specific carrier by ID.
     */
    @GetMapping("/carriers/{id}")
    public ResponseEntity<CarrierDTO> getCarrierById(@PathVariable String id) {
        CarrierDTO carrier = adminService.getCarrierById(id);
        return ResponseEntity.ok(carrier);
    }

    /**
     * Create a new carrier.
     */
    @PostMapping("/carriers")
    public ResponseEntity<CarrierDTO> createCarrier(@Valid @RequestBody CarrierRequest request) {
        CarrierDTO carrier = adminService.createCarrier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(carrier);
    }

    /**
     * Update an existing carrier.
     */
    @PutMapping("/carriers/{id}")
    public ResponseEntity<CarrierDTO> updateCarrier(
            @PathVariable String id,
            @Valid @RequestBody CarrierRequest request) {
        CarrierDTO carrier = adminService.updateCarrier(id, request);
        return ResponseEntity.ok(carrier);
    }

    /**
     * Delete a carrier.
     */
    @DeleteMapping("/carriers/{id}")
    public ResponseEntity<Void> deleteCarrier(@PathVariable String id) {
        adminService.deleteCarrier(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * List all users with pagination.
     */
    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<UserDTO> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get a specific user by ID.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        UserDTO user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Disable a user account (sets active = false).
     */
    @PatchMapping("/users/{id}/disable")
    public ResponseEntity<UserDTO> disableUser(@PathVariable String id) {
        UserDTO user = adminService.disableUser(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Enable a user account (sets active = true).
     */
    @PatchMapping("/users/{id}/enable")
    public ResponseEntity<UserDTO> enableUser(@PathVariable String id) {
        UserDTO user = adminService.enableUser(id);
        return ResponseEntity.ok(user);
    }
}
