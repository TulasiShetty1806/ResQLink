package com.resqlink.data;

import com.resqlink.model.*;
import com.resqlink.model.enums.*;
import com.resqlink.repository.*;
import com.resqlink.service.DistanceCalculator;
import com.resqlink.service.EmergencyRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private UserRepository userRepository;
    @Autowired private IncidentRepository incidentRepository;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private ResourceItemRepository resourceItemRepository;
    @Autowired private EmergencyRequestRepository requestRepository;
    @Autowired private ResourceAllocationRepository allocationRepository;
    @Autowired private TransportMissionRepository transportRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmergencyRequestService requestService;
    @Autowired private DistanceCalculator distanceCalculator;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already initialized with seed data.");
            return;
        }

        logger.info("Initializing ResQLink with realistic disaster response dataset...");

        // 1. Seed Demo Users
        String encPass = passwordEncoder.encode("password");
        User admin = userRepository.save(new User("admin", encPass, "Commander Sarah Jenkins", "coordinator@resqlink.local", "+91-98765-00001", UserRole.ROLE_COORDINATOR, "National Disaster Management Authority"));
        User orgUser = userRepository.save(new User("redcross", encPass, "Dr. Robert Vance", "relief@redcross.org", "+91-98765-00002", UserRole.ROLE_ORGANIZATION, "International Red Cross Relief Hub"));
        User logisticsUser = userRepository.save(new User("driver1", encPass, "Vikram Singh", "fleet@resqlink.local", "+91-98765-00003", UserRole.ROLE_LOGISTICS, "Rapid Logistics Transport Corps"));
        User citizenUser = userRepository.save(new User("citizen1", encPass, "Ananya Sharma", "ananya.sharma@gmail.com", "+91-98765-00004", UserRole.ROLE_CITIZEN, "Community Volunteer"));

        // 2. Seed Relief Organizations
        Organization ndrf = organizationRepository.save(new Organization(
                "National Disaster Response Force (NDRF)", "GOVT", "Brigadier M. Nair", "+91-44-2538-0001", "ops@ndrf.gov.in", "Central Defense Depot, Anna Salai, Chennai", 13.0827, 80.2707));
        Organization redCrossOrg = organizationRepository.save(new Organization(
                "Red Cross Emergency Support Hub", "NGO", "Dr. Robert Vance", "+91-44-2819-0002", "contact@redcross.org", "Greams Road Disaster Depot, Chennai", 13.0604, 80.2496));
        Organization apexHospital = organizationRepository.save(new Organization(
                "Apex Trauma & Emergency Hospital", "HOSPITAL", "Dr. Priya Sundaram", "+91-44-2433-0003", "trauma@apexcare.org", "T. Nagar Health Corridor, Chennai", 13.0418, 80.2341));
        Organization coastalAlliance = organizationRepository.save(new Organization(
                "Coastal Relief & Food Supply Coalition", "NGO", "Arunachalam K.", "+91-44-2491-0004", "aid@coastalrelief.org", "Adyar Disaster Storehouse, Chennai", 12.9815, 80.2180));

        // 3. Seed Incidents
        Incident incFlood = incidentRepository.save(new Incident(
                "INC-0001", "Coastal Flash Floods - Velachery & Adyar Sectors", IncidentType.FLOOD, IncidentSeverity.CRITICAL,
                "Severe torrential rain overflowed Adyar basin inundating low-lying residential sectors. Power grid down, drinking water compromised, multiple families stranded on rooftops.",
                "Velachery Lowlands & Adyar Basin, Chennai", 12.9815, 80.2180, 15.0, 14500, 3));

        Incident incFire = incidentRepository.save(new Incident(
                "INC-0002", "Ambattur Industrial Chemical Plant Fire", IncidentType.FIRE, IncidentSeverity.HIGH,
                "Chemical storage explosion caused secondary factory fires. Hazardous smoke plumes spreading. Local population evacuation underway.",
                "Ambattur Industrial Estate, Chennai", 13.0850, 80.1600, 6.0, 2800, 1));

        Incident incLandslide = incidentRepository.save(new Incident(
                "INC-0003", "Highland Pass Mudflow & Highway Blockade", IncidentType.LANDSLIDE, IncidentSeverity.MEDIUM,
                "Heavy rainfall triggered slope failure blocking essential transit corridor. Multiple transit vehicles isolated awaiting clearance.",
                "North Hill Ridge Route, Chennai Bypass", 13.1400, 80.1200, 8.0, 600, 0));

        // 4. Seed Resource Items
        ResourceItem resBoats = resourceItemRepository.save(new ResourceItem(
                "RES-0001", "Inflatable Rescue Motorboats & Life Vests", ResourceCategory.RESCUE_EQUIPMENT,
                "Durable 8-person motorized rescue dinghies equipped with flotation jackets and throw ropes.",
                25, "Boats", "NDRF Central Logistics Base", 13.0827, 80.2707, ndrf));

        ResourceItem resTraumaKits = resourceItemRepository.save(new ResourceItem(
                "RES-0002", "Emergency Trauma & Surgical Field Kits", ResourceCategory.MEDICAL_SUPPLIES,
                "Sterile burn dressings, tourniquets, sutures, antiseptic wash, and IV start sets.",
                180, "Kits", "Red Cross Greams Depot", 13.0604, 80.2496, redCrossOrg));

        ResourceItem resWater = resourceItemRepository.save(new ResourceItem(
                "RES-0003", "Packaged Clean Drinking Water (20L Cans)", ResourceCategory.FOOD_AND_WATER,
                "Laboratory certified sealed potable drinking water drums.",
                1200, "Cans (20L)", "Coastal Relief Adyar Depot", 12.9815, 80.2180, coastalAlliance));

        ResourceItem resRations = resourceItemRepository.save(new ResourceItem(
                "RES-0004", "Ready-to-Eat Emergency Ration Packs (3-Day)", ResourceCategory.FOOD_AND_WATER,
                "High-calorie, vacuum-sealed nutritionally fortified family meals requiring no cooking.",
                2500, "Packs", "Coastal Relief Adyar Depot", 12.9815, 80.2180, coastalAlliance));

        ResourceItem resTents = resourceItemRepository.save(new ResourceItem(
                "RES-0005", "All-Weather Emergency Family Shelters", ResourceCategory.SHELTER_BEDDING,
                "Heavy-duty waterproof insulated 6-person relief tents with ground tarps.",
                80, "Tents", "Red Cross Greams Depot", 13.0604, 80.2496, redCrossOrg));

        ResourceItem resGenerators = resourceItemRepository.save(new ResourceItem(
                "RES-0006", "Diesel Mobile Generators (7.5 kVA)", ResourceCategory.POWER_AND_COMMUNICATION,
                "Portable silent diesel electric generators with multi-plug surge protection boards.",
                20, "Units", "NDRF Central Logistics Base", 13.0827, 80.2707, ndrf));

        ResourceItem resBlood = resourceItemRepository.save(new ResourceItem(
                "RES-0007", "Universal O-Negative Blood Units & IV Saline", ResourceCategory.MEDICAL_SUPPLIES,
                "Refrigerated blood emergency units and lactated Ringer's infusion bags.",
                150, "Units", "Apex Trauma Center Depot", 13.0418, 80.2341, apexHospital));

        // 5. Seed Emergency Requests spanning the full workflow:
        // Request 1: In Triage / Prioritized state (ready for allocation demo!)
        EmergencyRequest req1 = new EmergencyRequest(
                "REQ-1011-A", incFlood, ResourceCategory.FOOD_AND_WATER, "Drinking Water Cans", 40, "Cans (20L)",
                RequestUrgency.IMMEDIATE_LIFE_THREAT, "Rajesh Govind", "+91-94441-11001",
                "Apartment Block 4, Gandhi Road, Velachery", 12.9750, 80.2210, 65, true,
                "Ground floor completely submerged. 12 elderly residents with no drinking water for 18 hours.");
        req1.setPriorityScore(requestService.calculatePriorityScore(req1));
        req1.setStatus(RequestStatus.PRIORITIZED);
        requestRepository.save(req1);

        // Request 2: Allocated state
        EmergencyRequest req2 = new EmergencyRequest(
                "REQ-2022-B", incFlood, ResourceCategory.MEDICAL_SUPPLIES, "Emergency Trauma & Field Kits", 15, "Kits",
                RequestUrgency.CRITICAL_HOURS, "Sister Mary Clara", "+91-94441-22002",
                "Community Health Center, Adyar Bridge", 12.9900, 80.2300, 35, true,
                "Flood-injured patients arriving with lacerations and hypothermia.");
        req2.setPriorityScore(requestService.calculatePriorityScore(req2));
        req2.setStatus(RequestStatus.ALLOCATED);
        req2 = requestRepository.save(req2);

        // Allocate resources for req2
        ResourceAllocation alloc2 = new ResourceAllocation(
                "ALC-1001", req2, resTraumaKits, 15, "admin", "Allocated from Red Cross Greams Depot for rapid dispatch."
        );
        resTraumaKits.setAvailableQuantity(resTraumaKits.getAvailableQuantity() - 15);
        resTraumaKits.setReservedQuantity(resTraumaKits.getReservedQuantity() + 15);
        resourceItemRepository.save(resTraumaKits);
        allocationRepository.save(alloc2);

        // Request 3: Dispatched & In Transit state (with live TransportMission!)
        EmergencyRequest req3 = new EmergencyRequest(
                "REQ-3033-C", incFlood, ResourceCategory.RESCUE_EQUIPMENT, "Inflatable Rescue Motorboats", 3, "Boats",
                RequestUrgency.IMMEDIATE_LIFE_THREAT, "Inspector S. Raman", "+91-94441-33003",
                "Sector 7 Flood Relief Staging Area, Velachery", 12.9800, 80.2150, 80, true,
                "Urgent evacuation needed for 80 residents cut off by 6-foot water level.");
        req3.setPriorityScore(requestService.calculatePriorityScore(req3));
        req3.setStatus(RequestStatus.DISPATCHED);
        req3 = requestRepository.save(req3);

        ResourceAllocation alloc3 = new ResourceAllocation(
                "ALC-1002", req3, resBoats, 3, "admin", "Critical boat dispatch authorized."
        );
        resBoats.setAvailableQuantity(resBoats.getAvailableQuantity() - 3);
        resBoats.setReservedQuantity(resBoats.getReservedQuantity() + 3);
        resourceItemRepository.save(resBoats);
        alloc3.setStatus(AllocationStatus.DISPATCHED);
        allocationRepository.save(alloc3);

        double dist3 = distanceCalculator.calculateDistanceKm(
                resBoats.getDepotLatitude(), resBoats.getDepotLongitude(),
                req3.getDeliveryLatitude(), req3.getDeliveryLongitude());
        int dur3 = distanceCalculator.estimateDurationMinutes(dist3, TransportMode.RESCUE_BOAT);

        TransportMission mission3 = new TransportMission(
                "TRK-9001", req3, alloc3, TransportMode.RESCUE_BOAT, "BOAT-NDRF-07",
                "Capt. Rajesh Kumar", "+91-98765-43210", dist3, dur3,
                "Navigating through Adyar water corridor. Flashing beacon activated.");
        mission3.setStatus(TransportStatus.IN_TRANSIT);
        mission3.setDispatchTime(LocalDateTime.now().minusMinutes(20));
        mission3.setEstimatedArrival(LocalDateTime.now().plusMinutes(dur3 - 20));
        transportRepository.save(mission3);

        // Request 4: Delivered & Closed state
        EmergencyRequest req4 = new EmergencyRequest(
                "REQ-4044-D", incFire, ResourceCategory.FOOD_AND_WATER, "Ready-to-Eat Emergency Ration Packs", 120, "Packs",
                RequestUrgency.URGENT_DAY, "Camp Coordinator Joseph", "+91-94441-44004",
                "Evacuation Relief Shelter 3, Ambattur", 13.0800, 80.1700, 120, false,
                "Displaced factory workers and neighborhood families sheltered.");
        req4.setPriorityScore(58.0);
        req4.setStatus(RequestStatus.DELIVERED);
        req4 = requestRepository.save(req4);

        ResourceAllocation alloc4 = new ResourceAllocation(
                "ALC-1003", req4, resRations, 120, "admin", "Fulfilled from Coastal Food Alliance."
        );
        resRations.setAvailableQuantity(resRations.getAvailableQuantity() - 120);
        resourceItemRepository.save(resRations);
        alloc4.setStatus(AllocationStatus.COMPLETED);
        allocationRepository.save(alloc4);

        TransportMission mission4 = new TransportMission(
                "TRK-9002", req4, alloc4, TransportMode.CARGO_TRUCK, "TN-09-ER-5521",
                "Suresh Varma", "+91-98765-55555", 14.5, 35,
                "Delivered via Outer Ring Road without delays.");
        mission4.setStatus(TransportStatus.DELIVERED);
        mission4.setActualDeliveryTime(LocalDateTime.now().minusHours(1));
        mission4.setRecipientSignatureNotes("Received 120 ration packs in excellent sealed condition. Confirmed by Camp In-Charge.");
        transportRepository.save(mission4);

        // 6. Audit Trail Logs
        auditLogRepository.save(new AuditLog("INCIDENT", incFlood.getId(), "CREATED", "admin", "Incident INC-0001 declared with CRITICAL severity."));
        auditLogRepository.save(new AuditLog("RESOURCE", resBoats.getId(), "REGISTERED", "redcross", "25 Inflatable Boats registered at NDRF Base."));
        auditLogRepository.save(new AuditLog("REQUEST", req1.getId(), "SUBMITTED", "citizen1", "Request REQ-1011-A received for 40 drinking water cans. Priority: " + req1.getPriorityScore()));
        auditLogRepository.save(new AuditLog("TRANSPORT", mission3.getId(), "DISPATCHED", "admin", "Mission TRK-9001 dispatched with Boat NDRF-07."));

        logger.info("ResQLink seed data successfully loaded! Ready for demonstration.");
    }
}
