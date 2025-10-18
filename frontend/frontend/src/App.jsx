import { useEffect, useState } from "react";
import {
  Box,
  Container,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  Chip,
  Alert,
  CircularProgress,
  AppBar,
  Toolbar,
  IconButton,
  ThemeProvider,
  createTheme
} from "@mui/material";
import { fetchHealth, getRouteAlerts, getRouteSummary, deleteRouteAlert } from "./api/client";
import RouteDialog from "./components/RouteDialog";
import RouteCard from "./components/RouteCard";

// Create a theme that removes width constraints
const theme = createTheme({
  components: {
    MuiContainer: {
      styleOverrides: {
        root: {
          maxWidth: 'none !important',
          width: '100% !important'
        }
      }
    },
    MuiBox: {
      styleOverrides: {
        root: {
          maxWidth: 'none !important',
          width: '100% !important'
        }
      }
    }
  }
});

function App() {
  const [status, setStatus] = useState("Loading...");
  const [routeAlerts, setRouteAlerts] = useState([]);
  const [routeSummaries, setRouteSummaries] = useState({});
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [error, setError] = useState(null);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Check backend health
      const health = await fetchHealth();
      setStatus(health.status);
      
      // Load route alerts
      const alerts = await getRouteAlerts();
      setRouteAlerts(alerts);
      
      // Load route summaries for each alert
      const summaries = {};
      for (const alert of alerts) {
        try {
          const summary = await getRouteSummary(
            alert.startStopId,
            alert.endStopId,
            alert.routeId
          );
          summaries[alert.id] = summary;
        } catch (err) {
          console.warn(`Failed to load summary for alert ${alert.id}:`, err);
        }
      }
      setRouteSummaries(summaries);
    } catch (err) {
      setError(`Failed to load data: ${err.message}`);
      console.error("Error loading data:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    
    // Refresh data every 30 seconds
    const interval = setInterval(loadData, 30000);
    return () => clearInterval(interval);
  }, []);

  const handleAddRoute = () => {
    setDialogOpen(true);
  };

  const handleDialogClose = (success) => {
    setDialogOpen(false);
    if (success) {
      loadData(); // Refresh data after adding new route
    }
  };

  const handleDeleteRoute = async (alertId) => {
    try {
      await deleteRouteAlert(alertId);
      setRouteAlerts(prev => prev.filter(alert => alert.id !== alertId));
      setRouteSummaries(prev => {
        const updated = { ...prev };
        delete updated[alertId];
        return updated;
      });
    } catch (err) {
      setError(`Failed to delete route: ${err.message}`);
    }
  };

  const formatTime = (timestamp) => {
    if (!timestamp) return "Unknown";
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  const getStatusColor = (summary) => {
    if (!summary) return "default";
    if (summary.onTimePercentage >= 90) return "success";
    if (summary.onTimePercentage >= 70) return "warning";
    return "error";
  };

  return (
    <ThemeProvider theme={theme}>
      <Box sx={{ flexGrow: 1, minHeight: '100vh', backgroundColor: '#f5f5f5', width: '100vw' }}>
      <AppBar position="static" sx={{ backgroundColor: '#1976d2' }}>
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            🚇 Transit Monitor
          </Typography>
          <Chip 
            label={`Backend: ${status}`} 
            color={status === 'UP' ? 'success' : 'error'}
            size="small"
            sx={{ mr: 2 }}
          />
          <IconButton color="inherit" onClick={loadData} disabled={loading}>
            🔄
          </IconButton>
        </Toolbar>
      </AppBar>

      <div style={{ padding: '32px' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
          <Typography variant="h4" component="h1" sx={{ fontWeight: 'bold' }}>
            Route Monitoring
          </Typography>
          <Button
            variant="contained"
            onClick={handleAddRoute}
            sx={{ 
              backgroundColor: '#1976d2',
              '&:hover': { backgroundColor: '#1565c0' }
            }}
          >
            ➕ New Route
          </Button>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
          </Box>
        ) : routeAlerts.length === 0 ? (
          <Card sx={{ textAlign: 'center', py: 6 }}>
            <CardContent>
              <Typography variant="h6" color="text.secondary" gutterBottom>
                No routes monitored yet
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                Click "New Route" to start monitoring your transit routes
              </Typography>
              <Button variant="contained" onClick={handleAddRoute}>
                Add Your First Route
              </Button>
            </CardContent>
          </Card>
        ) : (
          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', 
            gap: '20px'
          }}>
            {routeAlerts.map((alert) => {
              const summary = routeSummaries[alert.id];
              return (
                <RouteCard
                  key={alert.id}
                  alert={alert}
                  summary={summary}
                  onDelete={() => handleDeleteRoute(alert.id)}
                  formatTime={formatTime}
                  getStatusColor={getStatusColor}
                />
              );
            })}
          </div>
        )}

        <RouteDialog
          open={dialogOpen}
          onClose={handleDialogClose}
        />
      </div>
      </Box>
    </ThemeProvider>
  );
}

export default App;
