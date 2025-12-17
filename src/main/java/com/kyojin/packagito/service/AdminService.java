package com.kyojin.packagito.service;

import com.kyojin.packagito.dto.request.CarrierRequest;
import com.kyojin.packagito.dto.response.CarrierDTO;
import com.kyojin.packagito.dto.response.UserDTO;
import com.kyojin.packagito.entity.enums.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    Page<CarrierDTO> getAllCarriers(Pageable pageable);

    Page<CarrierDTO> getCarriersBySpecialty(Specialty specialty, Pageable pageable);

    CarrierDTO getCarrierById(String id);

    CarrierDTO createCarrier(CarrierRequest request);

    CarrierDTO updateCarrier(String id, CarrierRequest request);

    void deleteCarrier(String id);

    Page<UserDTO> getAllUsers(Pageable pageable);

    UserDTO getUserById(String id);

    UserDTO disableUser(String id);

    UserDTO enableUser(String id);
}
