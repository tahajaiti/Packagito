package com.kyojin.packagito.mapper;

import com.kyojin.packagito.dto.request.CarrierRequest;
import com.kyojin.packagito.dto.response.CarrierDTO;
import com.kyojin.packagito.entity.enums.CarrierStatus;
import com.kyojin.packagito.entity.enums.Role;
import com.kyojin.packagito.entity.user.Carrier;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = { Role.class, CarrierStatus.class })
public interface CarrierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(Role.TRANSPORTER)")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "status", expression = "java(request.getStatus() != null ? request.getStatus() : CarrierStatus.AVAILABLE)")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Carrier toCarrier(CarrierRequest request);

    CarrierDTO toDTO(Carrier carrier);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateCarrier(CarrierRequest request, @MappingTarget Carrier carrier);
}
