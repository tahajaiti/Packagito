package com.kyojin.packagito.service;

import com.kyojin.packagito.dto.request.CreateParcelRequest;
import com.kyojin.packagito.dto.request.UpdateParcelRequest;
import com.kyojin.packagito.dto.response.ParcelDTO;
import com.kyojin.packagito.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParcelService {

    ParcelDTO create(CreateParcelRequest request);

    ParcelDTO findById(String id);

    Page<ParcelDTO> findAll(Pageable pageable, String destination, UserPrincipal currentUser);

    ParcelDTO update(String id, UpdateParcelRequest request);

    void delete(String id);

}
