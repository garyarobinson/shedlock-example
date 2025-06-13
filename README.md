# Database Cleanup Service

A Spring Boot application that performs scheduled database cleanup operations using ShedLock for distributed locking and Flyway for database migrations.

## Features

- **Scheduled Database Cleanup**: Automatically cleans up old records from audit logs, error logs, and temporary data
- **Distributed Locking**: Uses ShedLock to ensure only one instance performs cleanup at a time
- **Database Migrations**: Uses Flyway with timestamp-based migration naming
- **PostgreSQL Integration**: Optimized for PostgreSQL database
- **REST API**: Manual cleanup endpoints for on-demand operations
- **Statistics Tracking**: Records cleanup operation statistics
- **Health Monitoring**: Spring Boot Actuator endpoints for monitoring

## Project Structure

```
src/
├── main/
│   ├── java/com/example/cleanupservice/
│   │   ├── CleanupServiceApplication.java
│   │   ├── config/
│   │   │   └── ShedLockConfiguration.java
│   │   ├── controller/
│   │   │   └── CleanupController.java
│   │   └── service/
│   │       ├── DatabaseCleanupService.java
│   │       └── CleanupStatisticsService.java
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│           ├── T2021.03.18.13.00__initial_schema.sql
│           └── T2021.03.25.10.30__add_indexes_and_constraints.sql
├── build.gradle
├── settings.gradle
├── docker-compose.yml
├── Dockerfile
└── README.md
```

## Database Tables

The application manages the following tables:

- **shedlock**: Distributed locking mechanism
- **audit_logs**: User activity audit trail
- **error_logs**: Application error logging
- **temp_data**: Temporary data storage
- **cleanup_statistics**: Cleanup operation tracking

## Seed Data

The application includes seed data for the `audit_logs` table to facilitate testing and development. Some entries are older than 90 days and will be deleted during the scheduled cleanup process. These entries are defined in the `T2021.03.18.13.00__initial_schema.sql` migration file.

## CRON Schedule

The cleanup task is configured to run every minute by default. This is defined in the `DatabaseCleanupService` class with the following CRON expression:

```
0 */1 * * * ?
```

This ensures frequent cleanup of outdated records for testing purposes. Adjust the schedule in production as needed.

## Configuration

### Application Properties

The application can be configured through `application.yml`:

```yaml
cleanup:
  audit-log-retention-days: 90
  temp-data-retention-days: 7
  error-log-retention-days: 30
  scheduled:
    audit-cleanup-cron: "0 0 2 * * ?"
    temp-cleanup-cron: "0 30 1 * * ?"
    error-cleanup-cron: "0 0 3 * * SUN"
```

### Database Configuration

Update the database connection settings:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cleanup_db
    username: cleanup_user
    password: cleanup_password
```

## Running the Application

### Using Docker Compose (Recommended)

1. Start the services:
```bash
docker-compose up -d
```

This will start both PostgreSQL and the cleanup service.

### Manual Setup

1. **Setup PostgreSQL**:
```sql
CREATE DATABASE cleanup_db;
CREATE USER cleanup_user WITH PASSWORD 'cleanup_password';
GRANT ALL PRIVILEGES ON DATABASE cleanup_db TO cleanup_user;
```

2. **Build and run**:
```bash
./gradlew build
./gradlew bootRun
```

## API Endpoints

### Manual Cleanup
- `POST /api/cleanup/audit-logs?days=90` - Trigger manual audit log cleanup
- `GET /api/cleanup/status` - Check cleanup service status

### Health & Monitoring
- `GET /actuator/health` - Application health status
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/flyway` - Flyway migration information

## Migration Naming Convention

The project uses timestamp-based migration naming:
- Pattern: `T{YYYY.MM.DD.HH.MM}__{description}.sql`
- Example: `T2021.03.18.13.00__initial_schema.sql`

## ShedLock Configuration

ShedLock ensures that scheduled tasks run only on one instance in a distributed environment:

- **Default Lock Duration**: 10 minutes
- **Lock Table**: `shedlock`
- **Provider**: JDBC Template

## Development

### Adding New Cleanup Tasks

1. Add the scheduled method to `DatabaseCleanupService`
2. Use `@SchedulerLock` annotation with unique lock name
3. Record statistics using `CleanupStatisticsService`
4. Add configuration properties for retention periods

### Adding New Migrations

1. Create new SQL file in `src/main/resources/db/migration/`
2. Follow naming convention: `T{timestamp}__{description}.sql`
3. Include appropriate indexes and constraints

## Monitoring

The application includes comprehensive logging and statistics tracking:

- Cleanup operations are logged with details
- Statistics are stored in `cleanup_statistics` table
- Spring Boot Actuator provides health checks and metrics

## Dependencies

Key dependencies used:

- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL Driver
- Flyway Core
- ShedLock 5.10.0
- Spring Boot Actuator

## Security Considerations

- Database credentials should be externalized
- Consider using connection pooling for production
- Monitor cleanup statistics for unusual patterns
- Set appropriate retention periods based on compliance requirements

## How to Run

1. **Build the Application**:
   ```bash
   ./gradlew build
   ```

2. **Run the Application**:
   ```bash
   java -jar build/libs/shedlock-example-0.0.1-SNAPSHOT.jar
   ```

3. **Access the REST API**:
   Use tools like Postman or cURL to interact with the manual cleanup endpoints.

4. **Monitor Health**:
   Access Spring Boot Actuator endpoints for health and metrics monitoring.

## Docker Support

The application includes a `Dockerfile` and `docker-compose.yml` for containerized deployment. Use the following commands to build and run the application in Docker:

```bash
docker-compose up --build
```

## Testing

Unit and integration tests are included in the `test` directory. Run tests using:

```bash
./gradlew test
```

## Notes

- Ensure the PostgreSQL database is running and accessible before starting the application.
- Update the `application.yml` file with the correct database credentials and configurations.
