# Transit App Backend - Component Overview

## ✅ **All Issues Fixed and Setup Complete**

---

## **Architecture Overview**

This is a **Spring Boot 3.3.2** application using **Java 21** with the following stack:

### **Core Technologies**
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA (Hibernate)
- **Migration**: Flyway
- **Build Tools**: Maven & Gradle (both configured)
- **Security**: Spring Security
- **Real-time Data**: GTFS-realtime bindings
- **Monitoring**: Spring Actuator

---

## **Application Structure**

### **1. Configuration Layer** (`config/`)
- **`CorsConfig.java`**: CORS configuration allowing frontend (localhost:5174) to access API
- **`SecurityConfig.java`**: Spring Security configuration (permits all API and actuator endpoints)

### **2. Model Layer** (`model/`)
JPA entities mapping to database tables:
- **`User.java`**: User authentication (UUID-based, with MFA support)
- **`Stop.java`**: Transit stops with geographic coordinates
- **`Route.java`**: Transit routes (short/long names)
- **`StopTime.java`**: Scheduled stop times for trips
- **`Arrival.java`**: Actual arrival records with delay tracking

### **3. Repository Layer** (`repository/`)
Spring Data JPA repositories for data access:
- `StopRepository`, `RouteRepository`, `StopTimeRepository`, `ArrivalRepository`
- Custom query: `findByStopIdAndRouteId()` in `StopTimeRepository`

### **4. Service Layer** (`service/`)
- **`EtaService.java`**: 
  - Fetches and parses GTFS-realtime feeds
  - In-memory cache of ETAs keyed by `stopId|routeId`
  - Returns up to 5 upcoming arrivals per stop/route combination
  - Tracks scheduled times and delays
  
- **`RealTimePoller.java`**: 
  - Scheduled task (@Scheduled) 
  - Polls GTFS-realtime feed every 20 seconds (configurable via `poll.intervalMs`)
  - Calls `EtaService.refresh()`

### **5. Web Layer** (`web/`)
- **`EtaController.java`**: 
  - REST API endpoint: `GET /api/eta?stopId=XXX&routeId=YYY`
  - Returns real-time ETA list for specified stop and route

### **6. Main Application**
- **`TransitBackendApplication.java`**: 
  - Entry point with `@SpringBootApplication`
  - Enables scheduling with `@EnableScheduling`

---

## **Database Schema** (Flyway Migrations)

### **V1__init.sql** - Initial Tables
- `users`: Authentication with email, password, MFA flag
- `stops`: Transit stops (id, name, lat, lon)
- `routes`: Transit routes (id, short_name, long_name)

### **V2__stop_times.sql** - Scheduled Data
- `stop_times`: Trip schedules with stop sequences and arrival times
- Indexes on `stop_id` and `(route_id, scheduled_arrival_ts)`

### **V3__arrivals.sql** - Historical Data
- `arrivals`: Actual arrival records with delays
- Index on `(stop_id, actual_arrival_ts)`

---

## **Configuration Files**

### **application.yaml**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/transit
    username: transit
    password: transit
  jpa:
    hibernate.ddl-auto: validate  # Flyway manages schema
    show-sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration
  gtfs:
    staticZipUrl: "https://YOUR-AGENCY/gtfs.zip"
    realtimeFeedUrl: "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/nyct%2Fgtfs"
  poll:
    intervalMs: 20000  # Poll every 20 seconds
