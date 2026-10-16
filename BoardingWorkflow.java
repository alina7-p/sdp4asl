package com.airport.model;

public class BoardingWorkflow {
    private final String passengerId;
    private WorkflowStatus status;

    public BoardingWorkflow(String passengerId) {
        this.passengerId = passengerId;
        this.status = WorkflowStatus.INITIATED;
    }

    public BoardingWorkflow(String passengerId, WorkflowStatus status) {
        this.passengerId = passengerId;
        this.status = status;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }
}