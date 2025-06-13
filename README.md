# Database Cleanup Service

A Spring Boot application that performs scheduled database cleanup operations using ShedLock for distributed locking and Flyway for database migrations.

## Features

- **Scheduled Database Cleanup**: Automatically cleans up old records from `audit_logs` and `app_version` tables.
- **Distributed Locking**: Uses ShedLock to ensure only one instance performs cleanup at a time for each scheduled task.
- **Database Migrations**: Uses Flyway with versioned migration naming.
- **PostgreSQL Integration**: Optimized for PostgreSQL database.
- **REST API**: Placeholder for potential manual cleanup endpoints (not fully implemented).
- **Health Monitoring**: Spring Boot Actuator endpoints for monitoring.

## Project Structure

```
.gitignore
src/
├── main/
│   ├── java/com/example/cleanupservice/
│   │   ├── ShedlockExampleApplication.java
│   │   ├── config/
│   │   │   └── ShedLockConfiguration.java
│   │   ├── entity/
│   │   │   ├── AuditLog.java
│   │   │   └── AppVersion.java
│   │   ├── repository/
│   │   │   ├── AuditLogRepository.java
│   │   │   └── AppVersionRepository.java
│   │   └── service/
│   │       └── DatabaseCleanupService.java
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│           ├── V1__initial_schema.sql
│           ├── V2__app_version.sql
│           └── V3__insert_app_version_data.sql
├── build.gradle
├── gradlew
├── settings.gradle
├── docker-compose.yml
├── Dockerfile
└── README.md
```

## Database Tables

The application manages the following tables:

- **shedlock**: Distributed locking mechanism for ShedLock.
- **audit_logs**: User activity audit trail.
- **app_version**: Application version and session tracking.

## Seed Data

The application includes seed data to facilitate testing and development:
- The `audit_logs` table is created by [`V1__initial_schema.sql`](src/main/resources/db/migration/V1__initial_schema.sql). The cleanup service is designed to remove old records from this table.
- For the `app_version` table, example entries are defined in [`V3__insert_app_version_data.sql`](src/main/resources/db/migration/V3__insert_app_version_data.sql). Some entries have `last_read` timestamps older than 90 days for cleanup testing.

## CRON Schedules

The cleanup tasks are configured with the following default CRON expressions in `DatabaseCleanupService`:

- **Audit Log Cleanup**: `0 */1 * * * ?` (Runs every minute)
  - Cleans records from `audit_logs`.
- **AppVersion Cleanup**: `0 */2 * * * ?` (Runs every 2 minutes)
  - Cleans records from `app_version`.

These schedules ensure frequent cleanup for testing purposes. Adjust them in `DatabaseCleanupService` or via `application.yml` for production environments.

## Configuration

### Application Properties

The application can be configured through `src/main/resources/application.yml`:

```yaml
cleanup:
  audit-log-retention-days: 90 # Default 90 days
  app-version-retention-days: 90 # Default 90 days
  # scheduled: # To override cron expressions from DatabaseCleanupService, uncomment and set here
    # audit-cleanup-cron: "0 0 2 * * ?" # Example: Daily at 2 AM
    # app-version-cleanup-cron: "0 0 3 * * ?" # Example: Daily at 3 AM
```

### Database Configuration

Update the database connection settings in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cleanup_db
    username: cleanup_user
    password: cleanup_password # Consider using environment variables or secrets management for passwords
```

## Running the Application

### Using Docker Compose (Recommended)

1.  Start the services:
    ```bash
    docker-compose up -d --build
    ```
    This will build the image if not present and start both PostgreSQL and the cleanup service.

### Manual Setup

1.  **Setup PostgreSQL** (if not using Docker):
    Ensure you have a PostgreSQL instance running. Create the database and user:
    ```sql
    CREATE DATABASE cleanup_db;
    CREATE USER cleanup_user WITH PASSWORD 'your_strong_password'; -- Use a strong password
    GRANT ALL PRIVILEGES ON DATABASE cleanup_db TO cleanup_user;
    ```

2.  **Build and run the application**:
    ```bash
    ./gradlew build
    java -jar build/libs/shedlock-example-0.0.1-SNAPSHOT.jar
    ```
    Alternatively, to run directly using Gradle:
    ```bash
    ./gradlew bootRun
    ```

## API Endpoints

Currently, no specific API endpoints for manual cleanup are fully implemented. The focus is on scheduled cleanup.

### Health & Monitoring (Actuator)

-   `GET /actuator/health` - Application health status
-   `GET /actuator/metrics` - Application metrics
-   `GET /actuator/flyway` - Flyway migration information
-   `GET /actuator/scheduledtasks` - Information about scheduled tasks

## Migration Naming Convention

The project uses Flyway's versioned migration naming convention:
-   Pattern: `V{VERSION_NUMBER}__{description}.sql`
-   Example: `V1__initial_schema.sql`

## ShedLock Configuration

ShedLock ensures that scheduled tasks run only on one instance in a distributed environment:
-   **Lock Table**: `shedlock` (created by `V1__initial_schema.sql`)
-   **Provider**: JDBC Template
-   Lock durations (`lockAtMostFor`, `lockAtLeastFor`) are configured per task in `DatabaseCleanupService`.

## Development

### Adding New Cleanup Tasks

1.  Add a new scheduled method to `DatabaseCleanupService.java`.
2.  Annotate it with `@Scheduled` and `@SchedulerLock` (provide a unique lock name).
3.  Implement the cleanup logic, typically involving a new Repository method.
4.  Add relevant configuration properties (e.g., retention days, CRON expression) to `application.yml` and use `@Value` in the service.

### Adding New Migrations

1.  Create a new SQL file in `src/main/resources/db/migration/`.
2.  Follow the naming convention: `V{NEXT_VERSION_NUMBER}__{description}.sql`.
3.  Include necessary DDL (table creations, alterations) or DML (data insertions, updates).

## Monitoring

-   Application logs provide details on cleanup operations (start, completion, records deleted, errors).
-   ShedLock logs information about lock acquisition and release (configure `net.javacrumbs.shedlock` logging level to `DEBUG` or `TRACE` for more details).
-   Spring Boot Actuator endpoints provide runtime insights.

## Dependencies

Key dependencies used:

-   Spring Boot `3.5.0`
-   Spring Data JPA
-   PostgreSQL Driver
-   Flyway (`org.flywaydb:flyway-database-postgresql:11.9.1`)
-   ShedLock (`5.10.0`)
-   Lombok
-   Spring Boot Actuator

## Security Considerations

-   Database credentials should be externalized from `application.yml` in production environments (e.g., use environment variables, Spring Cloud Config, HashiCorp Vault).
-   Review and set appropriate retention periods based on data sensitivity and compliance requirements.
-   Monitor application logs and database activity for any unusual patterns.

## Testing

Unit and integration tests can be added to the `src/test/java` directory. Run tests using:
```bash
./gradlew test
```
