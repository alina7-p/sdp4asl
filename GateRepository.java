package com.airport.subsystemC;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GateRepository {
    private final Path storagePath = Paths.get("data_subsystem_c.csv");

    public GateRepository() {
        try {
            if (!Files.exists(storagePath)) {
                Files.createFile(storagePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage for Subsystem C", e);
        }
    }

    public synchronized void save(GatePassRecord record) {
        Map<String, GatePassRecord> allRecords = loadAll();
        allRecords.put(record.getPassengerId(), record);
        writeAll(allRecords.values());
    }

    public synchronized GatePassRecord findByPassengerId(String passengerId) {
        return loadAll().get(passengerId);
    }

    private Map<String, GatePassRecord> loadAll() {
        Map<String, GatePassRecord> records = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(storagePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    GatePassRecord record = new GatePassRecord(
                            parts[0],
                            parts[1],
                            GateStatus.valueOf(parts[2])
                    );
                    records.put(record.getPassengerId(), record);
                }
            }
        } catch (IOException e) {
        }
        return records;
    }

    private void writeAll(Collection<GatePassRecord> records) {
        try (BufferedWriter writer = Files.newBufferedWriter(storagePath)) {
            for (GatePassRecord r : records) {
                writer.write(r.getPassengerId() + "," + r.getGateNumber() + "," + r.getStatus().name());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist Subsystem C data", e);
        }
    }
}