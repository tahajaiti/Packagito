package com.kyojin.packagito.repository;

import com.kyojin.packagito.entity.parcel.Parcel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelRepository extends MongoRepository<Parcel, String> {

}