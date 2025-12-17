package com.kyojin.packagito.dto.response;

import com.kyojin.packagito.entity.enums.CarrierStatus;
import com.kyojin.packagito.entity.enums.Specialty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierDTO {

    private String id;
    private String username;
    private boolean active;
    private CarrierStatus status;
    private Specialty specialty;
    private Instant createdAt;
    private Instant updatedAt;
}
