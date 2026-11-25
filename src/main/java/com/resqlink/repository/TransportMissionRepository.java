package com.resqlink.repository;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.TransportMission;
import com.resqlink.model.enums.TransportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransportMissionRepository extends JpaRepository<TransportMission, Long> {
    Optional<TransportMission> findByTrackingNumber(String trackingNumber);
    Optional<TransportMission> findByEmergencyRequest(EmergencyRequest emergencyRequest);
    List<TransportMission> findByStatus(TransportStatus status);
    List<TransportMission> findAllByOrderByDispatchTimeDesc();
    long countByStatus(TransportStatus status);
}
