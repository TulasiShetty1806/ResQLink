package com.resqlink.model.enums;

public enum IncidentStatus {
    ACTIVE("Active / Response Ongoing", "danger"),
    CONTAINED("Contained / Stabilizing", "warning"),
    RESOLVED("Resolved / Closed", "success");

    private final String label;
    private final String badgeClass;

    IncidentStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() { return label; }
    public String getBadgeClass() { return badgeClass; }
}
