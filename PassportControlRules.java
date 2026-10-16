package com.airport.subsystemC;

public class PassportControlRules {

    public boolean verifyPassport(String passportNumber, boolean visaValid) {
        return passportNumber != null && !passportNumber.trim().isEmpty() && visaValid;
    }
}