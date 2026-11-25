package com.resqlink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 50)
    private String orgType; // NGO, GOVT, HOSPITAL, PRIVATE_SUPPLIER, VOLUNTEER_CORPS

    @Column(nullable = false, length = 100)
    private String contactPerson;

    @Column(nullable = false, length = 20)
    private String contactPhone;

    @Column(nullable = false, length = 100)
    private String contactEmail;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private boolean verified = true;

    private LocalDateTime registeredAt = LocalDateTime.now();

    public Organization() {}

    public Organization(String name, String orgType, String contactPerson, String contactPhone, String contactEmail, String address, Double latitude, Double longitude) {
        this.name = name;
        this.orgType = orgType;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.verified = true;
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOrgType() { return orgType; }
    public void setOrgType(String orgType) { this.orgType = orgType; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
}
