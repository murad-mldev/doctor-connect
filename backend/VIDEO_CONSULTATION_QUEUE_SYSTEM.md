# Video Consultation Queue System - Complete Implementation Guide

## Overview
A comprehensive queue-based video consultation system for doctor-patient appointments with Twilio Video integration, real-time notifications, and intelligent queue management.

---

## Table of Contents
1. [The Problem](#the-problem)
2. [Recommended Solution](#recommended-solution)
3. [System Architecture](#system-architecture)
4. [Implementation Details](#implementation-details)
5. [API Endpoints](#api-endpoints)
6. [Patient Flow](#patient-flow)
7. [Doctor Flow](#doctor-flow)
8. [Additional Features](#additional-features)

---

## The Problem

### Privacy Concern with Google Meet
- Google Meet allows multiple users in same room
- Anyone with link can join
- Not suitable for private doctor-patient consultations
- HIPAA compliance issues

### Queue Management Challenge
```
Scheduled Appointments:
- 2:00 PM - Patient A (takes 20 minutes)
- 2:15 PM - Patient B (waiting)
- 2:30 PM - Patient C (waiting)

Problem: Patient B's appointment is at 2:15 PM, but Patient A's call runs until 2:20 PM.
When should Patient B join?
```

---

## Recommended Solution

### Queue System with Waiting Room + Twilio Video

**Key Features:**
1. ✅ One unique Twilio room per appointment
2. ✅ Secure access tokens for doctor and patient only
3. ✅ Real-time queue with waiting room
4. ✅ WebSocket notifications
5. ✅ Auto-queue management
6. ✅ Average duration tracking
7. ✅ Smart scheduling

---

## System Architecture

### Appointment Status Flow
```
SCHEDULED
    ↓ (Patient joins at scheduled time)
PATIENT_WAITING
    ↓ (Doctor clicks "Start Consultation")
IN_PROGRESS
    ↓ (Doctor clicks "End Consultation")
COMPLETED
    ↓ (Auto-notify next patient)
NEXT_PATIENT_NOTIFIED
```

### Components

#### 1. Database Models

**Appointment (Enhanced)**
```java
- id (UUID)
- doctor (DoctorProfile)
- patient (PatientProfile)
- scheduledTime (Date)
- status (AppointmentStatus)
- queuePosition (Integer)
- patientJoinedAt (Date)
- consultationStartedAt (Date)
- consultationEndedAt (Date)
- actualDurationMinutes (Integer)
- twilioRoomSid (String)
- twilioRoomName (String)
```

**AppointmentQueue (New)**
```java
- id (UUID)
- doctor (DoctorProfile)
- currentAppointment (Appointment)
- queueDate (Date)
- totalWaitingPatients (Integer)
- averageWaitTimeMinutes (Integer)
- createdAt (Date)
- updatedAt (Date)
```

**DoctorConsultationStats (New)**
```java
- id (UUID)
- doctor (DoctorProfile)
- averageConsultationMinutes (Integer)
- totalConsultations (Integer)
- totalConsultationMinutes (Long)
- lastUpdated (Date)
```

#### 2. Services

**TwilioVideoService**
- Create unique room per appointment
- Generate access tokens for doctor/patient
- Complete/end room
- Check room status

**AppointmentQueueService**
- Manage doctor's queue
- Add patient to waiting room
- Get queue position
- Calculate estimated wait time
- Notify next patient
- Auto-timeout handling

**ConsultationAnalyticsService**
- Track consultation duration
- Calculate doctor's average duration
- Smart scheduling suggestions
- Generate reports

**WebSocketNotificationService**
- Real-time queue updates
- "Your turn next" notifications
- "Doctor ready" alerts
- Wait time updates

---

## Implementation Details

### 1. Patient Joins Waiting Room

**Endpoint:** `POST /api/v1/video/join-waiting-room/{appointmentId}`

**Request:**
```json
{
  "joinTime": "2024-01-15T14:15:00Z"
}
```

**Response:**
```json
{
  "appointmentId": "uuid",
  "status": "PATIENT_WAITING",
  "queuePosition": 2,
  "estimatedWaitMinutes": 8,
  "message": "Doctor is currently with another patient. You're 2nd in queue."
}
```

**Backend Logic:**
```java
1. Validate appointment belongs to current user
2. Check appointment time (can join 5 min early)
3. Update status to PATIENT_WAITING
4. Set patientJoinedAt timestamp
5. Add to doctor's queue
6. Calculate queue position
7. Send WebSocket notification to doctor
8. Return queue info to patient
```

---

### 2. Doctor Views Queue

**Endpoint:** `GET /api/v1/video/queue/my-queue`

**Response:**
```json
{
  "currentConsultation": {
    "appointmentId": "uuid",
    "patientName": "John Doe",
    "durationMinutes": 18,
    "status": "IN_PROGRESS"
  },
  "waitingQueue": [
    {
      "appointmentId": "uuid",
      "patientName": "Jane Smith",
      "scheduledTime": "2024-01-15T14:15:00Z",
      "waitingMinutes": 8,
      "queuePosition": 1
    },
    {
      "appointmentId": "uuid",
      "patientName": "Bob Wilson",
      "scheduledTime": "2024-01-15T14:30:00Z",
      "waitingMinutes": 2,
      "queuePosition": 2
    }
  ],
  "totalWaiting": 2,
  "averageConsultationMinutes": 12
}
```

---

### 3. Doctor Starts Consultation

**Endpoint:** `POST /api/v1/video/start-consultation/{appointmentId}`

**Response:**
```json
{
  "appointmentId": "uuid",
  "twilioRoomName": "appointment-uuid-room",
  "doctorAccessToken": "eyJhbGc...",
  "tokenExpiry": "2024-01-15T16:00:00Z",
  "status": "IN_PROGRESS"
}
```

**Backend Logic:**
```java
1. Validate doctor owns this appointment
2. Create Twilio room (unique per appointment)
3. Generate doctor access token (valid 2 hours)
4. Update status to IN_PROGRESS
5. Set consultationStartedAt timestamp
6. Send WebSocket to patient: "Doctor is ready! Join now"
7. Patient receives join link with access token
8. Return doctor's access token
```

---

### 4. Patient Joins Video Call

**Endpoint:** `POST /api/v1/video/join-consultation/{appointmentId}`

**Response:**
```json
{
  "appointmentId": "uuid",
  "twilioRoomName": "appointment-uuid-room",
  "patientAccessToken": "eyJhbGc...",
  "tokenExpiry": "2024-01-15T16:00:00Z",
  "doctorName": "Dr. Smith"
}
```

**Security Checks:**
```java
1. Verify current user is the patient for this appointment
2. Verify appointment status is IN_PROGRESS
3. Verify consultation was started by doctor
4. Generate patient access token (same room as doctor)
5. Return access token
```

---

### 5. Doctor Ends Consultation

**Endpoint:** `POST /api/v1/video/end-consultation/{appointmentId}`

**Response:**
```json
{
  "appointmentId": "uuid",
  "status": "COMPLETED",
  "durationMinutes": 15,
  "nextPatient": {
    "appointmentId": "uuid",
    "patientName": "Jane Smith",
    "notificationSent": true
  }
}
```

**Backend Logic:**
```java
1. End Twilio room
2. Update status to COMPLETED
3. Set consultationEndedAt timestamp
4. Calculate actualDurationMinutes
5. Update doctor's average consultation time
6. Get next patient in queue
7. Send WebSocket: "Doctor is ready! Join now"
8. Send SMS/Email notification to next patient
9. Update queue positions
10. Return next patient info
```

---

## API Endpoints

### Queue Management

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/video/join-waiting-room/{appointmentId}` | Patient joins waiting room | PATIENT |
| GET | `/api/v1/video/queue/my-queue` | Doctor views their queue | DOCTOR |
| GET | `/api/v1/video/queue/my-position/{appointmentId}` | Patient checks queue position | PATIENT |
| POST | `/api/v1/video/start-consultation/{appointmentId}` | Doctor starts consultation | DOCTOR |
| POST | `/api/v1/video/join-consultation/{appointmentId}` | Patient joins video call | PATIENT |
| POST | `/api/v1/video/end-consultation/{appointmentId}` | Doctor ends consultation | DOCTOR |
| GET | `/api/v1/video/queue/waiting-patients` | Get all waiting patients | DOCTOR |
| POST | `/api/v1/video/queue/notify-next` | Manually notify next patient | DOCTOR |

### Analytics & Stats

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/video/analytics/doctor/{doctorId}` | Doctor's consultation stats | DOCTOR/ADMIN |
| GET | `/api/v1/video/analytics/average-duration` | Average consultation duration | DOCTOR |
| GET | `/api/v1/video/analytics/queue-metrics` | Queue performance metrics | ADMIN |

### WebSocket

| Endpoint | Description |
|----------|-------------|
| `/ws/appointments/{appointmentId}` | Real-time appointment updates |
| `/ws/queue/doctor/{doctorId}` | Real-time queue updates for doctor |

---

## Patient Flow

### Complete Patient Journey

```
Step 1: Patient logs in 5 minutes before appointment (2:10 PM for 2:15 PM appointment)
        ↓
Step 2: Clicks "Join Appointment" on appointment card
        ↓
Step 3: POST /api/v1/video/join-waiting-room/{appointmentId}
        → Status: PATIENT_WAITING
        → Response: "Queue position: 2nd | Est. wait: 8 min"
        ↓
Step 4: Patient sees waiting room UI:
        ┌────────────────────────────────────┐
        │  Doctor is with another patient    │
        │                                    │
        │  Your position: 2nd in queue       │
        │  Estimated wait: 8 minutes         │
        │                                    │
        │  ⏱️ You've been waiting: 3 min     │
        │                                    │
        │  [Cancel Appointment]              │
        └────────────────────────────────────┘
        ↓
Step 5: WebSocket Update (Patient becomes 1st in queue)
        → "You're next! Doctor will be with you shortly"
        ↓
Step 6: Doctor ends previous call
        → WebSocket: "Doctor is ready! Click to join"
        → SMS notification
        → Button appears: [Join Video Call Now]
        ↓
Step 7: Patient clicks "Join Video Call Now"
        → POST /api/v1/video/join-consultation/{appointmentId}
        → Receives Twilio access token
        → Video call starts
        ↓
Step 8: Consultation in progress
        → Video interface with Twilio Video SDK
        ↓
Step 9: Doctor ends consultation
        → WebSocket: "Consultation ended"
        → Show: "Please provide feedback"
        → Status: COMPLETED
```

---

## Doctor Flow

### Doctor Dashboard Experience

```
Doctor Dashboard at 2:18 PM:

┌─────────────────────────────────────────────────────┐
│ CURRENT CONSULTATION                                │
├─────────────────────────────────────────────────────┤
│ Patient: John Doe                                   │
│ Duration: 18 minutes                                │
│ Appointment Time: 2:00 PM                           │
│                                                     │
│ [End Consultation]                                  │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ WAITING QUEUE (3 patients waiting)                  │
├─────────────────────────────────────────────────────┤
│ 🟡 Jane Smith                                       │
│    Scheduled: 2:15 PM | Waiting: 8 min             │
│    Condition: Follow-up checkup                     │
│    [Start Consultation] [View Details]             │
├─────────────────────────────────────────────────────┤
│ 🟢 Bob Wilson                                       │
│    Scheduled: 2:30 PM | Waiting: 2 min             │
│    Condition: Cold & Flu symptoms                   │
│    [View Details]                                   │
├─────────────────────────────────────────────────────┤
│ ⚪ Alice Brown                                      │
│    Scheduled: 2:45 PM | Not joined yet             │
│    Condition: Skin rash                             │
│    [View Details]                                   │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ TODAY'S STATS                                       │
├─────────────────────────────────────────────────────┤
│ Completed: 5 | In Progress: 1 | Waiting: 3         │
│ Average Duration: 12 min | Running: 8 min late     │
└─────────────────────────────────────────────────────┘
```

---

## Additional Features

### 1. Average Duration Tracking

**Purpose:** Learn from historical data to improve scheduling

**Implementation:**
```java
@Service
public class ConsultationAnalyticsService {

    public void updateDoctorAverageDuration(UUID doctorId, Integer consultationMinutes) {
        // Find or create stats
        DoctorConsultationStats stats = findOrCreate(doctorId);

        // Update running average
        long totalMinutes = stats.getTotalConsultationMinutes() + consultationMinutes;
        int totalConsultations = stats.getTotalConsultations() + 1;
        int newAverage = (int) (totalMinutes / totalConsultations);

        stats.setTotalConsultationMinutes(totalMinutes);
        stats.setTotalConsultations(totalConsultations);
        stats.setAverageConsultationMinutes(newAverage);
        stats.setLastUpdated(new Date());

        save(stats);
    }

    public Integer getEstimatedDuration(UUID doctorId) {
        // Return doctor's average, or default to 15 min
        return findByDoctorId(doctorId)
            .map(DoctorConsultationStats::getAverageConsultationMinutes)
            .orElse(15);
    }
}
```

**Usage:**
- Show estimated duration when booking appointment
- Calculate buffer time between appointments
- Warn admin if doctor is over-scheduled

---

### 2. Smart Scheduling

**Purpose:** Suggest optimal appointment times based on doctor's patterns

**Algorithm:**
```java
public List<LocalTime> suggestAppointmentTimes(UUID doctorId, LocalDate date) {
    int avgDuration = getAverageDuration(doctorId); // e.g., 12 min
    int bufferTime = 3; // 3 min buffer
    int slotDuration = avgDuration + bufferTime; // 15 min slots

    // Get doctor's schedule (9 AM - 5 PM)
    LocalTime start = LocalTime.of(9, 0);
    LocalTime end = LocalTime.of(17, 0);

    // Generate time slots
    List<LocalTime> availableSlots = new ArrayList<>();
    LocalTime current = start;

    while (current.isBefore(end)) {
        if (!hasAppointment(doctorId, date, current)) {
            availableSlots.add(current);
        }
        current = current.plusMinutes(slotDuration);
    }

    return availableSlots;
}
```

---

### 3. Late Notifications

**Purpose:** Keep patients informed about delays

**Trigger Conditions:**
```java
if (waitTimeMinutes > 10) {
    sendNotification(patient,
        "Your appointment is delayed by " + waitTimeMinutes + " minutes. " +
        "You can reschedule or continue waiting."
    );
}
```

**Notification Templates:**
```
5-10 min late:
"Doctor is running a few minutes late. Estimated wait: 8 minutes."

10-20 min late:
"Doctor is running 15 minutes late. Would you like to reschedule? [Reschedule] [Continue Waiting]"

20+ min late:
"We apologize for the delay. Doctor is running 25 minutes late.
We're offering a 10% discount on your next appointment.
[Reschedule] [Continue Waiting]"
```

---

### 4. Auto-timeout

**Purpose:** Free up queue if patient doesn't respond

**Logic:**
```java
@Scheduled(fixedRate = 60000) // Run every minute
public void checkTimeouts() {
    List<Appointment> notified = findAppointmentsWithStatus(NEXT_PATIENT_NOTIFIED);

    for (Appointment apt : notified) {
        long minutesSinceNotification = getMinutesSince(apt.getNotifiedAt());

        if (minutesSinceNotification > 5) {
            // Patient didn't join within 5 minutes
            apt.setStatus(NO_SHOW);
            save(apt);

            // Notify doctor
            sendToDoctor(apt.getDoctorId(),
                "Patient " + apt.getPatientName() + " did not join. Moving to next patient."
            );

            // Move to next patient
            notifyNextPatient(apt.getDoctorId());
        }
    }
}
```

---

### 5. Reschedule Option

**Endpoint:** `POST /api/v1/appointments/{appointmentId}/reschedule-from-queue`

**Use Case:** Patient in waiting room decides to reschedule

**Request:**
```json
{
  "reason": "LONG_WAIT_TIME",
  "preferredDate": "2024-01-16",
  "preferredTime": "10:00"
}
```

**Response:**
```json
{
  "message": "Appointment rescheduled successfully",
  "newAppointmentId": "uuid",
  "newDateTime": "2024-01-16T10:00:00Z",
  "confirmation": "You'll receive an SMS confirmation"
}
```

---

## Security & Privacy

### Twilio Access Tokens
```java
// Each token is:
- Unique per participant (doctor or patient)
- Time-limited (2 hours)
- Room-specific (can only join assigned room)
- User-specific (contains identity)
- Cannot be reused by others
```

### Participant Validation
```java
public String generateAccessToken(UUID appointmentId, String userId) {
    Appointment apt = findAppointment(appointmentId);

    // Validate user is authorized
    boolean isDoctor = apt.getDoctor().getUser().getId().equals(userId);
    boolean isPatient = apt.getPatient().getUser().getId().equals(userId);

    if (!isDoctor && !isPatient) {
        throw new UnauthorizedException("Not authorized for this consultation");
    }

    String identity = isDoctor ? "doctor-" + userId : "patient-" + userId;
    String roomName = "appointment-" + appointmentId;

    return twilioService.generateToken(identity, roomName);
}
```

### Room Isolation
- Each appointment gets unique Twilio room
- Room name: `appointment-{appointmentId}`
- Only 2 participants allowed (doctor + patient)
- Room auto-closes after consultation
- No recording by default (configurable for compliance)

---

## WebSocket Events

### Patient-Side Events

```javascript
// Patient subscribes to their appointment
ws.subscribe('/appointments/' + appointmentId);

// Event: Queue position updated
{
  "type": "QUEUE_POSITION_UPDATE",
  "queuePosition": 2,
  "estimatedWaitMinutes": 5
}

// Event: You're next
{
  "type": "NEXT_IN_QUEUE",
  "message": "You're next! Doctor will be with you shortly"
}

// Event: Doctor ready
{
  "type": "DOCTOR_READY",
  "message": "Doctor is ready! Click to join",
  "joinUrl": "/video/join/appointment-uuid"
}

// Event: Timeout warning
{
  "type": "TIMEOUT_WARNING",
  "message": "Please join within 2 minutes or you'll be removed from queue",
  "remainingSeconds": 120
}
```

### Doctor-Side Events

```javascript
// Doctor subscribes to their queue
ws.subscribe('/queue/doctor/' + doctorId);

// Event: Patient joined waiting room
{
  "type": "PATIENT_JOINED_QUEUE",
  "appointmentId": "uuid",
  "patientName": "Jane Smith",
  "queuePosition": 3,
  "totalWaiting": 3
}

// Event: Patient left queue
{
  "type": "PATIENT_LEFT_QUEUE",
  "appointmentId": "uuid",
  "reason": "CANCELLED",
  "totalWaiting": 2
}

// Event: Long wait alert
{
  "type": "LONG_WAIT_ALERT",
  "message": "Patient Jane Smith has been waiting 15 minutes",
  "appointmentId": "uuid"
}
```

---

## Performance Considerations

### Database Indexing
```sql
CREATE INDEX idx_appointment_doctor_status ON appointment(doctor_id, status);
CREATE INDEX idx_appointment_queue_position ON appointment(doctor_id, queue_position);
CREATE INDEX idx_appointment_scheduled_time ON appointment(scheduled_time);
```

### Caching Strategy
```java
@Cacheable(value = "doctorQueue", key = "#doctorId")
public List<Appointment> getDoctorQueue(UUID doctorId) {
    // Cache doctor's queue for 30 seconds
}

@CacheEvict(value = "doctorQueue", key = "#doctorId")
public void updateQueue(UUID doctorId) {
    // Invalidate cache when queue changes
}
```

### WebSocket Optimization
- Use topics instead of individual subscriptions
- Batch updates every 5 seconds instead of real-time
- Disconnect idle connections after 1 hour

---

## Testing Strategy

### Unit Tests
```java
@Test
public void testQueuePositionCalculation() {
    // Given 3 waiting patients
    // When patient B cancels
    // Then patient C should move from position 3 to 2
}

@Test
public void testAverageDurationCalculation() {
    // Given doctor has 5 consultations: [10, 12, 15, 8, 20]
    // Then average should be 13 minutes
}
```

### Integration Tests
```java
@Test
public void testPatientToPatientFlow() {
    // 1. Doctor starts consultation with Patient A
    // 2. Patient B joins waiting room
    // 3. Doctor ends consultation with A
    // 4. Verify Patient B receives notification
    // 5. Verify Patient B can join
}
```

### Load Testing
- Simulate 100 concurrent appointments
- Test WebSocket scalability
- Measure database query performance

---

## Deployment Checklist

- [ ] Configure Twilio credentials
- [ ] Set up WebSocket message broker (Redis/RabbitMQ)
- [ ] Create database indexes
- [ ] Configure notification service (SMS/Email)
- [ ] Set up monitoring alerts for long wait times
- [ ] Test with real doctors and patients
- [ ] Configure HIPAA-compliant logging
- [ ] Set up video quality monitoring
- [ ] Create admin dashboard for queue metrics
- [ ] Document API for frontend team
