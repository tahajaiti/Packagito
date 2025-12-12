package com.kyojin.packagito.service;

import com.kyojin.packagito.dto.request.CreateParcelRequest;
import com.kyojin.packagito.dto.request.UpdateParcelRequest;
import com.kyojin.packagito.dto.response.ParcelDTO;

public interface ParcelService {

    ParcelDTO create(CreateParcelRequest request);

    ParcelDTO findById(String id);

    ParcelDTO update(String id, UpdateParcelRequest request);

    void delete(String id);

}
