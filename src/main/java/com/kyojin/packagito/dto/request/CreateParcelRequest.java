package com.kyojin.packagito.dto.request;

import com.kyojin.packagito.core.annotation.ValidParcel;
import com.kyojin.packagito.entity.enums.ParcelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidParcel
public class CreateParcelRequest {

    @NotNull(message = "Type is required")
    private ParcelType type;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;

    @NotBlank(message = "Destination address is required")
    private String destinationAddress;

    // fragile
    private String handlingInstructions;

    // frigo
    private Double temperatureMin;
    private Double temperatureMax;
}