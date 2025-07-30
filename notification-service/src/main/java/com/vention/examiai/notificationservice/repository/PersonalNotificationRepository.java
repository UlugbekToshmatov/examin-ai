package com.vention.examiai.notificationservice.repository;

import com.vention.examiai.notificationservice.model.PersonalNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalNotificationRepository extends JpaRepository<PersonalNotification, Long> {
}