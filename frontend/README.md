# 🚇 Transit Monitor Frontend

A modern React application for monitoring transit routes with real-time analytics and delay alerts.

## ✨ Features

- **Route Monitoring**: Add and monitor transit routes with start/end stops
- **Real-time Analytics**: Track travel times, delays, and on-time performance
- **Smart Alerts**: Get notified when delays exceed your threshold
- **Beautiful UI**: Modern Material-UI design with responsive layout
- **Auto-refresh**: Data updates every 30 seconds automatically

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ 
- Backend running on http://localhost:8080

### Installation & Run

```bash
# Navigate to frontend directory
cd frontend/frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The app will be available at http://localhost:5173

## 🎯 How to Use

### 1. Add a New Route
- Click the **"New Route"** button
- Select your start and end stops from the dropdown
- Choose the route you want to monitor
- Set your delay threshold (e.g., 5 minutes)
- Click **"Create Route Monitor"**

### 2. Monitor Your Routes
Each route card shows:
- **Next Arrival**: When the next vehicle is expected
- **Travel Time**: Total time between your stops
- **Average Delay**: 7-day average delay in minutes
- **On-Time Performance**: Percentage of on-time arrivals
- **Alert Threshold**: Your configured delay threshold

### 3. Route Cards
- **Green**: Excellent performance (90%+ on-time)
- **Orange**: Good performance (70-89% on-time)  
- **Red**: Poor performance (<70% on-time)

## 🛠️ Technical Stack

- **React 19** - Modern React with hooks
- **Material-UI** - Beautiful component library
- **Vite** - Fast build tool and dev server
- **JavaScript** - No TypeScript complexity

## 📱 Responsive Design

The app works perfectly on:
- Desktop computers
- Tablets
- Mobile phones

## 🔧 Configuration

The app connects to the backend at `http://localhost:8080` by default.

To change the API URL, create a `.env` file:
```
VITE_API_BASE=http://your-backend-url:port
```

## 🎨 UI Components

- **App.jsx** - Main application with route management
- **RouteCard.jsx** - Individual route monitoring cards
- **RouteDialog.jsx** - Add new route popup dialog
- **client.js** - API communication layer

## 🚀 Production Build

```bash
npm run build
```

This creates optimized production files in the `dist/` directory.

## 🔄 Auto-refresh

The app automatically refreshes data every 30 seconds to show the latest:
- Next arrival times
- Delay statistics  
- On-time performance
- Route alerts

## 🎯 Next Steps

Your frontend is now ready! The app provides:
- ✅ Beautiful route monitoring interface
- ✅ Real-time data from your backend
- ✅ Smart delay alerts
- ✅ Responsive design
- ✅ Easy route management

Start monitoring your transit routes! 🚇📊
