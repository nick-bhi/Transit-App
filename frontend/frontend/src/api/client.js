const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080';

// Health check
export async function fetchHealth() {
  const res = await fetch(`${API_BASE}/actuator/health`);
  return res.json();
}

// Route alerts
export async function createRouteAlert(alertData) {
  const res = await fetch(`${API_BASE}/api/routes/alerts`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(alertData)
  });
  return res.json();
}

export async function getRouteAlerts() {
  const res = await fetch(`${API_BASE}/api/routes/alerts`);
  return res.json();
}

export async function deleteRouteAlert(id) {
  const res = await fetch(`${API_BASE}/api/routes/alerts/${id}`, {
    method: 'DELETE'
  });
  return res.ok;
}

// Route analytics
export async function getRouteSummary(startStopId, endStopId, routeId) {
  const params = new URLSearchParams({ startStopId, endStopId, routeId });
  const res = await fetch(`${API_BASE}/api/routes/summary?${params}`);
  return res.json();
}

// Stops and routes
export async function getStops() {
  const res = await fetch(`${API_BASE}/api/routes/stops`);
  return res.json();
}

export async function getRoutes() {
  const res = await fetch(`${API_BASE}/api/routes/routes`);
  return res.json();
}

export async function searchStops(query) {
  const params = new URLSearchParams({ query });
  const res = await fetch(`${API_BASE}/api/routes/stops/search?${params}`);
  return res.json();
}
