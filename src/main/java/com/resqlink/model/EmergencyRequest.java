package com.resqlink.model;

import com.resqlink.model.enums.RequestStatus;
import com.resqlink.model.enums.RequestUrgency;
import com.resqlink.model.enums.ResourceCategory;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_requests")
public class EmergencyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String trackingCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ResourceCategory category;

    @Column(nullable = false, length = 150)
    private String itemName;

    @Column(nullable = false)
    private Integer requestedQuantity = 1;

    @Column(nullable = false, length = 30)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RequestUrgency urgency;

    @Column(nullable = false)
    private Double priorityScore = 0.0; // 0 - 100 calculated by priority algorithm

    @Column(nullable = false, length = 100)
    private String requesterName;

    @Column(nullable = false, length = 20)
    private String requesterPhone;

    @Column(nullable = false, length = 255)
    private String requesterAddress;

    @Column(nullable = false)
    private Double deliveryLatitude;

    @Column(nullable = false)
    private Double deliveryLongitude;

    private Integer affectedPeopleCount = 1;

    private boolean hasInjuredOrInfants = false;

    @Column(columnDefinition = "TEXT")
    private String specialInstructions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RequestStatus status = RequestStatus.PENDING_TRIAGE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(length = 6)
    private String verificationPin; // e.g. 4-digit code for delivery confirmation

    public EmergencyRequest() {}

    public EmergencyRequest(String trackingCode, Incident incident, ResourceCategory category, String itemName, Integer requestedQuantity, String unit, RequestUrgency urgency, String requesterName, String requesterPhone, String requesterAddress, Double deliveryLatitude, Double deliveryLongitude, Integer affectedPeopleCount, boolean hasInjuredOrInfants, String specialInstructions) {
        this.trackingCode = trackingCode;
        this.incident = incident;
        this.category = category;
        this.itemName = itemName;
        this.requestedQuantity = requestedQuantity;
        this.unit = unit;
        this.urgency = urgency;
        this.requesterName = requesterName;
        this.requesterPhone = requesterPhone;
        this.requesterAddress = requesterAddress;
        this.deliveryLatitude = deliveryLatitude;
        this.deliveryLongitude = deliveryLongitude;
        this.affectedPeopleCount = affectedPeopleCount;
        this.hasInjuredOrInfants = hasInjuredOrInfants;
        this.specialInstructions = specialInstructions;
        this.status = RequestStatus.PENDING_TRIAGE;
        this.requestedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Generate a 4-digit delivery pin
        this.verificationPin = String.format("%04d", (int)(Math.random() * 9000) + 1000);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }

    public Incident getIncident() { return incident; }
    public void setIncident(Incident incident) { this.incident = incident; }

    public ResourceCategory getCategory() { return category; }
    public void setCategory(ResourceCategory category) { this.category = category; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public RequestUrgency getUrgency() { return urgency; }
    public void setUrgency(RequestUrgency urgency) { this.urgency = urgency; }

    public Double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Double priorityScore) { this.priorityScore = priorityScore; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getRequesterPhone() { return requesterPhone; }
    public void setRequesterPhone(String requesterPhone) { this.requesterPhone = requesterPhone; }

    public String getRequesterAddress() { return requesterAddress; }
    public void setRequesterAddress(String requesterAddress) { this.requesterAddress = requesterAddress; }

    public Double getDeliveryLatitude() { return deliveryLatitude; }
    public void setDeliveryLatitude(Double deliveryLatitude) { this.deliveryLatitude = deliveryLatitude; }

    public Double getDeliveryLongitude() { return deliveryLongitude; }
    public void setDeliveryLongitude(Double deliveryLongitude) { this.deliveryLongitude = deliveryLongitude; }

    public Integer getAffectedPeopleCount() { return affectedPeopleCount; }
    public void setAffectedPeopleCount(Integer affectedPeopleCount) { this.affectedPeopleCount = affectedPeopleCount; }

    public boolean isHasInjuredOrInfants() { return hasInjuredOrInfants; }
    public void setHasInjuredOrInfants(boolean hasInjuredOrInfants) { this.hasInjuredOrInfants = hasInjuredOrInfants; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getVerificationPin() { return verificationPin; }
    public void setVerificationPin(String verificationPin) { this.verificationPin = verificationPin; }
}
