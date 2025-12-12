package com.kyojin.packagito.entity.user;

import com.kyojin.packagito.entity.enums.CarrierStatus;
import com.kyojin.packagito.entity.enums.Specialty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = "users")
@TypeAlias("carrier")
public class Carrier extends User {

    @Builder.Default
    private CarrierStatus status = CarrierStatus.AVAILABLE;

    private Specialty specialty;
}