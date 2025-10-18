import { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Autocomplete,
  Box,
  Typography,
  Alert,
  CircularProgress,
  Divider
} from "@mui/material";
import { createRouteAlert, getStops, getRoutes } from "../api/client";

function RouteDialog({ open, onClose }) {
  const [stops, setStops] = useState([]);
  const [routes, setRoutes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [formData, setFormData] = useState({
    startStopId: null,
    endStopId: null,
    routeId: null,
    alertThresholdMinutes: 5
  });

  useEffect(() => {
    if (open) {
      loadData();
    }
  }, [open]);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const [stopsData, routesData] = await Promise.all([
        getStops(),
        getRoutes()
      ]);
      
      setStops(stopsData);
      setRoutes(routesData);
    } catch (err) {
      setError(`Failed to load data: ${err.message}`);
      console.error("Error loading data:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async () => {
    try {
      setSubmitting(true);
      setError(null);

      if (!formData.startStopId || !formData.endStopId || !formData.routeId) {
        setError("Please fill in all required fields");
        return;
      }

      await createRouteAlert({
        startStopId: formData.startStopId.id,
        endStopId: formData.endStopId.id,
        routeId: formData.routeId.id,
        alertThresholdMinutes: formData.alertThresholdMinutes
      });

      onClose(true);
    } catch (err) {
      setError(`Failed to create route alert: ${err.message}`);
      console.error("Error creating route alert:", err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleClose = () => {
    if (!submitting) {
      setFormData({
        startStopId: null,
        endStopId: null,
        routeId: null,
        alertThresholdMinutes: 5
      });
      setError(null);
      onClose(false);
    }
  };

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  return (
    <Dialog 
      open={open} 
      onClose={handleClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: { borderRadius: 2 }
      }}
    >
      <DialogTitle>
        <Typography variant="h5" component="h2" sx={{ fontWeight: 'bold' }}>
          Add New Route Monitor
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Set up monitoring for a transit route with delay alerts
        </Typography>
      </DialogTitle>

      <Divider />

      <DialogContent sx={{ pt: 3 }}>
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
            <Typography sx={{ ml: 2 }}>Loading stops and routes...</Typography>
          </Box>
        ) : (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}

            {/* Start Stop */}
            <Autocomplete
              options={stops}
              getOptionLabel={(option) => `${option.name} (${option.id})`}
              value={formData.startStopId}
              onChange={(event, newValue) => handleInputChange('startStopId', newValue)}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Start Stop"
                  placeholder="Search for a stop..."
                  required
                />
              )}
              loading={loading}
              noOptionsText="No stops found"
            />

            {/* End Stop */}
            <Autocomplete
              options={stops}
              getOptionLabel={(option) => `${option.name} (${option.id})`}
              value={formData.endStopId}
              onChange={(event, newValue) => handleInputChange('endStopId', newValue)}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="End Stop"
                  placeholder="Search for a stop..."
                  required
                />
              )}
              loading={loading}
              noOptionsText="No stops found"
            />

            {/* Route */}
            <Autocomplete
              options={routes}
              getOptionLabel={(option) => `${option.shortName} - ${option.longName}`}
              value={formData.routeId}
              onChange={(event, newValue) => handleInputChange('routeId', newValue)}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Route"
                  placeholder="Select a route..."
                  required
                />
              )}
              loading={loading}
              noOptionsText="No routes found"
            />

            {/* Alert Threshold */}
            <TextField
              label="Alert Threshold (minutes)"
              type="number"
              value={formData.alertThresholdMinutes}
              onChange={(e) => handleInputChange('alertThresholdMinutes', parseInt(e.target.value) || 5)}
              helperText="Get notified when delays exceed this threshold"
              inputProps={{ min: 1, max: 60 }}
              fullWidth
            />

            <Box sx={{ 
              p: 2, 
              backgroundColor: 'rgba(25, 118, 210, 0.1)', 
              borderRadius: 1,
              border: '1px solid rgba(25, 118, 210, 0.2)'
            }}>
              <Typography variant="body2" color="text.secondary">
                <strong>What this will do:</strong>
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                • Monitor the selected route between your chosen stops<br/>
                • Track travel times, delays, and on-time performance<br/>
                • Send alerts when delays exceed {formData.alertThresholdMinutes} minutes<br/>
                • Update data every 30 seconds automatically
              </Typography>
            </Box>
          </Box>
        )}
      </DialogContent>

      <Divider />

      <DialogActions sx={{ p: 3 }}>
        <Button 
          onClick={handleClose} 
          disabled={submitting}
          color="inherit"
        >
          Cancel
        </Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          disabled={submitting || loading}
          sx={{ 
            backgroundColor: '#1976d2',
            '&:hover': { backgroundColor: '#1565c0' }
          }}
        >
          {submitting ? (
            <>
              <CircularProgress size={16} sx={{ mr: 1 }} />
              Creating...
            </>
          ) : (
            'Create Route Monitor'
          )}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default RouteDialog;
