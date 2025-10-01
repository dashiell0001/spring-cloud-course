# Flight Search Service — Docker Build, Local Registry, and Compose (Step-by-Step)


## Start a Local Docker Registry

Run a local registry on port **5000**:

```bash
docker run -d -p 5000:5000 --name registry registry:2
```

---

## Build, Tag, and Push the Image to the Local Registry

From your project root (where the `Dockerfile` is):

```bash
# Build the image
docker build -t flight-search-service:1.0 .

# Tag for local registry
docker tag flight-search-service:2.0 localhost:5000/flight-search-service:1.0

# Push to the local registry
docker push localhost:5000/flight-search-service:1.0
```

(Verify it’s available:)
```bash
docker pull localhost:5000/fflight-search-service:1.0
```

---

## Create `docker-compose.yml` to Pull the Image and Wire PostgreSQL

Create `docker-compose.yml` (you can store it under a `deploy/` folder to keep infra separate):

```yaml
version: "3.9"

services:
  db:
    image: postgres:16-alpine
    container_name: flights-postgres
    environment:
      POSTGRES_DB: flights
      POSTGRES_USER: flight
      POSTGRES_PASSWORD: flight
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U flight -d flights"]
      interval: 5s
      timeout: 5s
      retries: 10
    volumes:
      - pgdata:/var/lib/postgresql/data

  flight-search-service:
    image: localhost:5000/flight-search-service:1.0   # pulled from local registry
    container_name: flight-search-service
    depends_on:
      db:
        condition: service_healthy
    environment:
      # IMPORTANT: use docker service name 'db', not localhost
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/flights
      SPRING_DATASOURCE_USERNAME: flight
      SPRING_DATASOURCE_PASSWORD: flight
      JAVA_TOOL_OPTIONS: "-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
    ports:
      - "8080:8080"
    restart: unless-stopped

volumes:
  pgdata:
```

**Why this works**  
- Compose **pulls** the app image from `localhost:5000`.  
- The app connects to Postgres via the Docker DNS name `db` (not `localhost`).  
- We use a healthcheck so the app waits for Postgres to be ready.

---

## Bring the Stack Up

From the folder containing `docker-compose.yml`:

```bash
docker compose up -d
```

Test endpoints:

```bash
curl http://localhost:8080/actuator/health
curl "http://localhost:8080/api/flights/search?origin=MEX&destination=LAX&dateFrom=2025-12-20&dateTo=2025-12-28"
```

---

## 6) Tear Down (Optional)

```bash
docker compose down
# Optional: stop/remove the local registry
docker rm -f registry
```

