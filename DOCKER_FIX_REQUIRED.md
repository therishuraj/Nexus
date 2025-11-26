# Required Changes to Fix Docker Service Communication

## Problem
Services are using hardcoded `localhost` URLs which don't work in Docker. When running in Docker Compose, services must use Docker service names (e.g., `user-service`, `investment-service`) instead of `localhost`.

---

## Changes Required

### 1. ✅ API-Gateway (ALREADY FIXED)
**Status**: Already updated to use `@Value` with environment variables

**File**: `API-Gateway/src/main/java/com/nexus/api_gateway/service/UserServiceClient.java`
```java
@Value("${user.service.url:http://localhost:3000}")
private String userServiceUrl;
```

**Docker Compose**: Already has environment variable set
```yaml
USER_SERVICE_URL: http://user-service:3000
```

---

### 2. ❌ INVESTMENT-SERVICE (NEEDS FIX)
**Status**: **HARDCODED localhost - MUST BE FIXED**

#### Issue Location
**File**: `investment-service/src/main/java/com/nexus/investment_service/utils/Constants.java`
```java
public static final String USER_SERVICE_BASE_URL = "http://localhost:3000/api/v1/users";
```

**Used in**: `investment-service/src/main/java/com/nexus/investment_service/config/WebClientConfig.java`

#### Required Changes

**Step 1**: Update `Constants.java` to remove hardcoded URL (or make it configurable)

**Option A - Make it configurable (RECOMMENDED)**:
```java
package com.nexus.investment_service.utils;

public class Constants {
    // Remove this line:
    // public static final String USER_SERVICE_BASE_URL = "http://localhost:3000/api/v1/users";
    
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_FUNDED = "FUNDED";
    public static final String STATUS_CLOSED = "CLOSED";
}
```

**Step 2**: Update `WebClientConfig.java` to use @Value:
```java
package com.nexus.investment_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${user.service.url:http://localhost:3000}")
    private String userServiceUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        String baseUrl = userServiceUrl + "/api/v1/users";
        log.info("Configuring WebClient baseUrl={}", baseUrl);
        return builder
                .baseUrl(baseUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                        .build())
                .build();
    }
}
```

**Step 3**: Add environment variable to docker-compose.yml (under investment-service):
```yaml
investment-service:
  build: ./investment-service
  container_name: investment-service
  ports:
    - "3004:3004"
  depends_on:
    kafka:
      condition: service_healthy
  environment:
    SPRING_DATA_MONGODB_URI: mongodb+srv://nexus:nexus5@cluster0.kei0rsa.mongodb.net/funding_requests?retryWrites=true&w=majority&appName=Cluster0
    SERVER_PORT: 3004
    USER_SERVICE_URL: http://user-service:3000  # ADD THIS LINE
  networks:
    - app-network
```

**Step 4**: Create application.properties (if it doesn't exist):
Create file: `investment-service/src/main/resources/application.properties`
```properties
# Server Configuration
server.port=${SERVER_PORT:3004}

# User Service URL
user.service.url=${USER_SERVICE_URL:http://localhost:3000}

# MongoDB Configuration
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017/funding_requests}
```

---

### 3. ⚠️ ORDER-SERVICE (VERIFY)
**Status**: Appears to use @Value correctly, but verify

**Files checked**:
- `order-service/src/main/java/com/razz/orderservice/client/ProductServiceClient.java` ✅
- `order-service/src/main/java/com/razz/orderservice/client/UserServiceClient.java` ✅

**Docker Compose** has environment variables:
```yaml
PRODUCT_SERVICE_URL: http://product-service:3002
USER_SERVICE_URL: http://user-service:3000
INVESTMENT_SERVICE_URL: http://investment-service:3004
```

**Action**: No changes needed if application.properties uses these environment variables

---

### 4. ⚠️ PAYMENT-SERVICE (VERIFY)
**Status**: Has hardcoded localhost in application.properties

**Issue**: `payment-service/src/main/resources/application.properties` has:
```properties
user.service.base-url=http://localhost:3000
```

**Required Change**: Update to use environment variable:
```properties
user.service.base-url=${USER_SERVICE_URL:http://localhost:3000}
```

**Docker Compose**: Add environment variable (currently missing):
```yaml
payment-service:
  build: ./payment-service
  container_name: payment-service
  ports:
    - "3006:3006"
  depends_on:
    kafka:
      condition: service_healthy
  environment:
    SPRING_DATA_MONGODB_URI: mongodb+srv://nexus:nexus5@cluster0.kei0rsa.mongodb.net/nexus_products?retryWrites=true&w=majority&appName=Cluster0
    SERVER_PORT: 3006
    USER_SERVICE_URL: http://user-service:3000  # ADD THIS LINE
  networks:
    - app-network
```

---

## Summary of Required Actions

### Immediate Priority (Fixes the current error):

1. **Fix investment-service**:
   - [ ] Remove hardcoded URL from `Constants.java`
   - [ ] Update `WebClientConfig.java` to use `@Value("${user.service.url}")`
   - [ ] Create `application.properties` with configurable URL
   - [ ] Add `USER_SERVICE_URL` environment variable to docker-compose.yml

2. **Fix payment-service**:
   - [ ] Update `application.properties` to use environment variable
   - [ ] Add `USER_SERVICE_URL` environment variable to docker-compose.yml

3. **Rebuild and restart**:
   ```bash
   cd /Users/I528997/Desktop/BITS/Project/Nexus
   docker-compose down
   docker-compose up -d --build
   ```

---

## Testing After Changes

1. **Check logs**:
   ```bash
   docker logs investment-service
   docker logs payment-service
   ```

2. **Look for**:
   - ✅ "Configuring WebClient baseUrl=http://user-service:3000/api/v1/users"
   - ❌ NOT "http://localhost:3000"

3. **Test API**:
   - Try the Bruno request again to `http://localhost:8080/nexus/auth/login`
   - Should no longer see "Connection refused: localhost/127.0.0.1:3000"

---

## Root Cause

**Why this happens**: 
- In Docker, each service runs in its own container
- `localhost` inside a container refers to that container only
- Services must use Docker service names from docker-compose.yml
- Environment variables allow the same code to work locally (localhost) and in Docker (service names)

**Solution Pattern**:
```java
@Value("${service.url:http://localhost:3000}")  // Default for local dev
private String serviceUrl;
```

```yaml
environment:
  SERVICE_URL: http://service-name:3000  # Override for Docker
```
