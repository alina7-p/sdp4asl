package com.airport.subsystemB;

public class SecurityBaggageRules {

    public boolean inspectBaggage(double weightKg, boolean hasProhibitedItems) {
        return weightKg > 0 && weightKg <= 32.0 && !hasProhibitedItems;
    }
}