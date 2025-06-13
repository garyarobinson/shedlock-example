package com.example.cleanupservice.service;

import com.example.cleanupservice.repository.AppVersionRepository;
import com.example.cleanupservice.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseCleanupServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AppVersionRepository appVersionRepository;

    @InjectMocks
    private DatabaseCleanupService databaseCleanupService;

    private final int auditLogRetentionDays = 90;
    private final int appVersionRetentionDays = 60;

    @BeforeEach
    void setUp() {
        // Inject values for retention days
        ReflectionTestUtils.setField(databaseCleanupService, "auditLogRetentionDays", auditLogRetentionDays);
        ReflectionTestUtils.setField(databaseCleanupService, "appVersionRetentionDays", appVersionRetentionDays);
    }

    @Test
    void cleanupAuditLogs_shouldDeleteOldLogs_whenSuccessful() {
        // Given
        int expectedDeletedRecords = 5;
        when(auditLogRepository.deleteByCreatedAtBefore(any(LocalDateTime.class))).thenReturn(expectedDeletedRecords);

        // When
        databaseCleanupService.cleanupAuditLogs();

        // Then
        verify(auditLogRepository, times(1)).deleteByCreatedAtBefore(any(LocalDateTime.class));
    }

    @Test
    void cleanupAuditLogs_shouldHandleException_whenRepositoryFails() {
        // Given
        when(auditLogRepository.deleteByCreatedAtBefore(any(LocalDateTime.class))).thenThrow(new RuntimeException("DB error"));

        // When
        databaseCleanupService.cleanupAuditLogs();

        // Then
        verify(auditLogRepository, times(1)).deleteByCreatedAtBefore(any(LocalDateTime.class));
    }

    @Test
    void cleanupAuditLogs_shouldDeleteZeroLogs_whenNoOldLogs() {
        // Given
        int expectedDeletedRecords = 0;
        when(auditLogRepository.deleteByCreatedAtBefore(any(LocalDateTime.class))).thenReturn(expectedDeletedRecords);

        // When
        databaseCleanupService.cleanupAuditLogs();

        // Then
        verify(auditLogRepository, times(1)).deleteByCreatedAtBefore(any(LocalDateTime.class));
    }

    @Test
    void cleanupAppVersions_shouldDeleteOldVersions_whenSuccessful() {
        // Given
        int expectedDeletedRecords = 10;
        when(appVersionRepository.deleteByLastReadBefore(any(OffsetDateTime.class))).thenReturn(expectedDeletedRecords);

        // When
        databaseCleanupService.cleanupAppVersions();

        // Then
        verify(appVersionRepository, times(1)).deleteByLastReadBefore(any(OffsetDateTime.class));
    }

    @Test
    void cleanupAppVersions_shouldHandleException_whenRepositoryFails() {
        // Given
        when(appVersionRepository.deleteByLastReadBefore(any(OffsetDateTime.class))).thenThrow(new RuntimeException("DB error"));

        // When
        databaseCleanupService.cleanupAppVersions();

        // Then
        verify(appVersionRepository, times(1)).deleteByLastReadBefore(any(OffsetDateTime.class));
    }

    @Test
    void cleanupAppVersions_shouldDeleteZeroVersions_whenNoOldVersions() {
        // Given
        int expectedDeletedRecords = 0;
        when(appVersionRepository.deleteByLastReadBefore(any(OffsetDateTime.class))).thenReturn(expectedDeletedRecords);

        // When
        databaseCleanupService.cleanupAppVersions();

        // Then
        verify(appVersionRepository, times(1)).deleteByLastReadBefore(any(OffsetDateTime.class));
    }
}
