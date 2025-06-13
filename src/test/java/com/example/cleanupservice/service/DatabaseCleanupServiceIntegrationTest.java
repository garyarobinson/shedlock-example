package com.example.cleanupservice.service;

import com.example.cleanupservice.entity.AppVersion;
import com.example.cleanupservice.entity.AuditLog;
import com.example.cleanupservice.repository.AppVersionRepository;
import com.example.cleanupservice.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test") // Optional: if you have an application-test.yml for other settings
class DatabaseCleanupServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3-alpine"));

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private DatabaseCleanupService databaseCleanupService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AppVersionRepository appVersionRepository;

    // Define retention days for testing; these will override @Value defaults if set via application-test.yml or here
    // For this test, we rely on the service's default or application.yml configured values.
    // To make tests more deterministic, we could set these via @DynamicPropertySource as well.
    // For now, we will assume the default 90 days is used or as per application.yml
    // Let's use a shorter retention for easier testing by setting properties dynamically.

    @DynamicPropertySource
    static void overrideCleanupProperties(DynamicPropertyRegistry registry) {
        registry.add("cleanup.audit-log-retention-days", () -> "1"); // 1 day for audit logs
        registry.add("cleanup.app-version-retention-days", () -> "1"); // 1 day for app versions
    }

    @BeforeEach
    void cleanupDatabase() {
        auditLogRepository.deleteAll();
        appVersionRepository.deleteAll();
        // Note: ShedLock table is managed by Flyway and doesn't need per-test cleanup unless specific test cases require it.
    }

    @Test
    void cleanupAuditLogs_shouldDeleteOldLogsAndKeepNewLogs() {
        // Given: Audit logs older than 1 day and newer than 1 day
        LocalDateTime now = LocalDateTime.now();
        AuditLog oldLog1 = AuditLog.builder().userId("user1").action("LOGIN").createdAt(now.minusDays(2)).build();
        AuditLog oldLog2 = AuditLog.builder().userId("user2").action("VIEW").createdAt(now.minusHours(25)).build(); // More than 1 day
        AuditLog newLog1 = AuditLog.builder().userId("user3").action("CREATE").createdAt(now.minusHours(12)).build();
        AuditLog newLog2 = AuditLog.builder().userId("user4").action("UPDATE").createdAt(now.minusMinutes(5)).build();

        auditLogRepository.saveAll(List.of(oldLog1, oldLog2, newLog1, newLog2));
        assertThat(auditLogRepository.count()).isEqualTo(4);

        // When
        databaseCleanupService.cleanupAuditLogs();

        // Then
        List<AuditLog> remainingLogs = auditLogRepository.findAll();
        assertThat(remainingLogs).hasSize(2);
        assertThat(remainingLogs).extracting(AuditLog::getUserId).containsExactlyInAnyOrder("user3", "user4");
    }

    @Test
    void cleanupAppVersions_shouldDeleteOldVersionsAndKeepNewVersions() {
        // Given: App versions with lastRead older than 1 day and newer than 1 day
        OffsetDateTime now = OffsetDateTime.now();
        AppVersion oldVersion1 = AppVersion.builder().applicationName("app1").applicationId("id1").organizationId("org1").lastRead(now.minusDays(2)).build();
        AppVersion oldVersion2 = AppVersion.builder().applicationName("app2").applicationId("id2").organizationId("org2").lastRead(now.minusHours(25)).build(); // More than 1 day
        AppVersion newVersion1 = AppVersion.builder().applicationName("app3").applicationId("id3").organizationId("org3").lastRead(now.minusHours(12)).build();
        AppVersion newVersion2 = AppVersion.builder().applicationName("app4").applicationId("id4").organizationId("org4").lastRead(now.minusMinutes(5)).build();

        appVersionRepository.saveAll(List.of(oldVersion1, oldVersion2, newVersion1, newVersion2));
        assertThat(appVersionRepository.count()).isEqualTo(4);

        // When
        databaseCleanupService.cleanupAppVersions();

        // Then
        List<AppVersion> remainingVersions = appVersionRepository.findAll();
        assertThat(remainingVersions).hasSize(2);
        assertThat(remainingVersions).extracting(AppVersion::getApplicationName).containsExactlyInAnyOrder("app3", "app4");
    }
}

