package com.resqlink.repository;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.ResourceAllocation;
import com.resqlink.model.enums.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, Long> {
    Optional<ResourceAllocation> findByAllocationCode(String allocationCode);
    List<ResourceAllocation> findByEmergencyRequest(EmergencyRequest emergencyRequest);
    List<ResourceAllocation> findByStatus(AllocationStatus status);
    List<ResourceAllocation> findAllByOrderByAllocatedAtDesc();
    long countByStatus(AllocationStatus status);
}
