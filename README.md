# SmartCampusAPI

A JAX-RS REST API for Smart Campus room, sensor, and sensor-reading management.

## Tech Stack

- Java 17
- Jakarta REST (JAX-RS)
- Maven (WAR packaging)
- In-memory storage (`HashMap` / `ArrayList`) only

## API Base URL

Use one of these depending on your deployed context path:

- `http://localhost:8080/SmartCampusAPI/api/v1`
- `http://localhost:8080/SmartCampusAPI-1.0-SNAPSHOT/api/v1`

## Run The Project (Easy Way)

1. Open the project in NetBeans.
2. Ensure GlassFish is configured as the target server.
3. Run `Clean and Build`.
4. Run the project.
5. Test the discovery endpoint:
   - `GET /api/v1`

## Main Endpoints

- `GET /api/v1`
- `GET /api/v1/rooms`
- `POST /api/v1/rooms`
- `GET /api/v1/rooms/{id}`
- `DELETE /api/v1/rooms/{id}`
- `GET /api/v1/sensors`
- `GET /api/v1/sensors?type=Temperature`
- `POST /api/v1/sensors`
- `GET /api/v1/sensors/{id}/readings`
- `POST /api/v1/sensors/{id}/readings`

## Sample curl Commands

Replace `BASE_URL` with one valid base URL above.

```bash
curl -X GET "BASE_URL"
```

```bash
curl -X GET "BASE_URL/rooms"
```

```bash
curl -X POST "BASE_URL/rooms" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "LAB-201",
    "name": "AI Lab",
    "capacity": 40,
    "sensorIds": []
  }'
```

```bash
curl -X POST "BASE_URL/sensors" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "TEMP-101",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 23.7,
    "roomId": "LAB-201"
  }'
```

```bash
curl -X GET "BASE_URL/sensors?type=Temperature"
```

```bash
curl -X POST "BASE_URL/sensors/TEMP-101/readings" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "READ-001",
    "value": 24.9
  }'
```

```bash
curl -X DELETE "BASE_URL/rooms/LAB-201"
```

## Error Handling

The API returns structured JSON errors for validation and not-found cases.
It also includes specific exception mapping for:

- `409 Conflict` when deleting non-empty rooms
- `422 Unprocessable Entity` for invalid linked `roomId` on sensor creation
- `403 Forbidden` when posting a reading to a sensor in `MAINTENANCE`
- `500 Internal Server Error` for unexpected runtime exceptions

## Logging

A JAX-RS request/response filter logs:

- Incoming method + URI
- Outgoing status code
