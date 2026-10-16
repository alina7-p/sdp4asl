package com.airport.facade;

import com.airport.model.*;
import com.airport.subsystemA.*;
import com.airport.subsystemB.*;
import com.airport.subsystemC.*;

public class AdminFacade {
    private final CheckInRepository checkInRepo;
    private final BaggageRepository baggageRepo;
    private final GateRepository gateRepo;
    private final WorkflowRepository workflowRepo;

    public AdminFacade(CheckInRepository checkInRepo, BaggageRepository baggageRepo,
                       GateRepository gateRepo, WorkflowRepository workflowRepo) {
        this.checkInRepo = checkInRepo;
        this.baggageRepo = baggageRepo;
        this.gateRepo = gateRepo;
        this.workflowRepo = workflowRepo;
    }

    public String inspectPassengerState(String passengerId) {
        BoardingWorkflow w = workflowRepo.findByPassengerId(passengerId);
        PassengerRecord a = checkInRepo.findById(passengerId);
        BaggageRecord b = baggageRepo.findByPassengerId(passengerId);
        GatePassRecord c = gateRepo.findByPassengerId(passengerId);

        return String.format(
                "=== SYSTEM INSPECTION [%s] ===\nWorkflow Status: %s\nSubsystem A (CheckIn): %s\nSubsystem B (Baggage): %s\nSubsystem C (Gate): %s",
                passengerId,
                w != null ? w.getStatus() : "NOT_FOUND",
                a != null ? a.getStatus() : "NONE",
                b != null ? b.getStatus() : "NONE",
                c != null ? c.getStatus() : "NONE"
        );
    }

    public void forceManualCompensation(String passengerId) {
        PassengerRecord a = checkInRepo.findById(passengerId);
        if (a != null) {
            a.updateStatus(CheckInStatus.CANCELLED);
            checkInRepo.save(a);
        }

        BaggageRecord b = baggageRepo.findByPassengerId(passengerId);
        if (b != null) {
            b.setStatus(BaggageStatus.RETURNED);
            baggageRepo.save(b);
        }

        GatePassRecord c = gateRepo.findByPassengerId(passengerId);
        if (c != null) {
            c.setStatus(GateStatus.REVOKED);
            gateRepo.save(c);
        }

        BoardingWorkflow w = workflowRepo.findByPassengerId(passengerId);
        if (w != null) {
            w.setStatus(WorkflowStatus.COMPENSATED);
            workflowRepo.save(w);
        }
    }
}