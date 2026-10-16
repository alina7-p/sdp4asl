package com.airport.subsystemA;

public class PassengerRecord {
    private final String passengerId;
    private final String flightNumber;
    private CheckInStatus status;

    public PassengerRecord(String passengerId, String flightNumber) {
        this.passengerId = passengerId;
        this.flightNumber = flightNumber;
        this.status = CheckInStatus.NEW;
    }

    public PassengerRecord(String passengerId, String flightNumber, CheckInStatus status) {
        this.passengerId = passengerId;
        this.flightNumber = flightNumber;
        this.status = status;
    }

    public void updateStatus(CheckInStatus newStatus) {
        if (this.status == CheckInStatus.CANCELLED && newStatus == CheckInStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot check-in a cancelled ticket.");
        }
        this.status = newStatus;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public CheckInStatus getStatus() {
        return status;
    }
}