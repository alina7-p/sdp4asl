package com.airport.app;

import com.airport.facade.AdminFacade;
import com.airport.facade.PassengerBoardingFacade;
import com.airport.model.WorkflowRepository;
import com.airport.subsystemA.CheckInRepository;
import com.airport.subsystemA.CheckInValidationRules;
import com.airport.subsystemB.BaggageRepository;
import com.airport.subsystemB.SecurityBaggageRules;
import com.airport.subsystemC.GateRepository;
import com.airport.subsystemC.PassportControlRules;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CheckInRepository checkInRepo = new CheckInRepository();
        CheckInValidationRules checkInRules = new CheckInValidationRules();

        BaggageRepository baggageRepo = new BaggageRepository();
        SecurityBaggageRules baggageRules = new SecurityBaggageRules();

        GateRepository gateRepo = new GateRepository();
        PassportControlRules gateRules = new PassportControlRules();

        WorkflowRepository workflowRepo = new WorkflowRepository();

        PassengerBoardingFacade boardingFacade = new PassengerBoardingFacade(
                checkInRepo, checkInRules,
                baggageRepo, baggageRules,
                gateRepo, gateRules,
                workflowRepo
        );

        AdminFacade adminFacade = new AdminFacade(
                checkInRepo, baggageRepo, gateRepo, workflowRepo
        );

        Scanner scanner = new Scanner(System.in);
        System.out.println("=============================================");
        System.out.println("   AIRPORT OPERATIONS BOARDING SYSTEM (CLI)  ");
        System.out.println("=============================================");

        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. [Happy Path] Successful landing (A -> B -> C)");
            System.out.println("2. [Fail at Step B] Luggage error (Rollback A)");
            System.out.println("3. [Fail at Step C] Passport/visa error (Rollback B -> A)");
            System.out.println("4. Passenger condition inspection (AdminFacade)");
            System.out.println("5. Forced rollback manually (AdminFacade)");
            System.out.println("0. Exit");
            System.out.print("> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter the passenger’s ID (e.g., P101): ");
                    String p1 = scanner.nextLine();
                    System.out.println("\n--- Chain launch (Happy Path) ---");
                    boolean res1 = boardingFacade.processBoardingChain(p1, "KC123", 20.0, false, true);
                    System.out.println("The result of executing the chain: " + (res1 ? "SUCCESS" : "REJECTED AND REVERTED"));
                    break;

                case "2":
                    System.out.print("Enter the passenger’s ID (e.g., P102): ");
                    String p2 = scanner.nextLine();
                    System.out.println("\n--- Chain launch (Baggage malfunction: overweight by 40 kg) ---");
                    boolean res2 = boardingFacade.processBoardingChain(p2, "DV456", 40.0, false, true);
                    System.out.println("The result of the chain execution is: " + (res2 ? "SUCCESS" : "REJECTED AND ROLLED BACK"));
                    break;

                case "3":
                    System.out.print("Enter the passenger’s ID (e.g., P103): ");
                    String p3 = scanner.nextLine();
                    System.out.println("\n--- Chain launch (Control failure: No visa) ---");
                    boolean res3 = boardingFacade.processBoardingChain(p3, "KC789", 18.0, false, false);
                    System.out.println("The result of executing the chain: " + (res3 ? "SUCCESS" : "DECLINED AND REVERTED"));
                    break;

                case "4":
                    System.out.print("Enter the passenger ID for verification: ");
                    String p4 = scanner.nextLine();
                    System.out.println(adminFacade.inspectPassengerState(p4));
                    break;

                case "5":
                    System.out.print("Enter the passenger ID for manual rollback: ");
                    String p5 = scanner.nextLine();
                    adminFacade.forceManualCompensation(p5);
                    System.out.println("Forced compensation has been carried out.");
                    break;

                case "0":
                    System.out.println("The program is terminating.");
                    return;

                default:
                    System.out.println("Incorrect choice. Try again.");
            }
        }
    }
}
