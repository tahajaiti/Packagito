package com.kyojin.packagito.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateParcelRequest {

    @Positive(message = "Weight must be positive")
    private Double weight;

    private String destinationAddress;

    // fragile
    private String handlingInstructions;

    // frigo
    private Double temperatureMin;

    private Double temperatureMax;
}