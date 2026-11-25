package com.resqlink.model.enums;

public enum IncidentType {
    FLOOD("Floods & Water Inundation", "bi-water", "#0284c7"),
    FIRE("Wildfire & Urban Fire", "bi-fire", "#dc2626"),
    EARTHQUAKE("Earthquake & Structural Collapse", "bi-activity", "#d97706"),
    CYCLONE("Cyclone / Hurricane / Severe Storm", "bi-tornado", "#7c3aed"),
    LANDSLIDE("Landslide & Mudflow", "bi-triangle-half", "#92400e"),
    INDUSTRIAL_ACCIDENT("Industrial & Chemical Hazard", "bi-radioactive", "#b45309"),
    TRANSPORT_ACCIDENT("Major Transit & Railway Crash", "bi-car-front-fill", "#4b5563"),
    MEDICAL_OUTBREAK("Epidemic / Medical Outbreak", "bi-capsule", "#059669");

    private final String label;
    private final String icon;
    private final String color;

    IncidentType(String label, String icon, String color) {
        this.label = label;
        this.icon = icon;
        this.color = color;
    }

    public String getLabel() { return label; }
    public String getIcon() { return icon; }
    public String getColor() { return color; }
}
