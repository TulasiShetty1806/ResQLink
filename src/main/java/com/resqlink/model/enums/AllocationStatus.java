package com.resqlink.model.enums;

public enum AllocationStatus {
    PROPOSED("Proposed by Engine", "warning"),
    CONFIRMED("Confirmed by Coordinator", "primary"),
    DISPATCHED("Dispatched to Logistics", "info"),
    COMPLETED("Completed / Fulfilled", "success"),
    CANCELLED("Cancelled", "secondary");

    private final String label;
    private final String badgeClass;

    AllocationStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() { return label; }
    public String getBadgeClass() { return badgeClass; }
}
