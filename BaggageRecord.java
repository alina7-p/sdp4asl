package com.airport.subsystemB;

public class BaggageRecord {
    private final String baggageTag;
    private final String passengerId;
    private final double weightKg;
    private BaggageStatus status;

    public BaggageRecord(String baggageTag, String passengerId, double weightKg) {
        this.baggageTag = baggageTag;
        this.passengerId = passengerId;
        this.weightKg = weightKg;
        this.status = BaggageStatus.NEW;
    }

    public BaggageRecord(String baggageTag, String passengerId, double weightKg, BaggageStatus status) {
        this.baggageTag = baggageTag;
        this.passengerId = passengerId;
        this.weightKg = weightKg;
        this.status = status;
    }

    public void updateStatus(BaggageStatus newStatus) {
        if (this.status == BaggageStatus.RETURNED && newStatus == BaggageStatus.ACCEPTED) {
            throw new IllegalStateException("Cannot accept baggage that was already returned.");
        }
        this.status = newStatus;
    }

    public String getBaggageTag() {
        return baggageTag;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public BaggageStatus getStatus() {
        return status;
    }

    public void setStatus(BaggageStatus status) {
        this.status = status;
    }
}