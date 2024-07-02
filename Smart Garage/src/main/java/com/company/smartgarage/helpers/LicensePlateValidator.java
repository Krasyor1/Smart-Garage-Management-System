package com.company.smartgarage.helpers;

import com.company.smartgarage.helpers.annotations.ValidLicencePlate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LicensePlateValidator implements ConstraintValidator<ValidLicencePlate, String> {


    @Override
    public void initialize(ValidLicencePlate licencePlate) {
        ConstraintValidator.super.initialize(licencePlate);
    }

    @Override
    public boolean isValid(String licensePlate, ConstraintValidatorContext context) {

        return (licensePlate != null && licensePlate.matches("(E|A|B|BT|BH|BP|EB|TX|K|KH|OB|M|PA|PK|EH|PB|PP|P|CC|CH|CM|CO|C|CA|CB|CT|T|X|H|Y)\\d{4}[ABEKMHOPCTYX]{2}"));

    }
}
