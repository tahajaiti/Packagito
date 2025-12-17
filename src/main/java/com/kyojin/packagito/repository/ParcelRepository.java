package com.kyojin.packagito.repository;

import com.kyojin.packagito.entity.enums.ParcelStatus;
import com.kyojin.packagito.entity.enums.ParcelType;
import com.kyojin.packagito.entity.parcel.Parcel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelRepository extends MongoRepository<Parcel, String> {

    Page<Parcel> findAllByCarrierId(String carrierId, Pageable pageable);

    Page<Parcel> findAllByDestinationAddressContainingIgnoreCase(String destination, Pageable pageable);

    Page<Parcel> findAllByCarrierIdAndDestinationAddressContainingIgnoreCase(String carrierId, String destination, Pageable pageable);
}