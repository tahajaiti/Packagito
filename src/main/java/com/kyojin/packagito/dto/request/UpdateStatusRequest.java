package com.kyojin.packagito.dto.request;

import com.kyojin.packagito.entity.enums.ParcelStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private ParcelStatus status;
}