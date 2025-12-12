package com.kyojin.packagito.dto.response;

import com.kyojin.packagito.entity.enums.CarrierStatus;
import com.kyojin.packagito.entity.enums.Role;
import com.kyojin.packagito.entity.enums.Specialty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String id;
    private String username;
    private Role role;
    private boolean active;

    // transporter fields
    private CarrierStatus status;
    private Specialty specialty;
}
