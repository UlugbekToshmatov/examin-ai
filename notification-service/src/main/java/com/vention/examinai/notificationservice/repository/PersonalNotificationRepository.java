package com.vention.examinai.notificationservice.repository;

import com.vention.examinai.notificationservice.model.PersonalNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalNotificationRepository extends JpaRepository<PersonalNotification, Long> {
}