# Transit App Backend - Quick Start Guide

## ✅ **Status: FIXED & READY TO RUN**

All compilation errors have been resolved!

---

## **What Was Fixed**

### 1. **Dependency Error** ⚠️ → ✅
**Problem**: `com.google.transit:gtfs-realtime-bindings:jar:0.0.5` not found in Maven Central

**Solution**: Changed version from `0.0.5` to `0.0.4` (the latest available version)

### 2. **Class Name Mismatch** ⚠️ → ✅
**Problem**: Class `RealtimePoller` didn't match filename `RealTimePoller.java`

**Solution**: Renamed class to `RealTimePoller`

### 3. **Lombok Compilation Issues** ⚠️ → ✅
**Problem**: Lombok annotations (`@Slf4j`, `@RequiredArgsConstructor`) weren't being processed

**Solution**: Removed Lombok annotations and added manual code:
- Manual `Logger` creation in `EtaService`
- Manual constructors in `EtaService`, `RealTimePoller`, and `EtaController`

---

## **How to Run**

### **Step 1: Start PostgreSQL Database**

```bash
cd backend
docker-compose up -d
```

This starts PostgreSQL on port 5432 with:
- Database: `transit`
- Username: `transit`
- Password: `transit`

### **Step 2: Run the Application**

Choose one of the following:

#### **Option A: Using Maven**
```bash
./mvnw spring-boot:run
```

#### **Option B: Using Gradle**
```bash
./gradlew bootRun
```

#### **Option C: Build JAR and Run**
```bash
# Maven
./mvnw clean package -DskipTests
java -jar target/transit-backend-0.0.1-SNAPSHOT.jar

# Or Gradle
./gradlew build -x test
java -jar build/libs/transit-backend-0.0.1-SNAPSHOT.jar
```

### **Step 3: Verify It's Running**

The application should start on **port 8080**.

Check the logs for:
```
Started TransitBackendApplication in X.XXX seconds
GTFS-rt refresh @... entries=...
```

Test the API:
```bash
curl "http://localhost:8080/api/eta?stopId=123&routeId=M15"
```

Check health:
```bash
curl http://localhost:8080/actuator/health
```

---

## **Before Using in Production**

⚠️ **Important Configuration Changes Needed:**

### 1. **Update GTFS Feed URLs** (in `application.yaml`)

```yaml
gtfs:
  staticZipUrl: "https://YOUR-AGENCY/gtfs.zip"  # Replace with actual URL
  realtimeFeedUrl: "https://YOUR-AGENCY/gtfs-realtime"  # Replace with actual URL
```

### 2. **Configure Database** (optional for production)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-db-host:5432/transit
    username: ${DB_USERNAME}  # Use env variables
    password: ${DB_PASSWORD}
```

### 3. **Update CORS Origins** (for production frontend)

```yaml
spring:
  web:
    cors:
      allowed-origins: "https://your-production-domain.com"
```

Or update `config/CorsConfig.java` directly.

---

## **Common Issues**

### **Issue: Database Connection Failed**

```
Error: Connection to localhost:5432 refused
```

**Solution**: Make sure PostgreSQL is running:
```bash
docker-compose ps
```

If not running, start it:
```bash
docker-compose up -d
```

### **Issue: Port 8080 Already in Use**

**Solution**: Either:
1. Stop the process using port 8080
2. Or change the port in `application.yaml`:
```yaml
server:
  port: 8081
```

### **Issue: Flyway Migration Errors**

**Solution**: If you need to reset the database:
```bash
docker-compose down -v
docker-compose up -d
```

Then restart the application.

---

## **API Documentation**

### **Get ETAs**
```
GET /api/eta?stopId={stopId}&routeId={routeId}
```

**Response:**
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

**Note**: The ETA cache is populated by the real-time poller every 20 seconds. Initial requests may return empty arrays until the first poll completes.

---

## **Development Tips**

### **View Database Tables**

```bash
docker exec -it backend-db-1 psql -U transit -d transit

# Inside psql:
\dt              # List tables
\d stops         # Describe stops table
SELECT * FROM stops LIMIT 5;
```

### **Watch Logs**

```bash
# Follow application logs
tail -f backend/target/spring-boot.log

# Or if running in Docker
docker-compose logs -f
```

### **Hot Reload**

For development, use Spring DevTools (already included in dependencies):
- Changes to code will auto-restart the application
- Changes to resources (like application.yaml) reload automatically

---

## **Next Steps**

1. ✅ **Backend compiles successfully**
2. 📝 Populate `stops` and `routes` tables with GTFS static data
3. 🔑 Update GTFS feed URLs in `application.yaml`
4. 🚀 Test the `/api/eta` endpoint with real data
5. 🔐 Implement authentication endpoints if needed
6. 📊 Add endpoints for historical data analysis

---

**Status**: ✅ **Ready to Run!**

The backend is fully set up and compiles successfully. Run `mvn spring-boot:run` or `./gradlew bootRun` to start!

