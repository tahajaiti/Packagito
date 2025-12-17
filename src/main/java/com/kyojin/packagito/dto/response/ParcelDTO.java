package com.kyojin.packagito.dto.response;

import com.kyojin.packagito.entity.enums.ParcelStatus;
import com.kyojin.packagito.entity.enums.ParcelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelDTO {

    private String id;
    private ParcelType type;
    private Double weight;
    private String destinationAddress;
    private ParcelStatus status;
    private String carrierId;
    private String carrierUsername;

    // fragile
    private String handlingInstructions;

    // frigo
    private Double temperatureMin;
    private Double temperatureMax;

    private Instant createdAt;
    private Instant updatedAt;
}