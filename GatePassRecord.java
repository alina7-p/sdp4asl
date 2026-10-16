package com.airport.subsystemC;

public class GatePassRecord {
    private final String passengerId;
    private final String gateNumber;
    private GateStatus status;

    public GatePassRecord(String passengerId, String gateNumber) {
        this.passengerId = passengerId;
        this.gateNumber = gateNumber;
        this.status = GateStatus.NEW;
    }

    public GatePassRecord(String passengerId, String gateNumber, GateStatus status) {
        this.passengerId = passengerId;
        this.gateNumber = gateNumber;
        this.status = status;
    }

    public void updateStatus(GateStatus newStatus) {

        if (this.status == GateStatus.REVOKED && newStatus == GateStatus.CLEARED) {
            throw new IllegalStateException("Cannot clear gate pass that was permanently revoked.");
        }
        this.status = newStatus;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getGateNumber() {
        return gateNumber;
    }

    public GateStatus getStatus() {
        return status;
    }

    public void setStatus(GateStatus status) {
        this.status = status;
    }
}