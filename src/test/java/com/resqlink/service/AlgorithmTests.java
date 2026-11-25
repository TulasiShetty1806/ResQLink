package com.resqlink.service;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.Incident;
import com.resqlink.model.enums.IncidentSeverity;
import com.resqlink.model.enums.IncidentType;
import com.resqlink.model.enums.RequestUrgency;
import com.resqlink.model.enums.ResourceCategory;
import com.resqlink.model.enums.TransportMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmTests {

    private DistanceCalculator distanceCalculator;
    private EmergencyRequestService requestService;

    @BeforeEach
    void setUp() {
        distanceCalculator = new DistanceCalculator();
        requestService = new EmergencyRequestService(null, null);
    }

    @Test
    void testHaversineDistanceCalculation() {
        // Distance between Chennai Central (13.0827, 80.2707) and Velachery (12.9815, 80.2180)
        double distance = distanceCalculator.calculateDistanceKm(13.0827, 80.2707, 12.9815, 80.2180);
        assertTrue(distance > 10.0 && distance < 25.0, "Expected road distance between 10km and 25km, got: " + distance);
    }

    @Test
    void testDurationEstimation() {
        double distanceKm = 20.0;
        int durationBoat = distanceCalculator.estimateDurationMinutes(distanceKm, TransportMode.RESCUE_BOAT);
        int durationAmbulance = distanceCalculator.estimateDurationMinutes(distanceKm, TransportMode.AMBULANCE);

        assertTrue(durationBoat > durationAmbulance, "Rescue boat duration should be longer than ambulance for same distance");
    }

    @Test
    void testPriorityScoreCalculation() {
        Incident criticalIncident = new Incident("INC-TEST", "Test Flood", IncidentType.FLOOD, IncidentSeverity.CRITICAL, "desc", "loc", 13.0, 80.0, 10.0, 1000, 2);

        EmergencyRequest highPriorityReq = new EmergencyRequest(
                "REQ-T1", criticalIncident, ResourceCategory.FOOD_AND_WATER, "Water", 50, "Cans",
                RequestUrgency.IMMEDIATE_LIFE_THREAT, "Citizen A", "123", "Street", 13.0, 80.0, 50, true, "help"
        );

        double score = requestService.calculatePriorityScore(highPriorityReq);
        assertTrue(score >= 70.0, "Critical emergency with immediate urgency and injured should score >= 70, got: " + score);
    }
}
