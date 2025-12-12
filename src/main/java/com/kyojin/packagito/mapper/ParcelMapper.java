package com.kyojin.packagito.mapper;


import com.kyojin.packagito.dto.request.CreateParcelRequest;
import com.kyojin.packagito.dto.request.UpdateParcelRequest;
import com.kyojin.packagito.dto.response.ParcelDTO;
import com.kyojin.packagito.entity.enums.ParcelStatus;
import com.kyojin.packagito.entity.enums.ParcelType;
import com.kyojin.packagito.entity.parcel.FragileParcel;
import com.kyojin.packagito.entity.parcel.FrigoParcel;
import com.kyojin.packagito.entity.parcel.Parcel;
import com.kyojin.packagito.entity.parcel.StandardParcel;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = {ParcelType.class, ParcelStatus.class})
public interface ParcelMapper {

    default Parcel toParcel(CreateParcelRequest request) {
        return switch (request.getType()) {
            case STANDARD -> toStandardParcel(request);
            case FRAGILE -> toFragileParcel(request);
            case FRIGO -> toFrigoParcel(request);
        };
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", expression = "java(ParcelType.STANDARD)")
    @Mapping(target = "status", expression = "java(ParcelStatus.PENDING)")
    @Mapping(target = "carrierId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StandardParcel toStandardParcel(CreateParcelRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", expression = "java(ParcelType.FRAGILE)")
    @Mapping(target = "status", expression = "java(ParcelStatus.PENDING)")
    @Mapping(target = "carrierId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FragileParcel toFragileParcel(CreateParcelRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", expression = "java(ParcelType.FRIGO)")
    @Mapping(target = "status", expression = "java(ParcelStatus.PENDING)")
    @Mapping(target = "carrierId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FrigoParcel toFrigoParcel(CreateParcelRequest request);

    @Mapping(target = "handlingInstructions", source = "parcel", qualifiedByName = "extractHandlingInstructions")
    @Mapping(target = "temperatureMin", source = "parcel", qualifiedByName = "extractTemperatureMin")
    @Mapping(target = "temperatureMax", source = "parcel", qualifiedByName = "extractTemperatureMax")
    @Mapping(target = "carrierUsername", ignore = true)
    ParcelDTO toDTO(Parcel parcel);


    // update

    default void updateParcel(UpdateParcelRequest request, Parcel parcel) {
        switch (parcel) {
            case StandardParcel standard -> updateStandardParcel(request, standard);
            case FragileParcel fragile -> updateFragileParcel(request, fragile);
            case FrigoParcel frigo -> updateFrigoParcel(request, frigo);
            default -> throw new IllegalArgumentException("Unknown parcel type");
        }
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "carrierId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateStandardParcel(UpdateParcelRequest request, @MappingTarget StandardParcel parcel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "carrierId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFragileParcel(UpdateParcelRequest request, @MappingTarget FragileParcel parcel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "carrierId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFrigoParcel(UpdateParcelRequest request, @MappingTarget FrigoParcel parcel);


    // helpers

    @Named("extractHandlingInstructions")
    default String extractHandlingInstructions(Parcel parcel) {
        return parcel instanceof FragileParcel fragile ? fragile.getHandlingInstructions() : null;
    }

    @Named("extractTemperatureMin")
    default Double extractTemperatureMin(Parcel parcel) {
        return parcel instanceof FrigoParcel frigo ? frigo.getTemperatureMin() : null;
    }

    @Named("extractTemperatureMax")
    default Double extractTemperatureMax(Parcel parcel) {
        return parcel instanceof FrigoParcel frigo ? frigo.getTemperatureMax() : null;
    }
}
