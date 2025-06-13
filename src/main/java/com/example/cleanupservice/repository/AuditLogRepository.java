package com.example.cleanupservice.repository;

import com.example.cleanupservice.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Delete audit logs older than the specified date
    @Modifying
    @Query("DELETE FROM AuditLog a WHERE a.createdAt < :cutoffDate")
    int deleteByCreatedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

}