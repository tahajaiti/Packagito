package com.kyojin.packagito.entity.parcel;

import com.kyojin.packagito.entity.enums.ParcelStatus;
import com.kyojin.packagito.entity.enums.ParcelType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@SuperBuilder
@Document(collection = "parcels")
@AllArgsConstructor
@NoArgsConstructor
public abstract class Parcel {

    @Id
    private String id;

    @Indexed
    private ParcelType type;

    private Double weight;

    @Indexed
    private String destinationAddress;

    @Indexed
    private ParcelStatus status;

    @Indexed
    private String carrierId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}