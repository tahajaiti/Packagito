package com.kyojin.packagito.core.annotation;

import com.kyojin.packagito.core.validation.ParcelRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ParcelRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidParcel {
    String message() default "Invalid parcel request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
