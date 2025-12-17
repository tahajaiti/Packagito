package com.kyojin.packagito.service.impl;

import com.kyojin.packagito.core.exception.NotFoundException;
import com.kyojin.packagito.dto.request.CreateParcelRequest;
import com.kyojin.packagito.dto.request.UpdateParcelRequest;
import com.kyojin.packagito.dto.response.ParcelDTO;
import com.kyojin.packagito.entity.enums.CarrierStatus;
import com.kyojin.packagito.entity.enums.ParcelStatus;
import com.kyojin.packagito.entity.enums.ParcelType;
import com.kyojin.packagito.entity.enums.Specialty;
import com.kyojin.packagito.entity.parcel.Parcel;
import com.kyojin.packagito.entity.user.Carrier;
import com.kyojin.packagito.mapper.ParcelMapper;
import com.kyojin.packagito.repository.ParcelRepository;
import com.kyojin.packagito.repository.UserRepository;
import com.kyojin.packagito.security.UserPrincipal;
import com.kyojin.packagito.service.ParcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParcelServiceImpl implements ParcelService {

    private final ParcelRepository parcelRepository;
    private final UserRepository userRepository;
    private final ParcelMapper parcelMapper;


    @Override
    public ParcelDTO create(CreateParcelRequest request) {
        log.info("Creating a new parcel with request: {}", request);

        Parcel parcel = parcelMapper.toParcel(request);

        Parcel saved = parcelRepository.save(parcel);

        log.info("Parcel created: {}", saved);

        return parcelMapper.toDTO(saved);
    }

    @Override
    public Page<ParcelDTO> findAll(Pageable pageable, String destination, UserPrincipal currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean hasSearch = destination != null && !destination.isBlank();

        if (isAdmin) {
            return hasSearch
                    ? parcelRepository.findAllByDestinationAddressContainingIgnoreCase(destination, pageable).map(parcelMapper::toDTO)
                    : parcelRepository.findAll(pageable).map(parcelMapper::toDTO);
        } else {
            return hasSearch
                    ? parcelRepository.findAllByCarrierIdAndDestinationAddressContainingIgnoreCase(currentUser.getId(), destination, pageable).map(parcelMapper::toDTO)
                    : parcelRepository.findAllByCarrierId(currentUser.getId(), pageable).map(parcelMapper::toDTO);
        }
    }

    @Override
    public ParcelDTO findById(String id) {
        log.info("Finding parcel by id: {}", id);

        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Parcel not found with id: {}", id);
                    return new NotFoundException("Parcel not found");
                });

        log.info("Parcel found: {}", parcel);

        return parcelMapper.toDTO(parcel);
    }

    @Override
    public ParcelDTO update(String id, UpdateParcelRequest request) {
        log.info("Updating parcel with id: {} and request: {}", id, request);

        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Parcel not found with id: {}", id);
                    return new NotFoundException("Parcel not found");
                });

        parcelMapper.updateParcel(request, parcel);

        Parcel saved = parcelRepository.save(parcel);

        log.info("Parcel updated: {}", saved);

        return parcelMapper.toDTO(saved);
    }

    @Override
    public void delete(String id) {
        log.info("Deleting parcel with id: {}", id);

        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Parcel not found with id: {}", id);
                    return new NotFoundException("Parcel not found");
                });

        parcelRepository.delete(parcel);

        log.info("Parcel deleted with id: {}", id);
    }

    @Override
    public ParcelDTO assign(String parcelId, String carrierId) {
        log.info("Assigning parcel {} to carrier {}", parcelId, carrierId);

        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new NotFoundException("Parcel not found"));

        Carrier carrier = (Carrier) userRepository.findById(carrierId)
                .orElseThrow(() -> new NotFoundException("Carrier not found"));

        if (carrier.getStatus() != CarrierStatus.AVAILABLE) {
            throw new RuntimeException("Carrier is not available");
        }

        if (!isSpecialtyCompatible(carrier.getSpecialty(), parcel.getParcelType())) {
            throw new RuntimeException("Carrier specialty does not match parcel type");
        }

        parcel.setCarrierId(carrierId);
        carrier.setStatus(CarrierStatus.ON_DELIVERY);

        userRepository.save(carrier);
        Parcel saved = parcelRepository.save(parcel);

        log.info("Parcel assigned: {}", saved);

        return parcelMapper.toDTO(saved);
    }

    @Override
    public ParcelDTO updateStatus(String parcelId, ParcelStatus status, UserPrincipal currentUser) {
        log.info("Updating parcel {} status to {}", parcelId, status);

        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new NotFoundException("Parcel not found"));

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !parcel.getCarrierId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not assigned to this parcel");
        }

        parcel.setStatus(status);
        Parcel saved = parcelRepository.save(parcel);

        log.info("Parcel status updated: {}", saved);

        return parcelMapper.toDTO(saved);
    }

    private boolean isSpecialtyCompatible(Specialty specialty, ParcelType parcelType) {
        return specialty.name().equals(parcelType.name());
    }
}
