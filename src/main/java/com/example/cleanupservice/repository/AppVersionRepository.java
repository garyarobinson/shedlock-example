package com.example.cleanupservice.repository;

import com.example.cleanupservice.entity.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

    int deleteByLastReadBefore(OffsetDateTime cutoffDate);
}

