package com.airport.subsystemB;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class BaggageRepository {
    private final Path storagePath = Paths.get("data_subsystem_b.csv");

    public BaggageRepository() {
        try {
            if (!Files.exists(storagePath)) {
                Files.createFile(storagePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage for Subsystem B", e);
        }
    }

    public synchronized void save(BaggageRecord record) {
        Map<String, BaggageRecord> allRecords = loadAll();
        allRecords.put(record.getPassengerId(), record);
        writeAll(allRecords.values());
    }

    public synchronized BaggageRecord findByPassengerId(String passengerId) {
        return loadAll().get(passengerId);
    }

    private Map<String, BaggageRecord> loadAll() {
        Map<String, BaggageRecord> records = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(storagePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    BaggageRecord record = new BaggageRecord(
                            parts[0],
                            parts[1],
                            Double.parseDouble(parts[2]),
                            BaggageStatus.valueOf(parts[3])
                    );
                    records.put(record.getPassengerId(), record);
                }
            }
        } catch (IOException e) {
        }
        return records;
    }

    private void writeAll(Collection<BaggageRecord> records) {
        try (BufferedWriter writer = Files.newBufferedWriter(storagePath)) {
            for (BaggageRecord r : records) {
                writer.write(r.getBaggageTag() + "," + r.getPassengerId() + "," + r.getWeightKg() + "," + r.getStatus().name());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist Subsystem B data", e);
        }
    }
}