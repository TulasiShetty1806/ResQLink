package com.resqlink.model.enums;

public enum ResourceCategory {
    MEDICAL_SUPPLIES("Medical Supplies & Trauma Kits", "bi-heart-pulse-fill", "#dc2626"),
    FOOD_AND_WATER("Food Packets & Drinking Water", "bi-egg-fried", "#059669"),
    RESCUE_EQUIPMENT("Rescue Boats, Cutters & Ropes", "bi-life-preserver", "#0284c7"),
    SHELTER_BEDDING("Tents, Tarpaulins & Blankets", "bi-house-heart-fill", "#d97706"),
    POWER_AND_COMMUNICATION("Generators, Solar & Sat-Phones", "bi-lightning-charge-fill", "#7c3aed"),
    PERSONNEL_VOLUNTEERS("Emergency Responders & Doctors", "bi-people-fill", "#0891b2");

    private final String label;
    private final String icon;
    private final String color;

    ResourceCategory(String label, String icon, String color) {
        this.label = label;
        this.icon = icon;
        this.color = color;
    }

    public String getLabel() { return label; }
    public String getIcon() { return icon; }
    public String getColor() { return color; }
}
