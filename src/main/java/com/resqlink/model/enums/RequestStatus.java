package com.resqlink.model.enums;

public enum RequestStatus {
    PENDING_TRIAGE("Pending Triage", "secondary"),
    PRIORITIZED("Prioritized / Matching", "warning"),
    ALLOCATED("Resources Allocated", "info"),
    DISPATCHED("In Transit", "primary"),
    DELIVERED("Delivered & Handed Over", "success"),
    CANCELLED("Cancelled", "dark");

    private final String label;
    private final String badgeClass;

    RequestStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() { return label; }
    public String getBadgeClass() { return badgeClass; }
}
