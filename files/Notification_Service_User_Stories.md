# Notification Service User Stories

### Story 1: Receive Confirmation Email
**As a** new User,  
**I want to** receive a confirmation email after registration,  
**so that** I can activate my account.  
**Acceptance Criteria**:
- After registration, system sends a confirmation email via RabbitMQ.
- Email contains a unique confirmation link.
- User can click the link to activate their account.
- System logs the email dispatch in PostgresSQL.

### Story 2: Receive Task Notifications
**As an** Intern or Mentor,  
**I want to** receive notifications about task submissions or feedback,  
**so that** I can stay updated on task progress.  
**Acceptance Criteria**:
- System sends notifications (email or in-app) when a task is submitted or feedback is provided.
- Notifications are processed via RabbitMQ based on type (task operation).
- User can view notifications in their dashboard.
- Notifications are stored in PostgresSQL for audit purposes.

### Story 3: Configure Notification Preferences
**As a** User,  
**I want to** configure my notification preferences,  
**so that** I receive notifications via my preferred channels.  
**Acceptance Criteria**:
- User can access a settings page to choose notification types (email, in-app).
- System saves preferences to PostgresSQL.
- Notifications are sent according to user preferences.

### Story 4: Manage Notification Threads
**As an** Administrator,  
**I want to** start, stop, or maintain notification threads,  
**so that** I can control notification delivery.  
**Acceptance Criteria**:
- Administrator can access a control panel to manage RabbitMQ threads.
- System logs thread status changes in PostgresSQL.
- Notifications are paused during maintenance and queued for later delivery.