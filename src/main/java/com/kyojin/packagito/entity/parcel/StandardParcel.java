package com.kyojin.packagito.entity.parcel;

import com.kyojin.packagito.entity.enums.ParcelType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = "parcels")
@TypeAlias("standard")
public class StandardParcel extends Parcel {

    {
        setParcelType(ParcelType.STANDARD);
    }



}