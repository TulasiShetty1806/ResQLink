package com.resqlink.model;

import com.resqlink.model.enums.IncidentSeverity;
import com.resqlink.model.enums.IncidentStatus;
import com.resqlink.model.enums.IncidentType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String incidentCode;

    @Column(nullable = false, length = 150)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentType incidentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentStatus status = IncidentStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 200)
    private String locationName;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private Double radiusKm = 10.0;

    private Integer affectedPeopleEstimate = 0;

    private Integer casualtiesReported = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime reportedAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Incident() {}

    public Incident(String incidentCode, String title, IncidentType incidentType, IncidentSeverity severity, String description, String locationName, Double latitude, Double longitude, Double radiusKm, Integer affectedPeopleEstimate, Integer casualtiesReported) {
        this.incidentCode = incidentCode;
        this.title = title;
        this.incidentType = incidentType;
        this.severity = severity;
        this.status = IncidentStatus.ACTIVE;
        this.description = description;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
        this.affectedPeopleEstimate = affectedPeopleEstimate;
        this.casualtiesReported = casualtiesReported;
        this.reportedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public IncidentType getIncidentType() { return incidentType; }
    public void setIncidentType(IncidentType incidentType) { this.incidentType = incidentType; }

    public IncidentSeverity getSeverity() { return severity; }
    public void setSeverity(IncidentSeverity severity) { this.severity = severity; }

    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Double radiusKm) { this.radiusKm = radiusKm; }

    public Integer getAffectedPeopleEstimate() { return affectedPeopleEstimate; }
    public void setAffectedPeopleEstimate(Integer affectedPeopleEstimate) { this.affectedPeopleEstimate = affectedPeopleEstimate; }

    public Integer getCasualtiesReported() { return casualtiesReported; }
    public void setCasualtiesReported(Integer casualtiesReported) { this.casualtiesReported = casualtiesReported; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
