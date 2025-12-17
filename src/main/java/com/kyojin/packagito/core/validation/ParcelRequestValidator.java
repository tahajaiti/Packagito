package com.kyojin.packagito.core.validation;

import com.kyojin.packagito.core.annotation.ValidParcel;
import com.kyojin.packagito.dto.request.CreateParcelRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ParcelRequestValidator implements ConstraintValidator<ValidParcel, CreateParcelRequest> {

    @Override
    public boolean isValid(CreateParcelRequest request, ConstraintValidatorContext context) {
        if (request.getType() == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        return switch (request.getType()) {
            case FRAGILE -> validateFragile(request, context);
            case FRIGO -> validateFrigo(request, context);
            case STANDARD -> true;
        };
    }

    private boolean validateFragile(CreateParcelRequest request, ConstraintValidatorContext context) {
        if (request.getHandlingInstructions() == null || request.getHandlingInstructions().isBlank()) {
            context.buildConstraintViolationWithTemplate("Handling instructions are required for fragile parcels")
                    .addPropertyNode("handlingInstructions")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }


    private boolean validateFrigo(CreateParcelRequest request, ConstraintValidatorContext context) {
        boolean valid = true;

        if (request.getTemperatureMin() == null) {
            context.buildConstraintViolationWithTemplate("Minimum temperature is required for frigo parcels")
                    .addPropertyNode("temperatureMin")
                    .addConstraintViolation();
            valid = false;
        }

        if (request.getTemperatureMax() == null) {
            context.buildConstraintViolationWithTemplate("Maximum temperature is required for frigo parcels")
                    .addPropertyNode("temperatureMax")
                    .addConstraintViolation();
            valid = false;
        }

        if (valid && request.getTemperatureMin() >= request.getTemperatureMax()) {
            context.buildConstraintViolationWithTemplate("Maximum temperature must be greater than minimum temperature")
                    .addPropertyNode("temperatureMax")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
