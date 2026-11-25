package com.resqlink.model.enums;

public enum TransportStatus {
    ASSIGNED("Vehicle Assigned", "secondary"),
    LOADING("Supplies Loading at Depot", "info"),
    IN_TRANSIT("In Transit on Route", "primary"),
    ARRIVED("Arrived at Drop Zone", "warning"),
    DELIVERED("Handover Verified", "success"),
    DELAYED("Delayed / Blocked Road", "danger");

    private final String label;
    private final String badgeClass;

    TransportStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() { return label; }
    public String getBadgeClass() { return badgeClass; }
}
