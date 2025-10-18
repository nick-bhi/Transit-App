import {
  Card,
  CardContent,
  Typography,
  Box,
  Chip,
  IconButton,
  Divider,
  LinearProgress
} from "@mui/material";

function RouteCard({ alert, summary, onDelete, formatTime, getStatusColor }) {
  const getStatusIcon = (percentage) => {
    if (percentage >= 90) return "✅";
    if (percentage >= 70) return "⚠️";
    return "❌";
  };

  const getDelayColor = (avgDelay) => {
    if (avgDelay <= 2) return "success";
    if (avgDelay <= 5) return "warning";
    return "error";
  };

  return (
    <Card 
      sx={{ 
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        transition: 'transform 0.2s, box-shadow 0.2s',
        '&:hover': {
          transform: 'translateY(-2px)',
          boxShadow: 4
        }
      }}
    >
      <CardContent sx={{ flexGrow: 1, p: 3 }}>
        {/* Header with route info and delete button */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
          <Box>
            <Typography variant="h6" component="h2" sx={{ fontWeight: 'bold', color: '#1976d2' }}>
              {summary?.routeName || `Route ${alert.routeId}`}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {alert.startStopId} → {alert.endStopId}
            </Typography>
          </Box>
          <IconButton 
            size="small" 
            onClick={onDelete}
            sx={{ color: 'error.main' }}
          >
            🗑️
          </IconButton>
        </Box>

        <Divider sx={{ my: 2 }} />

        {/* Next arrival */}
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <Box sx={{ mr: 1, fontSize: '1.2rem' }}>🕐</Box>
          <Box>
            <Typography variant="body2" color="text.secondary">
              Next Arrival
            </Typography>
            <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
              {summary?.nextScheduledArrival ? formatTime(summary.nextScheduledArrival) : 'Unknown'}
            </Typography>
          </Box>
        </Box>

        {/* Travel time */}
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <Box sx={{ mr: 1, fontSize: '1.2rem' }}>🚇</Box>
          <Box>
            <Typography variant="body2" color="text.secondary">
              Travel Time
            </Typography>
            <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
              {summary?.totalTravelTimeMinutes || 'Unknown'} min
            </Typography>
          </Box>
        </Box>

        {/* Performance metrics */}
        <Box sx={{ mt: 3 }}>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
            Performance (7-day avg)
          </Typography>
          
          {/* Average delay */}
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
            <Typography variant="body2">
              Avg Delay
            </Typography>
            <Chip 
              label={`${summary?.avgDelayMinutes?.toFixed(1) || '0.0'} min`}
              color={getDelayColor(summary?.avgDelayMinutes)}
              size="small"
            />
          </Box>

          {/* On-time percentage */}
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="body2">
              On-Time
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              {getStatusIcon(summary?.onTimePercentage)}
              <Typography variant="body2" sx={{ ml: 1, fontWeight: 'bold' }}>
                {summary?.onTimePercentage?.toFixed(1) || '100.0'}%
              </Typography>
            </Box>
          </Box>

          {/* Progress bar for on-time percentage */}
          <LinearProgress
            variant="determinate"
            value={summary?.onTimePercentage || 100}
            color={getStatusColor(summary)}
            sx={{ 
              height: 6, 
              borderRadius: 3,
              backgroundColor: 'rgba(0,0,0,0.1)'
            }}
          />
        </Box>

        {/* Alert threshold */}
        <Box sx={{ mt: 2, p: 1, backgroundColor: 'rgba(25, 118, 210, 0.1)', borderRadius: 1 }}>
          <Typography variant="caption" color="text.secondary">
            Alert threshold: {alert.alertThresholdMinutes} min delay
          </Typography>
        </Box>
      </CardContent>
    </Card>
  );
}

export default RouteCard;
