package com.resqlink.model.enums;

public enum RequestUrgency {
    IMMEDIATE_LIFE_THREAT("Immediate Life Threat (< 2 hrs)", 40, "danger"),
    CRITICAL_HOURS("Critical (< 6 hrs)", 30, "warning"),
    URGENT_DAY("Urgent (< 24 hrs)", 20, "primary"),
    STANDARD_SUPPLY("Standard Support (> 24 hrs)", 10, "secondary");

    private final String label;
    private final int weight;
    private final String badgeClass;

    RequestUrgency(String label, int weight, String badgeClass) {
        this.label = label;
        this.weight = weight;
        this.badgeClass = badgeClass;
    }

    public String getLabel() { return label; }
    public int getWeight() { return weight; }
    public String getBadgeClass() { return badgeClass; }
}
