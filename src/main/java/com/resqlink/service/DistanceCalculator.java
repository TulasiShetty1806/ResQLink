package com.resqlink.service;

import com.resqlink.model.enums.TransportMode;
import org.springframework.stereotype.Component;

@Component
public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double ROAD_DETOUR_FACTOR = 1.25; // Real-world road distance vs straight-line

    /**
     * Calculates the great-circle distance between two geographic coordinates using the Haversine formula.
     * @return distance in kilometers, adjusted for realistic road travel
     */
    public double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double straightLineKm = EARTH_RADIUS_KM * c;

        // Multiply by road curvature factor for realistic ground transport
        double roadDistance = straightLineKm * ROAD_DETOUR_FACTOR;
        return Math.round(roadDistance * 10.0) / 10.0;
    }

    /**
     * Estimates travel duration in minutes given the distance and transport mode.
     */
    public int estimateDurationMinutes(double distanceKm, TransportMode mode) {
        double speed = (mode != null) ? mode.getAverageSpeedKmh() : 50.0;
        double hours = distanceKm / speed;
        int minutes = (int) Math.ceil(hours * 60) + 10; // +10 minutes loading/prep buffer
        return Math.max(minutes, 10);
    }
}
