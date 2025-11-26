# ✅ FIXES APPLIED - Next Steps

## What Was Fixed

### 1. Investment Service ✅
**Files Modified:**
- `investment-service/src/main/java/com/nexus/investment_service/config/WebClientConfig.java`
  - Changed from hardcoded `Constants.USER_SERVICE_BASE_URL`
  - Now uses `@Value("${user.service.url:http://localhost:3000}")`
  
- `investment-service/src/main/java/com/nexus/investment_service/utils/Constants.java`
  - Removed hardcoded `USER_SERVICE_BASE_URL = "http://localhost:3000/api/v1/users"`
  - Added documentation comments

### 2. Docker Compose ✅
**File Modified:** `docker-compose.yml`
- Added `USER_SERVICE_URL: http://user-service:3000` to **investment-service**
- Added `USER_SERVICE_URL: http://user-service:3000` to **payment-service**

---

## Next Steps - EXECUTE THESE COMMANDS

### Step 1: Rebuild Investment Service
```bash
cd /Users/I528997/Desktop/BITS/Project/Nexus/investment-service
mvn clean install -DskipTests
```

### Step 2: Stop All Docker Containers
```bash
cd /Users/I528997/Desktop/BITS/Project/Nexus
docker-compose down
```

### Step 3: Rebuild and Start All Services
```bash
docker-compose up -d --build
```

### Step 4: Check Investment Service Logs
```bash
docker logs investment-service
```

**Look for this line (should show user-service, NOT localhost):**
```
Configuring WebClient baseUrl=http://user-service:3000/api/v1/users
```

### Step 5: Test Your API
Try your Bruno request again:
- URL: `http://localhost:8080/nexus/auth/login`
- Should now connect successfully without "Connection refused" error

---

## Verification Checklist

After running the commands above, verify:

- [ ] Investment service builds successfully
- [ ] Docker containers start without errors
- [ ] Investment service logs show: `baseUrl=http://user-service:3000/api/v1/users`
- [ ] No "Connection refused: localhost/127.0.0.1:3000" errors in logs
- [ ] API Gateway login endpoint works from Bruno

---

## If You Still See Errors

### Check Other Services
If you still see localhost errors, check these files:

1. **Payment Service** - `payment-service/src/main/resources/application.properties`
   - Should have: `user.service.base-url=${USER_SERVICE_URL:http://localhost:3000}`
   - Currently has (hardcoded): `user.service.base-url=http://localhost:3000`

2. **Order Service** - Should already be working (uses @Value correctly)

### Quick Check Command
```bash
# Search for remaining localhost references
grep -r "localhost:3000" investment-service/src/main/java
grep -r "localhost:3002" order-service/src/main/java
grep -r "localhost:3004" order-service/src/main/java
```

---

## Summary of the Fix

**Before:**
```java
// Hardcoded - doesn't work in Docker
public static final String USER_SERVICE_BASE_URL = "http://localhost:3000/api/v1/users";
```

**After:**
```java
// Configurable - works both locally and in Docker
@Value("${user.service.url:http://localhost:3000}")
private String userServiceUrl;

String baseUrl = userServiceUrl + "/api/v1/users";
```

**Docker Compose:**
```yaml
environment:
  USER_SERVICE_URL: http://user-service:3000  # Uses service name in Docker
```

This allows:
- **Local development**: Uses `localhost:3000`
- **Docker deployment**: Uses `user-service:3000`

---

## Expected Result

After these changes, when you call the API Gateway at `http://localhost:8080/nexus/auth/login`, the investment service will successfully connect to the user service using the Docker service name instead of localhost.

**Error Before:**
```
Connection refused: localhost/127.0.0.1:3000
```

**Success After:**
```
Successfully connected to user-service:3000
```
