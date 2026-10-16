package com.airport.subsystemA;

public class CheckInValidationRules {

    public boolean isValidForCheckIn(String flightNumber) {
        return flightNumber != null && flightNumber.matches("^[A-Z]{2}\\d{3,4}$");
    }
}