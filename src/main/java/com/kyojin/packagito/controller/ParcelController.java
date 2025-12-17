package com.kyojin.packagito.controller;

import com.kyojin.packagito.dto.request.CreateParcelRequest;
import com.kyojin.packagito.dto.request.UpdateParcelRequest;
import com.kyojin.packagito.dto.response.ParcelDTO;
import com.kyojin.packagito.security.UserPrincipal;
import com.kyojin.packagito.service.ParcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelService parcelService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParcelDTO> createParcel(@RequestBody CreateParcelRequest request) {
        ParcelDTO parcelDTO = parcelService.create(request);
        return ResponseEntity.ok(parcelDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ParcelDTO>> findAll(
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(parcelService.findAll(pageable, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcelDTO> findById(
            @PathVariable String id) {
        return ResponseEntity.ok(parcelService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParcelDTO> update(@PathVariable String id, @RequestBody UpdateParcelRequest request) {
        ParcelDTO parcelDTO = parcelService.update(id, request);
        return ResponseEntity.ok(parcelDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        parcelService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
