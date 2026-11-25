package com.resqlink.model;

import com.resqlink.model.enums.ResourceCategory;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_items")
public class ResourceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String resourceCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ResourceCategory category;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Integer totalQuantity = 0;

    @Column(nullable = false)
    private Integer availableQuantity = 0;

    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    @Column(nullable = false, length = 30)
    private String unit; // "kits", "bottles", "tents", "liters", "units"

    @Column(nullable = false, length = 200)
    private String depotLocationName;

    @Column(nullable = false)
    private Double depotLatitude;

    @Column(nullable = false)
    private Double depotLongitude;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    private LocalDate expiryDate;

    private LocalDateTime lastRestocked = LocalDateTime.now();

    public ResourceItem() {}

    public ResourceItem(String resourceCode, String name, ResourceCategory category, String description, Integer totalQuantity, String unit, String depotLocationName, Double depotLatitude, Double depotLongitude, Organization organization) {
        this.resourceCode = resourceCode;
        this.name = name;
        this.category = category;
        this.description = description;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
        this.reservedQuantity = 0;
        this.unit = unit;
        this.depotLocationName = depotLocationName;
        this.depotLatitude = depotLatitude;
        this.depotLongitude = depotLongitude;
        this.organization = organization;
        this.lastRestocked = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getResourceCode() { return resourceCode; }
    public void setResourceCode(String resourceCode) { this.resourceCode = resourceCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ResourceCategory getCategory() { return category; }
    public void setCategory(ResourceCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }

    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }

    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDepotLocationName() { return depotLocationName; }
    public void setDepotLocationName(String depotLocationName) { this.depotLocationName = depotLocationName; }

    public Double getDepotLatitude() { return depotLatitude; }
    public void setDepotLatitude(Double depotLatitude) { this.depotLatitude = depotLatitude; }

    public Double getDepotLongitude() { return depotLongitude; }
    public void setDepotLongitude(Double depotLongitude) { this.depotLongitude = depotLongitude; }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LocalDateTime getLastRestocked() { return lastRestocked; }
    public void setLastRestocked(LocalDateTime lastRestocked) { this.lastRestocked = lastRestocked; }
}
