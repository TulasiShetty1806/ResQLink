package com.resqlink.model.enums;

public enum TransportMode {
    AMBULANCE("Emergency Ambulance", "bi-truck", 70.0),
    RESCUE_BOAT("Inflatable Rescue Boat", "bi-water", 35.0),
    CARGO_TRUCK("Heavy Cargo Truck", "bi-truck-flatbed", 50.0),
    AIR_DROP_DRONE("Supply Airdrop Drone", "bi-airplane", 90.0),
    OFFROAD_4X4("4x4 Disaster Response Van", "bi-car-front", 60.0),
    HELICOPTER("Disaster Aviation Helicopter", "bi-cursor-fill", 160.0);

    private final String label;
    private final String icon;
    private final double averageSpeedKmh;

    TransportMode(String label, String icon, double averageSpeedKmh) {
        this.label = label;
        this.icon = icon;
        this.averageSpeedKmh = averageSpeedKmh;
    }

    public String getLabel() { return label; }
    public String getIcon() { return icon; }
    public double getAverageSpeedKmh() { return averageSpeedKmh; }
}
