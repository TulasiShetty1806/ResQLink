package com.resqlink.repository;

import com.resqlink.model.Incident;
import com.resqlink.model.enums.IncidentSeverity;
import com.resqlink.model.enums.IncidentStatus;
import com.resqlink.model.enums.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    Optional<Incident> findByIncidentCode(String incidentCode);
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findBySeverity(IncidentSeverity severity);
    List<Incident> findByIncidentType(IncidentType incidentType);
    List<Incident> findAllByOrderByReportedAtDesc();
    long countByStatus(IncidentStatus status);
}
