package com.airport;

import com.airport.facade.PassengerBoardingFacade;
import com.airport.model.*;
import com.airport.subsystemA.*;
import com.airport.subsystemB.*;
import com.airport.subsystemC.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class PassengerBoardingFacadeTest {

    private CheckInRepository checkInRepo;
    private CheckInValidationRules checkInRules;
    private BaggageRepository baggageRepo;
    private SecurityBaggageRules baggageRules;
    private GateRepository gateRepo;
    private PassportControlRules gateRules;
    private WorkflowRepository workflowRepo;
    private PassengerBoardingFacade facade;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get("data_subsystem_a.csv"));
        Files.deleteIfExists(Paths.get("data_subsystem_b.csv"));
        Files.deleteIfExists(Paths.get("data_subsystem_c.csv"));
        Files.deleteIfExists(Paths.get("data_workflow.csv"));

        checkInRepo = new CheckInRepository();
        checkInRules = new CheckInValidationRules();
        baggageRepo = new BaggageRepository();
        baggageRules = new SecurityBaggageRules();
        gateRepo = new GateRepository();
        gateRules = new PassportControlRules();
        workflowRepo = new WorkflowRepository();

        facade = new PassengerBoardingFacade(
                checkInRepo, checkInRules,
                baggageRepo, baggageRules,
                gateRepo, gateRules,
                workflowRepo
        );
    }

    private void givenPassengerBoardingRequest(String passengerId, String flight, double bagWeight,
                                               boolean hasProhibited, boolean visaValid,
                                               boolean expectedResult) {
        boolean result = facade.processBoardingChain(passengerId, flight, bagWeight, hasProhibited, visaValid);
        assertEquals(expectedResult, result, "Workflow execution result mismatch for passenger: " + passengerId);
    }

    @Test
    void testHappyPath_AllSubsystemsSucceed() {

        givenPassengerBoardingRequest("P001", "KC123", 15.0, false, true, true);

        assertEquals(CheckInStatus.CHECKED_IN, checkInRepo.findById("P001").getStatus());
        assertEquals(BaggageStatus.ACCEPTED, baggageRepo.findByPassengerId("P001").getStatus());
        assertEquals(GateStatus.CLEARED, gateRepo.findByPassengerId("P001").getStatus());
        assertEquals(WorkflowStatus.GATE_CLEARED, workflowRepo.findByPassengerId("P001").getStatus());
    }

    @Test
    void testCompensationDepth1_BaggageFails_RollsBackCheckIn() {

        givenPassengerBoardingRequest("P002", "KC123", 45.0, false, true, false);

        assertEquals(CheckInStatus.CANCELLED, checkInRepo.findById("P002").getStatus());
        assertNull(baggageRepo.findByPassengerId("P002"));
        assertEquals(WorkflowStatus.COMPENSATED, workflowRepo.findByPassengerId("P002").getStatus());
    }

    @Test
    void testCompensationDepth2_PassportFails_RollsBackBaggageAndCheckIn() {

        givenPassengerBoardingRequest("P003", "KC123", 20.0, false, false, false);

        assertEquals(CheckInStatus.CANCELLED, checkInRepo.findById("P003").getStatus());
        assertEquals(BaggageStatus.RETURNED, baggageRepo.findByPassengerId("P003").getStatus());
        assertEquals(WorkflowStatus.COMPENSATED, workflowRepo.findByPassengerId("P003").getStatus());
    }

    @Test
    void testPersistenceReload_DataSurvivesApplicationRestart() {
        givenPassengerBoardingRequest("P004", "KC123", 20.0, false, true, true);

        CheckInRepository reloadedCheckIn = new CheckInRepository();
        BaggageRepository reloadedBaggage = new BaggageRepository();
        WorkflowRepository reloadedWorkflow = new WorkflowRepository();

        assertNotNull(reloadedCheckIn.findById("P004"));
        assertEquals(CheckInStatus.CHECKED_IN, reloadedCheckIn.findById("P004").getStatus());
        assertEquals(BaggageStatus.ACCEPTED, reloadedBaggage.findByPassengerId("P004").getStatus());
        assertEquals(WorkflowStatus.GATE_CLEARED, reloadedWorkflow.findByPassengerId("P004").getStatus());
    }
}