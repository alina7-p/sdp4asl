package com.airport.facade;

import com.airport.model.*;
import com.airport.subsystemA.*;
import com.airport.subsystemB.*;
import com.airport.subsystemC.*;

public class PassengerBoardingFacade {
    private final CheckInRepository checkInRepo;
    private final CheckInValidationRules checkInRules;

    private final BaggageRepository baggageRepo;
    private final SecurityBaggageRules baggageRules;

    private final GateRepository gateRepo;
    private final PassportControlRules gateRules;

    private final WorkflowRepository workflowRepo;

    public PassengerBoardingFacade(CheckInRepository checkInRepo, CheckInValidationRules checkInRules,
                                   BaggageRepository baggageRepo, SecurityBaggageRules baggageRules,
                                   GateRepository gateRepo, PassportControlRules gateRules,
                                   WorkflowRepository workflowRepo) {
        this.checkInRepo = checkInRepo;
        this.checkInRules = checkInRules;
        this.baggageRepo = baggageRepo;
        this.baggageRules = baggageRules;
        this.gateRepo = gateRepo;
        this.gateRules = gateRules;
        this.workflowRepo = workflowRepo;
    }

    public boolean processBoardingChain(String passengerId, String flight, double bagWeight,
                                        boolean hasProhibited, boolean visaValid) {
        BoardingWorkflow workflow = new BoardingWorkflow(passengerId);
        workflowRepo.save(workflow);

        if (!checkInRules.isValidForCheckIn(flight)) {
            throw new IllegalArgumentException("Invalid Flight Code: " + flight);
        }
        PassengerRecord pRecord = new PassengerRecord(passengerId, flight);
        pRecord.updateStatus(CheckInStatus.CHECKED_IN);
        checkInRepo.save(pRecord);

        workflow.setStatus(WorkflowStatus.CHECKIN_COMPLETED);
        workflowRepo.save(workflow);

        try {
            workflow.setStatus(WorkflowStatus.BAGGAGE_PROCESSING);
            workflowRepo.save(workflow);

            if (!baggageRules.inspectBaggage(bagWeight, hasProhibited)) {
                throw new SecurityException("Baggage security screening failed!");
            }
            BaggageRecord bRecord = new BaggageRecord("BAG-" + passengerId, passengerId, bagWeight);
            bRecord.setStatus(BaggageStatus.ACCEPTED);
            baggageRepo.save(bRecord);

            workflow.setStatus(WorkflowStatus.BAGGAGE_COMPLETED);
            workflowRepo.save(workflow);
        } catch (Exception e) {

            workflow.setStatus(WorkflowStatus.FAILED_AT_BAGGAGE);
            workflowRepo.save(workflow);

            compensateSubsystemA(passengerId);

            workflow.setStatus(WorkflowStatus.COMPENSATED);
            workflowRepo.save(workflow);
            return false;
        }

        try {
            if (!gateRules.verifyPassport("PASS-" + passengerId, visaValid)) {
                throw new SecurityException("Passport control rejected clearance!");
            }
            GatePassRecord gRecord = new GatePassRecord(passengerId, "Gate-A12");
            gRecord.setStatus(GateStatus.CLEARED);
            gateRepo.save(gRecord);

            workflow.setStatus(WorkflowStatus.GATE_CLEARED);
            workflowRepo.save(workflow);
        } catch (Exception e) {

            workflow.setStatus(WorkflowStatus.FAILED_AT_GATE);
            workflowRepo.save(workflow);

            compensateSubsystemB(passengerId);
            compensateSubsystemA(passengerId);

            workflow.setStatus(WorkflowStatus.COMPENSATED);
            workflowRepo.save(workflow);
            return false;
        }

        return true;
    }

    public void compensateSubsystemA(String passengerId) {
        PassengerRecord record = checkInRepo.findById(passengerId);
        if (record != null) {
            record.updateStatus(CheckInStatus.CANCELLED);
            checkInRepo.save(record);
        }
    }

    public void compensateSubsystemB(String passengerId) {
        BaggageRecord record = baggageRepo.findByPassengerId(passengerId);
        if (record != null) {
            record.setStatus(BaggageStatus.RETURNED);
            baggageRepo.save(record);
        }
    }
}