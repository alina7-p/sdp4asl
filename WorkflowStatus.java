package com.airport.model;

public enum WorkflowStatus {
    INITIATED,
    CHECKIN_COMPLETED,      // A committed
    BAGGAGE_PROCESSING,    // B in progress
    BAGGAGE_COMPLETED,     // B committed
    GATE_CLEARED,          // C committed -> SUCCESS
    FAILED_AT_BAGGAGE,     // B failed -> rollback A
    FAILED_AT_GATE,        // C failed -> rollback B then A
    COMPENSATED
}