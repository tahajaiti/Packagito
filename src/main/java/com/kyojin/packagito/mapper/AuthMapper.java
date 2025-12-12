package com.kyojin.packagito.mapper;


import com.kyojin.packagito.dto.request.RegisterRequest;
import com.kyojin.packagito.dto.response.AuthResponse;
import com.kyojin.packagito.dto.response.UserDTO;
import com.kyojin.packagito.entity.enums.CarrierStatus;
import com.kyojin.packagito.entity.enums.Role;
import com.kyojin.packagito.entity.user.Carrier;
import com.kyojin.packagito.entity.user.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = {Role.class, CarrierStatus.class})
public interface AuthMapper {

    @Mapping(target = "type", constant = "Bearer")
    @Mapping(target = "role", source = "user.role")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "token", source = "token")
    AuthResponse toAuthResponse(User user, String token);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(Role.TRANSPORTER)")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "status", expression = "java(CarrierStatus.AVAILABLE)")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Carrier toCarrier(RegisterRequest request);

    @Mapping(target = "status", source = "user", qualifiedByName = "extractStatus")
    @Mapping(target = "specialty", source = "user", qualifiedByName = "extractSpecialty")
    UserDTO toDTO(User user);

    @Named("extractStatus")
    default CarrierStatus extractStatus(User user) {
        return user instanceof Carrier carrier ? carrier.getStatus() : null;
    }

    @Named("extractSpecialty")
    default com.kyojin.packagito.entity.enums.Specialty extractSpecialty(User user) {
        return user instanceof Carrier carrier ? carrier.getSpecialty() : null;
    }
}