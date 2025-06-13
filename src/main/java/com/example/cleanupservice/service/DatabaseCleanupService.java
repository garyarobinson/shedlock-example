package com.example.cleanupservice.service;

import com.example.cleanupservice.repository.AuditLogRepository;
import com.example.cleanupservice.repository.AppVersionRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
@Slf4j
public class DatabaseCleanupService {

  private final AuditLogRepository auditLogRepository;
  private final AppVersionRepository appVersionRepository;

  @Value("${cleanup.audit-log-retention-days:90}")
  private int auditLogRetentionDays;

  @Value("${cleanup.app-version-retention-days:90}")
  private int appVersionRetentionDays;

  public DatabaseCleanupService(AuditLogRepository auditLogRepository, AppVersionRepository appVersionRepository) {
    this.auditLogRepository = auditLogRepository;
    this.appVersionRepository = appVersionRepository;
  }

  @Scheduled(cron = "0 */1 * * * ?")
  @SchedulerLock(name = "auditLogCleanup", lockAtMostFor = "9m", lockAtLeastFor = "1m")
  @Transactional
  public void cleanupAuditLogs() {
    log.info("Starting Audit Log cleanup - retention period: {} days", auditLogRetentionDays);

    long startTime = System.currentTimeMillis();
    LocalDateTime auditCutoffDate = LocalDateTime.now().minusDays(auditLogRetentionDays);

    int deletedRecords = 0;
    try {
      deletedRecords = auditLogRepository.deleteByCreatedAtBefore(auditCutoffDate);

      long executionTime = System.currentTimeMillis() - startTime;

      log.info("Audit Log cleanup completed. Deleted {} records in {} ms",
              deletedRecords, executionTime);

    } catch (Exception e) {
      log.error("Error during Audit Log cleanup", e);
    } finally {
      log.info("Scheduled Audit Log cleanup task has completed. Total records tidied up: {}", deletedRecords);
    }
  }

  @Scheduled(cron = "0 */2 * * * ?")
  @SchedulerLock(name = "appVersionCleanup", lockAtMostFor = "9m", lockAtLeastFor = "1m")
  @Transactional
  public void cleanupAppVersions() {
    log.info("Starting AppVersion cleanup - retention period: {} days", appVersionRetentionDays);

    long startTime = System.currentTimeMillis();
    OffsetDateTime cutoffDate = OffsetDateTime.now().minusDays(appVersionRetentionDays);

    int deletedRecords = 0;
    try {
      deletedRecords = appVersionRepository.deleteByLastReadBefore(cutoffDate);

      long executionTime = System.currentTimeMillis() - startTime;

      log.info("AppVersion cleanup completed. Deleted {} records in {} ms",
              deletedRecords, executionTime);

    } catch (Exception e) {
      log.error("Error during AppVersion cleanup", e);
    } finally {
      log.info("Scheduled AppVersion cleanup task has completed. Total records tidied up: {}", deletedRecords);
    }
  }
}