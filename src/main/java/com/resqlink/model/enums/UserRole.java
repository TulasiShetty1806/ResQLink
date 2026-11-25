package com.resqlink.model.enums;

public enum UserRole {
    ROLE_COORDINATOR("Incident Coordinator / Admin"),
    ROLE_ORGANIZATION("Relief Organization / NGO"),
    ROLE_CITIZEN("Citizen / Requester"),
    ROLE_LOGISTICS("Logistics & Transport Team");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
