package com.kyojin.packagito.entity.parcel;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import com.kyojin.packagito.entity.enums.ParcelType;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = "parcels")
@TypeAlias("standard")
public class StandardParcel extends Parcel {

    {
        setParcelType(ParcelType.STANDARD);
    }

}