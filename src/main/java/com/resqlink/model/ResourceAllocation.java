package com.resqlink.model;

import com.resqlink.model.enums.AllocationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_allocations")
public class ResourceAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String allocationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id", nullable = false)
    private EmergencyRequest emergencyRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resource_item_id", nullable = false)
    private ResourceItem resourceItem;

    @Column(nullable = false)
    private Integer allocatedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AllocationStatus status = AllocationStatus.CONFIRMED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime allocatedAt = LocalDateTime.now();

    @Column(length = 100)
    private String allocatedBy; // coordinator username

    @Column(columnDefinition = "TEXT")
    private String notes;

    public ResourceAllocation() {}

    public ResourceAllocation(String allocationCode, EmergencyRequest emergencyRequest, ResourceItem resourceItem, Integer allocatedQuantity, String allocatedBy, String notes) {
        this.allocationCode = allocationCode;
        this.emergencyRequest = emergencyRequest;
        this.resourceItem = resourceItem;
        this.allocatedQuantity = allocatedQuantity;
        this.status = AllocationStatus.CONFIRMED;
        this.allocatedBy = allocatedBy;
        this.notes = notes;
        this.allocatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAllocationCode() { return allocationCode; }
    public void setAllocationCode(String allocationCode) { this.allocationCode = allocationCode; }

    public EmergencyRequest getEmergencyRequest() { return emergencyRequest; }
    public void setEmergencyRequest(EmergencyRequest emergencyRequest) { this.emergencyRequest = emergencyRequest; }

    public ResourceItem getResourceItem() { return resourceItem; }
    public void setResourceItem(ResourceItem resourceItem) { this.resourceItem = resourceItem; }

    public Integer getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(Integer allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }

    public AllocationStatus getStatus() { return status; }
    public void setStatus(AllocationStatus status) { this.status = status; }

    public LocalDateTime getAllocatedAt() { return allocatedAt; }
    public void setAllocatedAt(LocalDateTime allocatedAt) { this.allocatedAt = allocatedAt; }

    public String getAllocatedBy() { return allocatedBy; }
    public void setAllocatedBy(String allocatedBy) { this.allocatedBy = allocatedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
