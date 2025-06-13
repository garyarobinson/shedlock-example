package com.example.cleanupservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "app_version")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private String organizationId;

    @Column(name = "session_metadata", columnDefinition = "TEXT")
    private String sessionMetadata;

    @Column(name = "environment")
    private String environment;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "agent_reporting_instance_id")
    private String agentReportingInstanceId;

    @Column(name = "application_name", nullable = false)
    private String applicationName;

    @Column(name = "application_id", nullable = false)
    private String applicationId;

    @Column(name = "last_read")
    private OffsetDateTime lastRead;
}

