package com.resqlink.repository;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.Incident;
import com.resqlink.model.enums.RequestStatus;
import com.resqlink.model.enums.RequestUrgency;
import com.resqlink.model.enums.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {
    Optional<EmergencyRequest> findByTrackingCode(String trackingCode);
    List<EmergencyRequest> findByIncident(Incident incident);
    List<EmergencyRequest> findByStatus(RequestStatus status);
    List<EmergencyRequest> findByUrgency(RequestUrgency urgency);
    List<EmergencyRequest> findByCategory(ResourceCategory category);
    List<EmergencyRequest> findAllByOrderByPriorityScoreDesc();
    List<EmergencyRequest> findByStatusOrderByPriorityScoreDesc(RequestStatus status);

    @Query("SELECT r FROM EmergencyRequest r WHERE r.status IN ('PENDING_TRIAGE', 'PRIORITIZED') ORDER BY r.priorityScore DESC")
    List<EmergencyRequest> findUnfulfilledRequestsPrioritized();

    long countByStatus(RequestStatus status);

    @Query("SELECT COUNT(r) FROM EmergencyRequest r WHERE r.status != 'DELIVERED' AND r.status != 'CANCELLED'")
    long countActiveRequests();

    @Query("SELECT SUM(r.affectedPeopleCount) FROM EmergencyRequest r")
    Long sumTotalAffectedPeople();
}
