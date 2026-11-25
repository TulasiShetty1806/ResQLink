# ResQLink 🚨
### Intelligent Emergency Resource Coordination & Rapid Allocation Platform
*An Advanced Java Enterprise Disaster Management System (ERMS)*

[![Java](https://img.shields.io/badge/Java-21%20%2F%2024-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Security-RBAC%206.x-blue.svg)](https://spring.io/projects/spring-security)
[![WebSocket](https://img.shields.io/badge/RealTime-WebSocket%20STOMP-blueviolet.svg)](https://spring.io/)
[![Database](https://img.shields.io/badge/Database-H2%20%2F%20MySQL-red.svg)](http://localhost:8085/h2-console)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger%203.0-green.svg)](http://localhost:8085/swagger-ui.html)

---

## 📌 1. Problem Scenario & Context

During catastrophic events such as **flash floods, urban and wildfires, earthquakes, cyclones, structural collapses, and mass-casualty accidents**, emergency supplies frequently exist across various relief depots, hospitals, military bases, and NGO warehouses—but are **poorly coordinated**.

Critical bottlenecks typically occur:
1. Requesters cannot accurately convey emergency triage priorities.
2. Coordinators lack unified situational awareness of available inventory across multi-agency depots.
3. Supply allocation is done ad-hoc without proximity or sufficiency optimization.
4. Logistics transit is untracked, causing delivery duplication or critical delays.

**ResQLink** provides one centralized, intelligent web platform where:
- **Incident Commanders / Coordinators** declare crisis zones and manage operations.
- **Relief Organizations & Warehouses** register and restock live relief supplies.
- **Citizens & Field Responders** submit categorized emergency aid requests.
- **The Intelligent Allocation Engine** analyzes multi-factor priority scores and computes optimal depot allocations.
- **Logistics Teams** receive dispatch orders, update transit checkpoints, and verify final doorstep handover via secure PIN codes.

---

## 🔄 2. The Core 8-Stage Workflow

ResQLink strictly implements the required 8-stage disaster life-cycle:

```
[1. Emergency] ➔ [2. Request] ➔ [3. Prioritize] ➔ [4. Find Resources] ➔ [5. Allocate] ➔ [6. Transport] ➔ [7. Deliver] ➔ [8. Track]
```

| Stage | Life-cycle Event | System Action & Implementation |
|---|---|---|
| **1. Emergency** | Disaster Incident Declared | Incident Commander logs disaster with epicenter GPS coordinates, impact radius, affected population estimate, and severity level (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`). |
| **2. Request** | Emergency Aid Demand Registered | Trapped citizens or rescue teams submit requests specifying item, quantity, urgency (`IMMEDIATE_LIFE_THREAT`, `CRITICAL_HOURS`), GPS drop-off coordinates, and casualty tags. |
| **3. Prioritize** | Algorithmic Triage Engine | Dynamic multi-variable formula scores requests ($0 - 100$) factoring Incident Severity, Urgency Weight, Vulnerable Victims (infants, elderly), and Elapsed Wait Time. |
| **4. Find Resources** | Proximity & Sufficiency Search | Evaluates all regional depots using the **Haversine Distance Formula** to rank candidate warehouses by distance, stock sufficiency, and transit time. |
| **5. Allocate** | Multi-Criteria Allocation | 1-Click atomic allocation reserves inventory from the best-fit depot, deductions update immediately, and request escalates to `ALLOCATED`. |
| **6. Transport** | Logistics Fleet Dispatched | Assigns transit mode (`AMBULANCE`, `RESCUE_BOAT`, `CARGO_TRUCK`, `AIR_DROP_DRONE`, `OFFROAD_4X4`), driver, vehicle number, and calculates ETA. |
| **7. Deliver** | Physical Handover & Verification | Field driver reaches drop zone; supplies are verified against the recipient's secure 4-digit PIN, updating status to `DELIVERED`. |
| **8. Track** | Real-Time Situational Awareness | Public tracking timeline (`REQ-XXXX` / `TRK-XXXX`), Leaflet OpenStreetMap emergency map with custom markers, and live WebSocket push toasts. |

---

## 🧮 3. Algorithmic Formulations

### A. Dynamic Triage Priority Scoring Formula

The priority score $S \in [5, 100]$ is computed dynamically via:

$$S = W_{\text{urgency}} + 0.75 \cdot W_{\text{severity}} + \min(15, 1.5 \cdot N_{\text{victims}}) + B_{\text{vulnerable}} + \min(15, 1.5 \cdot \frac{T_{\text{wait}}}{15})$$

Where:
- $W_{\text{urgency}}$: `IMMEDIATE_LIFE_THREAT` = 40, `CRITICAL_HOURS` = 30, `URGENT_DAY` = 20, `STANDARD` = 10
- $W_{\text{severity}}$: `CRITICAL` = 40, `HIGH` = 30, `MEDIUM` = 20, `LOW` = 10
- $B_{\text{vulnerable}}$: $+10$ points if infants, elderly, or injured persons are present
- $T_{\text{wait}}$: Elapsed minutes since request creation (escalates unattended requests automatically)

### B. Haversine Spatial Proximity Formula

Distance $d$ between depot coordinates $(\phi_1, \lambda_1)$ and delivery site $(\phi_2, \lambda_2)$:

$$a = \sin^2\left(\frac{\Delta \phi}{2}\right) + \cos(\phi_1)\cos(\phi_2)\sin^2\left(\frac{\Delta \lambda}{2}\right)$$
$$c = 2 \cdot \text{atan2}\left(\sqrt{a}, \sqrt{1-a}\right)$$
$$d = R \cdot c \cdot f_{\text{road}}$$

Where $R = 6371\text{ km}$ and $f_{\text{road}} = 1.25$ accounts for terrestrial road detour factors.

---

## 🛠️ 4. Advanced Java Tools & Technology Stack

| Layer | Advanced Java Tool / Technology |
|---|---|
| **Language & SDK** | **Java 21 / 24** (Record classes, virtual threads ready, modern pattern matching) |
| **Framework** | **Spring Boot 3.3.5** (Enterprise microservice-ready MVC architecture) |
| **Persistence & ORM** | **Spring Data JPA & Hibernate 6** (Entity mapping, JPQL spatial aggregates) |
| **Security & RBAC** | **Spring Security 6** (BCrypt encryption, role-based URL authorization) |
| **Real-Time Push** | **Spring WebSocket + STOMP Messaging** (Pushing live disaster alerts) |
| **API Documentation** | **SpringDoc OpenAPI 3.0 / Swagger UI** (`/swagger-ui.html`) |
| **Embedded Database** | **H2 Database** (Zero-configuration in-memory database with web console) |
| **Production Database** | **MySQL 8.0** ready (toggleable via `application.properties`) |
| **Template Engine** | **Thymeleaf 3** with Spring Security 6 dialect |
| **Frontend Frameworks** | **Bootstrap 5.3**, **Leaflet.js** (GIS Maps), **Chart.js**, **SockJS & Stomp.js** |

---

## 🚀 5. How to Run on Localhost

### Prerequisites
- **Java Development Kit (JDK) 21 or 24**
- **Apache Maven 3.8+** (or included `mvn`)

### Step 1: Clone or Navigate to the Workspace
```bash
cd d:\Assignments-5thsem\AJ\ERMS
```

### Step 2: Build and Test the Application
```bash
mvn clean test
```

### Step 3: Run ResQLink
```bash
mvn spring-boot:run
```

Once started, the application will be live at:
- **Main Web Application:** 👉 [http://localhost:8085](http://localhost:8085)
- **Interactive Swagger / OpenAPI UI:** 👉 [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
- **H2 Database Console:** 👉 [http://localhost:8085/h2-console](http://localhost:8085/h2-console)
  *(JDBC URL: `jdbc:h2:mem:resqlinkdb`, User: `sa`, Password: empty)*

---

## 👥 6. Pre-Seeded Demo Accounts (1-Click Fast Login)

The login screen provides **1-Click Quick Login** buttons for instant demonstration:

| Role | Username | Password | Intended Role |
|---|---|---|---|
| 🛡️ **Incident Coordinator** | `admin` | `password` | Manages crisis command, declares incidents, reviews triage, allocates resources |
| 🏥 **Relief Organization** | `redcross` | `password` | Manages depot stock (NDRF, Red Cross, Hospitals), replenishes supplies |
| 🚛 **Logistics Dispatcher** | `driver1` | `password` | Manages transit missions, updates en-route checkpoints, verifies PIN delivery |
| 👤 **Citizen / Requester** | `citizen1` | `password` | Submits emergency requests, tracks live timeline |

---

## 📡 7. REST API Endpoints

ResQLink exposes comprehensive REST endpoints for mobile app or external integration:

- `GET /api/incidents`: Fetch all disaster incidents
- `GET /api/requests`: Fetch aid requests ranked by priority score
- `GET /api/requests/track/{code}`: Public status tracking by `REQ-XXXX` code
- `GET /api/resources`: Real-time stock counts across regional depots
- `GET /api/allocations/recommend/{requestId}`: Returns algorithmic depot match rankings
- `GET /api/allocations/missions`: Returns active fleet convoys with route coordinates
- `WS /ws` (Topic `/topic/alerts`): Real-time live emergency broadcast stream

---

## 🏆 Summary of Key Features
- **Zero-Setup Localhost Execution:** Runs immediately with embedded H2 + rich mock disaster data.
- **End-to-End Workflow:** Fully interactive walkthrough from incident declaration to 4-digit PIN delivery verification.
- **Visual Situational Awareness:** Leaflet OpenStreetMap with danger circles, depot pins, request alerts, and route polylines.
- **Real-Time Push Alerts:** Browser tabs receive immediate toast alerts whenever emergencies are logged or convoys are dispatched.
