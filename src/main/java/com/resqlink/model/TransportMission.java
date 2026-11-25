package com.resqlink.model;

import com.resqlink.model.enums.TransportMode;
import com.resqlink.model.enums.TransportStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transport_missions")
public class TransportMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id", nullable = false)
    private EmergencyRequest emergencyRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allocation_id", nullable = false)
    private ResourceAllocation resourceAllocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransportMode transportMode;

    @Column(nullable = false, length = 50)
    private String vehicleNumber;

    @Column(nullable = false, length = 100)
    private String driverName;

    @Column(nullable = false, length = 20)
    private String driverPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransportStatus status = TransportStatus.ASSIGNED;

    private Double estimatedDistanceKm;

    private Integer estimatedDurationMinutes;

    private LocalDateTime dispatchTime = LocalDateTime.now();

    private LocalDateTime estimatedArrival;

    private LocalDateTime actualDeliveryTime;

    @Column(columnDefinition = "TEXT")
    private String routeNotes;

    @Column(columnDefinition = "TEXT")
    private String recipientSignatureNotes;

    public TransportMission() {}

    public TransportMission(String trackingNumber, EmergencyRequest emergencyRequest, ResourceAllocation resourceAllocation, TransportMode transportMode, String vehicleNumber, String driverName, String driverPhone, Double estimatedDistanceKm, Integer estimatedDurationMinutes, String routeNotes) {
        this.trackingNumber = trackingNumber;
        this.emergencyRequest = emergencyRequest;
        this.resourceAllocation = resourceAllocation;
        this.transportMode = transportMode;
        this.vehicleNumber = vehicleNumber;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.estimatedDistanceKm = estimatedDistanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.status = TransportStatus.IN_TRANSIT;
        this.dispatchTime = LocalDateTime.now();
        if (estimatedDurationMinutes != null) {
            this.estimatedArrival = this.dispatchTime.plusMinutes(estimatedDurationMinutes);
        }
        this.routeNotes = routeNotes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public EmergencyRequest getEmergencyRequest() { return emergencyRequest; }
    public void setEmergencyRequest(EmergencyRequest emergencyRequest) { this.emergencyRequest = emergencyRequest; }

    public ResourceAllocation getResourceAllocation() { return resourceAllocation; }
    public void setResourceAllocation(ResourceAllocation resourceAllocation) { this.resourceAllocation = resourceAllocation; }

    public TransportMode getTransportMode() { return transportMode; }
    public void setTransportMode(TransportMode transportMode) { this.transportMode = transportMode; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public TransportStatus getStatus() { return status; }
    public void setStatus(TransportStatus status) { this.status = status; }

    public Double getEstimatedDistanceKm() { return estimatedDistanceKm; }
    public void setEstimatedDistanceKm(Double estimatedDistanceKm) { this.estimatedDistanceKm = estimatedDistanceKm; }

    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public LocalDateTime getDispatchTime() { return dispatchTime; }
    public void setDispatchTime(LocalDateTime dispatchTime) { this.dispatchTime = dispatchTime; }

    public LocalDateTime getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(LocalDateTime estimatedArrival) { this.estimatedArrival = estimatedArrival; }

    public LocalDateTime getActualDeliveryTime() { return actualDeliveryTime; }
    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) { this.actualDeliveryTime = actualDeliveryTime; }

    public String getRouteNotes() { return routeNotes; }
    public void setRouteNotes(String routeNotes) { this.routeNotes = routeNotes; }

    public String getRecipientSignatureNotes() { return recipientSignatureNotes; }
    public void setRecipientSignatureNotes(String recipientSignatureNotes) { this.recipientSignatureNotes = recipientSignatureNotes; }
}
