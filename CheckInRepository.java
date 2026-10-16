package com.airport.subsystemA;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CheckInRepository {
    private final Path storagePath = Paths.get("data_subsystem_a.csv");

    public CheckInRepository() {
        try {
            if (!Files.exists(storagePath)) {
                Files.createFile(storagePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage for Subsystem A", e);
        }
    }

    public synchronized void save(PassengerRecord record) {
        Map<String, PassengerRecord> allRecords = loadAll();
        allRecords.put(record.getPassengerId(), record);
        writeAll(allRecords.values());
    }

    public synchronized PassengerRecord findById(String passengerId) {
        return loadAll().get(passengerId);
    }

    private Map<String, PassengerRecord> loadAll() {
        Map<String, PassengerRecord> records = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(storagePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    PassengerRecord r = new PassengerRecord(parts[0], parts[1], CheckInStatus.valueOf(parts[2]));
                    records.put(r.getPassengerId(), r);
                }
            }
        } catch (IOException e) {

        }
        return records;
    }

    private void writeAll(Collection<PassengerRecord> records) {
        try (BufferedWriter writer = Files.newBufferedWriter(storagePath)) {
            for (PassengerRecord r : records) {
                writer.write(r.getPassengerId() + "," + r.getFlightNumber() + "," + r.getStatus().name());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist Subsystem A data", e);
        }
    }
}