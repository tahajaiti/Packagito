package com.kyojin.packagito.service.impl;

import com.kyojin.packagito.core.exception.NotFoundException;
import com.kyojin.packagito.dto.request.CreateParcelRequest;
import com.kyojin.packagito.dto.request.UpdateParcelRequest;
import com.kyojin.packagito.dto.response.ParcelDTO;
import com.kyojin.packagito.entity.parcel.Parcel;
import com.kyojin.packagito.mapper.ParcelMapper;
import com.kyojin.packagito.repository.ParcelRepository;
import com.kyojin.packagito.service.ParcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParcelServiceImpl implements ParcelService {

    private final ParcelRepository parcelRepository;
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
}
