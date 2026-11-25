package com.resqlink.model.enums;

public enum IncidentSeverity {
    CRITICAL("Critical - Level 4 (Immediate Mass Threat)", 40, "danger"),
    HIGH("High - Level 3 (Severe Impact)", 30, "warning"),
    MEDIUM("Medium - Level 2 (Moderate Impact)", 20, "info"),
    LOW("Low - Level 1 (Minor / Precautionary)", 10, "secondary");

    private final String label;
    private final int weight;
    private final String badgeClass;

    IncidentSeverity(String label, int weight, String badgeClass) {
        this.label = label;
        this.weight = weight;
        this.badgeClass = badgeClass;
    }

    public String getLabel() { return label; }
    public int getWeight() { return weight; }
    public String getBadgeClass() { return badgeClass; }
}