```

### **docker-compose.yaml**
- PostgreSQL 15 container
- Port: 5432
- Database/User/Password: `transit`
- Persistent volume: `pgdata`

---

## **Dependencies**

### **Maven (pom.xml)** & **Gradle (build.gradle)**
Both build systems are configured with:
- Spring Boot Web, JPA, Security, Actuator
- PostgreSQL driver
- Flyway migration tool
- GTFS-realtime bindings (0.0.4) - **Note: 0.0.5 doesn't exist in Maven Central**
- Apache Commons CSV (1.11.0)
- Lombok (optional - not required, manual constructors used)

---

## **API Endpoints**

### **ETA Endpoint**
```
GET /api/eta?stopId={stopId}&routeId={routeId}
```

**Response**: Array of ETA objects
```json
[
  {
    "stopId": "123",
    "routeId": "M15",
    "scheduledTs": 1697000000000,
    "delaySec": 120
  }
]
```

### **Actuator Endpoints**
- `/actuator/health` - Health check
- `/actuator/info` - Application info
- All actuator endpoints are accessible (configured in SecurityConfig)

---

## **Issues Fixed**

### ✅ **1. Corrected Package Structure**
- Moved `Service/` → `service/` (under proper Java package path)
- Moved `web/` → `web/` (under proper Java package path)
- Renamed `Config/` → `config/` (lowercase to match package declaration)

### ✅ **2. Added Missing Import**
- Added `@EnableScheduling` import in `TransitBackendApplication.java`

### ✅ **3. Fixed Dependencies**
- Changed GTFS-realtime bindings from 0.0.5 to **0.0.4** (0.0.5 doesn't exist in Maven Central)
- Added GTFS-rt bindings and Commons CSV to `build.gradle`
- Added Spring Boot Configuration Processor

### ✅ **4. Fixed Class Name Mismatch**
- Renamed `RealtimePoller` class to `RealTimePoller` to match filename

### ✅ **5. Replaced Lombok with Manual Code**
- Removed `@Slf4j` and created manual `Logger` in `EtaService`
- Removed `@RequiredArgsConstructor` and created manual constructors
- This fixes compilation issues with Lombok annotation processing

### ✅ **6. Created Missing Components**
- Created all JPA entity classes (User, Stop, Route, StopTime, Arrival)
- Created all repository interfaces
- Created SecurityConfig to properly configure Spring Security

### ✅ **7. Directory Structure**
```
backend/src/main/java/com/example/transit/
├── config/
│   ├── CorsConfig.java
│   └── SecurityConfig.java
├── model/
│   ├── Arrival.java
│   ├── Route.java
│   ├── Stop.java
│   ├── StopTime.java
│   └── User.java
├── repository/
│   ├── ArrivalRepository.java
│   ├── RouteRepository.java
│   ├── StopRepository.java
│   └── StopTimeRepository.java
├── service/
│   ├── EtaService.java
│   └── RealTimePoller.java
├── web/
│   └── EtaController.java
└── TransitBackendApplication.java
```

---

## **How to Run**

### **1. Start PostgreSQL**
```bash
cd backend
docker-compose up -d
```

### **2. Run with Maven**
```bash
./mvnw spring-boot:run
```

### **3. Or run with Gradle**
```bash
./gradlew bootRun
```

### **4. Verify**
- Application starts on port 8080 (default)
- Flyway migrations run automatically
- GTFS-realtime polling starts immediately
- Access: `http://localhost:8080/api/eta?stopId=XXX&routeId=YYY`

---

## **Configuration TODO**

Before production use, update in `application.yaml`:
1. **GTFS Feed URLs**: Replace placeholder URLs with your actual transit agency feeds
2. **Database Credentials**: Use secure passwords (not "transit")
3. **CORS Origins**: Update to match your production frontend URL
4. **Security**: Implement proper authentication for sensitive endpoints

---

## **Data Flow**

1. **Scheduled Polling**: `RealTimePoller` triggers every 20 seconds
2. **Fetch GTFS-RT**: `EtaService.refresh()` downloads protobuf feed
3. **Parse & Cache**: Extracts ETAs, stores in-memory by stopId|routeId
4. **API Request**: Frontend calls `/api/eta` with stopId and routeId
5. **Response**: `EtaController` retrieves from `EtaService` cache and returns JSON

---

## **Next Steps**

1. ✅ **Backend structure is correct and ready**
2. 📝 Populate `stops` and `routes` tables with GTFS static data
3. 🔑 Update GTFS feed URLs in application.yaml
4. 🚀 Start the application and test the API
5. 🔐 Implement authentication endpoints if needed
6. 📊 Add endpoints for historical data analysis from `arrivals` table

---

**Status**: ✅ All backend components are properly structured and configured!

