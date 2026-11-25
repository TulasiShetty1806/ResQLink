package com.resqlink.service;

import com.resqlink.model.Organization;
import com.resqlink.model.ResourceItem;
import com.resqlink.model.enums.ResourceCategory;
import com.resqlink.repository.OrganizationRepository;
import com.resqlink.repository.ResourceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    private final ResourceItemRepository resourceItemRepository;
    private final OrganizationRepository organizationRepository;
    private final NotificationService notificationService;

    @Autowired
    public ResourceService(ResourceItemRepository resourceItemRepository,
                           OrganizationRepository organizationRepository,
                           NotificationService notificationService) {
        this.resourceItemRepository = resourceItemRepository;
        this.organizationRepository = organizationRepository;
        this.notificationService = notificationService;
    }

    public List<ResourceItem> getAllResources() {
        return resourceItemRepository.findAll();
    }

    public List<ResourceItem> getAvailableResourcesByCategory(ResourceCategory category) {
        return resourceItemRepository.findAvailableForCategory(category);
    }

    public Optional<ResourceItem> getResourceById(Long id) {
        return resourceItemRepository.findById(id);
    }

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    public Optional<Organization> getOrganizationById(Long id) {
        return organizationRepository.findById(id);
    }

    @Transactional
    public Organization registerOrganization(String name, String type, String contactPerson,
                                             String phone, String email, String address,
                                             Double latitude, Double longitude) {
        Organization org = new Organization(name, type, contactPerson, phone, email, address, latitude, longitude);
        return organizationRepository.save(org);
    }

    @Transactional
    public ResourceItem registerResource(String name, ResourceCategory category, String description,
                                         Integer quantity, String unit, String depotLocation,
                                         Double depotLat, Double depotLon, Long organizationId,
                                         LocalDate expiryDate, String registeredBy) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found: " + organizationId));

        long count = resourceItemRepository.count() + 1;
        String code = String.format("RES-%04d", count);

        ResourceItem item = new ResourceItem(code, name, category, description, quantity, unit, depotLocation, depotLat, depotLon, organization);
        item.setExpiryDate(expiryDate);
        ResourceItem saved = resourceItemRepository.save(item);

        notificationService.logAndBroadcast(
                "RESOURCE",
                saved.getId(),
                "REGISTERED",
                registeredBy,
                "New Relief Stock Registered: " + saved.getName(),
                saved.getAvailableQuantity() + " " + saved.getUnit() + " at " + saved.getDepotLocationName() + " (" + organization.getName() + ")",
                "success"
        );

        return saved;
    }

    @Transactional
    public ResourceItem restockResource(Long resourceId, Integer addedQuantity, String updatedBy) {
        ResourceItem item = resourceItemRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + resourceId));

        item.setTotalQuantity(item.getTotalQuantity() + addedQuantity);
        item.setAvailableQuantity(item.getAvailableQuantity() + addedQuantity);
        item.setLastRestocked(LocalDateTime.now());
        ResourceItem updated = resourceItemRepository.save(item);

        notificationService.logAndBroadcast(
                "RESOURCE",
                updated.getId(),
                "RESTOCKED",
                updatedBy,
                "Stock Replenished: " + updated.getName(),
                "+" + addedQuantity + " " + updated.getUnit() + " added. Total available: " + updated.getAvailableQuantity(),
                "info"
        );

        return updated;
    }

    public Long getTotalAvailableSupplies() {
        Long total = resourceItemRepository.sumTotalAvailableSupplies();
        return total != null ? total : 0L;
    }
}
