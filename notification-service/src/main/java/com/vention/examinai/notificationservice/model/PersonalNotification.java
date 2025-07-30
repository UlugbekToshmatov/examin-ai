package com.vention.examinai.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "personal_notification")
public class PersonalNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String type;

    @Column(name = "recipient_id", length = 100, nullable = false)
    private String recipientId;

    @Column(name = "sender_id", length = 100, nullable = false)
    private String senderId;

    @Column(length = 500, nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String message;

    @Column(length = 100, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
