/**
 * ResQLink - Interactive Emergency GIS Map
 * Visualizes Disaster Incidents, Relief Depots, Emergency Requests, and Transport Routes
 */

function initEmergencyMap(elementId, centerLat = 13.0500, centerLon = 80.2300, zoomLevel = 12) {
    const mapElement = document.getElementById(elementId);
    if (!mapElement) return null;

    // Initialize Leaflet map
    const map = L.map(elementId).setView([centerLat, centerLon], zoomLevel);

    // OpenStreetMap tile layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors | ResQLink Emergency GIS',
        maxZoom: 18
    }).addTo(map);

    return map;
}

function loadMapData(map) {
    if (!map) return;

    // 1. Fetch Incidents and plot zones
    fetch('/api/incidents')
        .then(res => res.json())
        .then(incidents => {
            incidents.forEach(inc => {
                const color = inc.severity === 'CRITICAL' ? '#dc2626' : (inc.severity === 'HIGH' ? '#ea580c' : '#0284c7');

                // Draw disaster danger zone circle
                L.circle([inc.latitude, inc.longitude], {
                    color: color,
                    fillColor: color,
                    fillOpacity: 0.25,
                    radius: (inc.radiusKm || 10) * 1000
                }).addTo(map);

                // Incident marker
                const incMarker = L.marker([inc.latitude, inc.longitude]).addTo(map);
                incMarker.bindPopup(`
                    <div style="min-width: 200px">
                        <span class="badge bg-danger mb-1">${inc.incidentType}</span>
                        <h6 class="fw-bold mb-1">${inc.title}</h6>
                        <p class="small text-muted mb-1"><i class="bi bi-geo-alt"></i> ${inc.locationName}</p>
                        <p class="small mb-1"><strong>Severity:</strong> ${inc.severity}</p>
                        <p class="small mb-1"><strong>Affected:</strong> ~${inc.affectedPeopleEstimate} people</p>
                        <a href="/incidents/${inc.id}" class="btn btn-sm btn-outline-danger w-100 mt-2">View Incident</a>
                    </div>
                `);
            });
        })
        .catch(err => console.error("Error loading incidents for map:", err));

    // 2. Fetch Resources and plot depots
    fetch('/api/resources')
        .then(res => res.json())
        .then(resources => {
            const depotGroups = {};
            resources.forEach(r => {
                const key = `${r.depotLatitude},${r.depotLongitude}`;
                if (!depotGroups[key]) {
                    depotGroups[key] = {
                        name: r.depotLocationName,
                        org: r.organization ? r.organization.name : 'Relief Base',
                        lat: r.depotLatitude,
                        lon: r.depotLongitude,
                        items: []
                    };
                }
                depotGroups[key].items.push(r);
            });

            Object.values(depotGroups).forEach(depot => {
                const depotIcon = L.divIcon({
                    className: 'custom-depot-marker',
                    html: `<div style="background-color: #0284c7; color: white; border-radius: 50%; width: 34px; height: 34px; display: flex; align-items: center; justify-content: center; font-size: 16px; border: 2px solid white; box-shadow: 0 2px 6px rgba(0,0,0,0.3);"><i class="bi bi-building"></i></div>`,
                    iconSize: [34, 34],
                    iconAnchor: [17, 17]
                });

                const marker = L.marker([depot.lat, depot.lon], { icon: depotIcon }).addTo(map);

                let itemsHtml = depot.items.map(i => `<li>${i.name}: <strong>${i.availableQuantity} ${i.unit}</strong></li>`).join('');

                marker.bindPopup(`
                    <div style="min-width: 220px">
                        <span class="badge bg-primary mb-1">Supply Depot</span>
                        <h6 class="fw-bold mb-1">${depot.name}</h6>
                        <p class="small text-muted mb-2">${depot.org}</p>
                        <div class="small fw-bold mb-1">Available Inventory:</div>
                        <ul class="small mb-0 ps-3">${itemsHtml}</ul>
                    </div>
                `);
            });
        })
        .catch(err => console.error("Error loading resources for map:", err));

    // 3. Fetch Requests and plot emergency locations
    fetch('/api/requests')
        .then(res => res.json())
        .then(requests => {
            requests.forEach(req => {
                if (req.status === 'DELIVERED' || req.status === 'CANCELLED') return;

                const reqIcon = L.divIcon({
                    className: 'custom-req-marker',
                    html: `<div style="background-color: #dc2626; color: white; border-radius: 50%; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center; font-size: 14px; border: 2px solid white; box-shadow: 0 0 10px rgba(220,38,38,0.8);"><i class="bi bi-exclamation-triangle-fill"></i></div>`,
                    iconSize: [30, 30],
                    iconAnchor: [15, 15]
                });

                const marker = L.marker([req.deliveryLatitude, req.deliveryLongitude], { icon: reqIcon }).addTo(map);
                marker.bindPopup(`
                    <div style="min-width: 220px">
                        <span class="badge bg-danger mb-1">Priority: ${req.priorityScore}</span>
                        <h6 class="fw-bold mb-1">${req.trackingCode}</h6>
                        <p class="small text-muted mb-1">${req.itemName} (${req.requestedQuantity} ${req.unit})</p>
                        <p class="small mb-1"><i class="bi bi-geo-alt"></i> ${req.requesterAddress}</p>
                        <p class="small mb-1"><strong>Urgency:</strong> ${req.urgency}</p>
                        <a href="/tracking/${req.trackingCode}" class="btn btn-sm btn-danger w-100 mt-2">Track Aid</a>
                    </div>
                `);
            });
        })
        .catch(err => console.error("Error loading requests for map:", err));

    // 4. Fetch Active Transport Missions and draw route lines
    fetch('/api/allocations/missions')
        .then(res => res.json())
        .then(missions => {
            missions.forEach(m => {
                if (m.status === 'DELIVERED') return;

                const req = m.emergencyRequest;
                const alloc = m.resourceAllocation;
                if (!req || !alloc || !alloc.resourceItem) return;

                const depotLat = alloc.resourceItem.depotLatitude;
                const depotLon = alloc.resourceItem.depotLongitude;
                const destLat = req.deliveryLatitude;
                const destLon = req.deliveryLongitude;

                // Draw dashed transit polyline
                const polyline = L.polyline([[depotLat, depotLon], [destLat, destLon]], {
                    color: '#2563eb',
                    weight: 4,
                    dashArray: '8, 8',
                    opacity: 0.8
                }).addTo(map);

                // Add mid-point vehicle marker
                const midLat = (depotLat + destLat) / 2;
                const midLon = (depotLon + destLon) / 2;

                const vehicleIcon = L.divIcon({
                    className: 'vehicle-marker',
                    html: `<div style="background-color: #16a34a; color: white; border-radius: 50%; width: 28px; height: 28px; display: flex; align-items: center; justify-content: center; font-size: 13px; border: 2px solid white; box-shadow: 0 2px 6px rgba(0,0,0,0.3);"><i class="bi bi-truck"></i></div>`,
                    iconSize: [28, 28],
                    iconAnchor: [14, 14]
                });

                L.marker([midLat, midLon], { icon: vehicleIcon }).addTo(map)
                    .bindPopup(`
                        <div style="min-width: 180px">
                            <span class="badge bg-success mb-1">In Transit</span>
                            <h6 class="fw-bold mb-1">${m.trackingNumber}</h6>
                            <p class="small mb-1"><strong>Vehicle:</strong> ${m.vehicleNumber}</p>
                            <p class="small mb-1"><strong>Driver:</strong> ${m.driverName}</p>
                            <p class="small mb-1"><strong>Mode:</strong> ${m.transportMode}</p>
                            <a href="/transport/${m.id}" class="btn btn-sm btn-outline-primary w-100 mt-2">Mission Details</a>
                        </div>
                    `);
            });
        })
        .catch(err => console.error("Error loading transport missions for map:", err));
}
