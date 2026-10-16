package com.airport.model;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WorkflowRepository {
    private final Path storagePath = Paths.get("data_workflow.csv");

    public WorkflowRepository() {
        try {
            if (!Files.exists(storagePath)) {
                Files.createFile(storagePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage for Workflows", e);
        }
    }

    public synchronized void save(BoardingWorkflow workflow) {
        Map<String, BoardingWorkflow> all = loadAll();
        all.put(workflow.getPassengerId(), workflow);
        writeAll(all.values());
    }

    public synchronized BoardingWorkflow findByPassengerId(String passengerId) {
        return loadAll().get(passengerId);
    }

    private Map<String, BoardingWorkflow> loadAll() {
        Map<String, BoardingWorkflow> map = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(storagePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    map.put(parts[0], new BoardingWorkflow(parts[0], WorkflowStatus.valueOf(parts[1])));
                }
            }
        } catch (IOException e) {

        }
        return map;
    }

    private void writeAll(Collection<BoardingWorkflow> workflows) {
        try (BufferedWriter writer = Files.newBufferedWriter(storagePath)) {
            for (BoardingWorkflow w : workflows) {
                writer.write(w.getPassengerId() + "," + w.getStatus().name());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist Workflow data", e);
        }
    }
}