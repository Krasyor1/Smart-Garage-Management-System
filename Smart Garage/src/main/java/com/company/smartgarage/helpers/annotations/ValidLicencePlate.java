package com.company.smartgarage.helpers.annotations;

import com.company.smartgarage.helpers.LicensePlateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LicensePlateValidator.class)
@Target({ElementType.TYPE,ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLicencePlate {
    String message() default "Invalid License Plate";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
