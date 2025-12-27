# Video Consultation Queue System - Implementation Completed ✅

## Overview
Successfully implemented a complete FIFO (First-In-First-Out) queue-based video consultation system with Twilio Video integration for secure 1-on-1 doctor-patient consultations.

---

## ✅ What Has Been Implemented

### 1. Database Models & Repositories

#### **Appointment Model Enhanced** (`Appointment.java`)
Added queue and video consultation fields:
- `queuePosition` - Patient's position in FIFO queue
- `patientJoinedAt` - When patient joined waiting room
- `consultationStartedAt` - When consultation began
- `consultationEndedAt` - When consultation ended
- `actualDurationMinutes` - Actual consultation duration
- `twilioRoomSid` - Twilio Video room ID
- `twilioRoomName` - Unique room identifier
- `notifiedAt` - When patient was notified

#### **AppointmentStatus Enum Enhanced** (`AppointmentStatus.java`)
New statuses:
- `PATIENT_WAITING` - Patient in waiting room
- `IN_PROGRESS` - Active consultation
- `NEXT_PATIENT_NOTIFIED` - Patient notified but hasn't joined
- `NO_SHOW` - Patient didn't join after notification

#### **AppointmentQueue Model** (`AppointmentQueue.java`)
Tracks doctor's queue state:
- Current appointment
- Total waiting patients
- Average wait time

#### **DoctorConsultationStats Model** (`DoctorConsultationStats.java`)
Tracks consultation analytics:
- Average consultation duration
- Total consultations completed
- Total consultation minutes

#### **Repositories Created**
- `AppointmentQueueRepository`
- `DoctorConsultationStatsRepository`
- Enhanced `AppointmentRepository` with queue methods

---

### 2. Core Services

#### **TwilioVideoService** (`TwilioVideoServiceImpl.java`)
Secure video room management:
- ✅ Creates unique Twilio room per appointment
- ✅ Generates secure access tokens for doctor/patient only
- ✅ Peer-to-peer rooms (max 2 participants)
- ✅ Room completion and status checking
- ✅ 2-hour token validity

**Key Methods:**
```java
createVideoRoom(appointmentId)              // Create unique room
generateAccessToken(appointmentId, userId)  // Generate secure token
completeRoom(roomSid)                       // End video room
isRoomActive(roomSid)                       // Check room status
```

#### **AppointmentQueueService** (`AppointmentQueueServiceImpl.java`)
FIFO queue management:
- ✅ First-In-First-Out ordering based on appointment time
- ✅ Automatic queue position calculation
- ✅ Estimated wait time calculation
- ✅ Next patient notification
- ✅ Queue position updates

**Key Methods:**
```java
joinWaitingRoom(appointmentId, userId)      // Patient joins queue
getDoctorQueue(doctorId)                    // Get doctor's queue
getQueuePosition(appointmentId)             // Get patient's position
getWaitingPatients(doctorId)                // Get all waiting (FIFO order)
getNextPatientInQueue(doctorId)             // Get next patient (FIFO)
updateQueuePositions(doctorId)              // Recalculate positions
calculateEstimatedWaitTime(appointmentId)   // Calculate wait time
notifyNextPatient(doctorId)                 // Notify next patient
```

#### **ConsultationAnalyticsService** (`ConsultationAnalyticsServiceImpl.java`)
Consultation statistics tracking:
- ✅ Updates doctor's average consultation time
- ✅ Tracks total consultations
- ✅ Provides estimated duration for scheduling

**Key Methods:**
```java
updateDoctorAverageDuration(doctorId, minutes)  // Update avg after each call
getEstimatedDuration(doctorId)                  // Get doctor's average
getDoctorStats(doctorId)                        // Get full statistics
```

---

### 3. API Endpoints

#### **VideoConsultationQueueController** (`VideoConsultationQueueController.java`)

**Patient Endpoints:**
```
POST   /api/v1/video-queue/join-waiting-room/{appointmentId}
       - Patient joins waiting room at scheduled time
       - Returns queue position and estimated wait

GET    /api/v1/video-queue/my-position/{appointmentId}
       - Check current queue position
       - Get estimated wait time

POST   /api/v1/video-queue/join-consultation/{appointmentId}
       - Patient joins active consultation
       - Returns Twilio access token
```

**Doctor Endpoints:**
```
GET    /api/v1/video-queue/my-queue
       - View current consultation + waiting queue
       - Returns FIFO-ordered list of waiting patients

GET    /api/v1/video-queue/waiting-patients
       - Get list of all waiting patients

POST   /api/v1/video-queue/start-consultation/{appointmentId}
       - Doctor starts consultation with specific patient
       - Creates Twilio room and returns access token

POST   /api/v1/video-queue/end-consultation/{appointmentId}
       - Doctor ends current consultation
       - Automatically notifies next patient in queue
       - Updates analytics

POST   /api/v1/video-queue/notify-next-patient
       - Manually notify next patient in queue
```

**Analytics Endpoints:**
```
GET    /api/v1/video-queue/analytics/doctor-stats
       - Get doctor's consultation statistics
       - Average duration, total consultations
```

---

### 4. Configuration

#### **application.yml** Updated
Added Twilio Video API configuration:
```yaml
twilio:
  api:
    key: ${TWILIO_API_KEY:your-api-key}
    secret: ${TWILIO_API_SECRET:your-api-secret}
```

---

## 🔄 How It Works (FIFO Flow)

### Patient Flow

```
1. Patient clicks "Join Appointment" at scheduled time (e.g., 2:15 PM)
   ↓
2. POST /api/v1/video-queue/join-waiting-room/{appointmentId}
   - Status: PATIENT_WAITING
   - Queue position calculated based on appointment time (FIFO)
   - Response: "Queue position: 2nd | Est. wait: 8 min"
   ↓
3. Patient waits in virtual waiting room
   - Can poll: GET /api/v1/video-queue/my-position/{appointmentId}
   ↓
4. Doctor ends previous consultation
   - System automatically notifies next patient (FIFO order)
   - Patient status: NEXT_PATIENT_NOTIFIED
   ↓
5. Patient joins: POST /api/v1/video-queue/join-consultation/{appointmentId}
   - Receives Twilio access token
   - Joins video call
   ↓
6. Consultation in progress
   - Status: IN_PROGRESS
   ↓
7. Doctor ends consultation
   - Status: COMPLETED
   - Analytics updated
```

### Doctor Flow

```
1. Doctor opens dashboard
   - GET /api/v1/video-queue/my-queue
   - Sees current patient + FIFO queue of waiting patients
   ↓
2. Doctor starts consultation with first patient in queue
   - POST /api/v1/video-queue/start-consultation/{appointmentId}
   - Creates Twilio room
   - Receives access token
   - Patient automatically notified to join
   ↓
3. Consultation in progress
   - Both doctor and patient in Twilio video room
   ↓
4. Doctor ends consultation
   - POST /api/v1/video-queue/end-consultation/{appointmentId}
   - Room closed
   - Duration calculated and analytics updated
   - Next patient in FIFO queue automatically notified
   ↓
5. Next patient ready to join
   - Cycle repeats
```

---

## 📊 FIFO Queue Logic

### How Queue Positions Are Calculated

1. **Ordering**: All appointments sorted by `appointmentTime` (earliest first)
2. **Position Assignment**:
   - 1st appointment time → Queue position 1
   - 2nd appointment time → Queue position 2
   - 3rd appointment time → Queue position 3

3. **Status Filtering**: Only includes appointments with status:
   - `PATIENT_WAITING`
   - `NEXT_PATIENT_NOTIFIED`
   - `CONFIRMED`

4. **Auto-Update**: Queue positions recalculated when:
   - Patient joins waiting room
   - Consultation ends
   - Patient removed from queue

### Example:
```
Appointments for Dr. Smith on Jan 15, 2024:

10:00 AM - Patient A (IN_PROGRESS)       → Current consultation
10:15 AM - Patient B (PATIENT_WAITING)   → Queue Position 1
10:30 AM - Patient C (PATIENT_WAITING)   → Queue Position 2
10:45 AM - Patient D (CONFIRMED)         → Queue Position 3
11:00 AM - Patient E (PENDING)           → Not in queue yet

When Dr. Smith ends Patient A's consultation:
- Patient B automatically notified (position 1)
- Patient C moves to position 1
- Patient D moves to position 2
```

---

## 🔐 Security Features

### Twilio Video Security
- ✅ Unique room per appointment
- ✅ Access tokens tied to specific user + room
- ✅ Peer-to-peer encryption
- ✅ Max 2 participants enforced
- ✅ 2-hour token expiration
- ✅ Cannot reuse tokens

### Participant Validation
```java
// Example from TwilioVideoService
boolean isDoctor = appointment.getDoctor().getUser().getId().equals(userId);
boolean isPatient = appointment.getPatient().getUser().getId().equals(userId);

if (!isDoctor && !isPatient) {
    throw new UnauthorizedException("Not authorized");
}
```

### Authorization
- Patient can only:
  - Join their own appointments
  - See their own queue position
- Doctor can only:
  - View their own queue
  - Start/end their own consultations

---

## 📈 Analytics & Intelligence

### Average Duration Tracking
After each consultation:
```java
long durationMinutes = (endTime - startTime) / 60000;
analyticsService.updateDoctorAverageDuration(doctorId, durationMinutes);
```

### Estimated Wait Time Calculation
```
estimatedWait = remainingTimeCurrentConsultation
                + (patientsAhead * averageConsultationTime)

Example:
- Current consultation: 5 min remaining
- 2 patients ahead of you
- Doctor's average: 12 min
- Estimated wait: 5 + (2 * 12) = 29 minutes
```

---

## 🚀 Setup Instructions

### 1. Database Migration
Run your application with `spring.jpa.hibernate.ddl-auto=update` to auto-create tables:
- `appointment_queue`
- `doctor_consultation_stats`
- New columns in `appointment` table

### 2. Twilio Setup

**Get Credentials:**
1. Create Twilio account at https://www.twilio.com
2. Get Account SID and Auth Token from console
3. Create API Key and Secret:
   - Go to: Settings → API Keys
   - Click "Create API Key"
   - Save the SID (API Key) and Secret

**Configure Environment:**
```bash
export TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxx
export TWILIO_AUTH_TOKEN=your_auth_token
export TWILIO_API_KEY=SKxxxxxxxxxxxxx
export TWILIO_API_SECRET=your_api_secret
```

Or update `application.yml` directly (not recommended for production).

### 3. Test Twilio Video
```bash
# Start backend
./gradlew bootRun

# Test room creation
curl -X POST http://localhost:8000/api/v1/video-queue/start-consultation/{appointmentId} \
  -H "Authorization: Bearer {doctor-token}"
```

---

## 🧪 Testing Guide

### Manual Testing Flow

**1. Create Appointments (Admin/Patient)**
```bash
# Create 3 appointments for same doctor
POST /api/v1/appointments
{
  "doctorId": "doctor-uuid",
  "appointmentTime": "2024-01-15T10:00:00Z"  # Patient A
}

POST /api/v1/appointments
{
  "doctorId": "doctor-uuid",
  "appointmentTime": "2024-01-15T10:15:00Z"  # Patient B
}

POST /api/v1/appointments
{
  "doctorId": "doctor-uuid",
  "appointmentTime": "2024-01-15T10:30:00Z"  # Patient C
}
```

**2. Patient B Joins Waiting Room**
```bash
POST /api/v1/video-queue/join-waiting-room/{appointmentB-id}
# Response: queuePosition: 2, estimatedWait: 15 min
```

**3. Patient C Joins Waiting Room**
```bash
POST /api/v1/video-queue/join-waiting-room/{appointmentC-id}
# Response: queuePosition: 3, estimatedWait: 27 min
```

**4. Doctor Views Queue**
```bash
GET /api/v1/video-queue/my-queue
# Response:
{
  "currentConsultation": {
    "appointmentId": "A-id",
    "durationMinutes": 8
  },
  "waitingQueue": [
    { "appointmentId": "B-id", "queuePosition": 1 },
    { "appointmentId": "C-id", "queuePosition": 2 }
  ]
}
```

**5. Doctor Ends Current Consultation**
```bash
POST /api/v1/video-queue/end-consultation/{appointmentA-id}
# Response:
{
  "status": "COMPLETED",
  "durationMinutes": 12,
  "nextPatient": {
    "appointmentId": "B-id",
    "notificationSent": true
  }
}
```

**6. Patient B Joins Consultation**
```bash
POST /api/v1/video-queue/join-consultation/{appointmentB-id}
# Response:
{
  "twilioRoomName": "appointment-B-id-room",
  "accessToken": "eyJhbGc..."  # Use this in Twilio Video SDK
}
```

---

## 📝 TODO / Future Enhancements

### WebSocket Integration (Optional - for real-time updates)
```
/ws/appointments/{appointmentId}  - Patient real-time updates
/ws/queue/doctor/{doctorId}       - Doctor queue updates

Events:
- QUEUE_POSITION_UPDATE
- NEXT_IN_QUEUE
- DOCTOR_READY
- TIMEOUT_WARNING
```

**Implementation Note:** WebSocket would require:
- Spring WebSocket configuration
- Message broker (Redis/RabbitMQ)
- WebSocket controller
- Frontend WebSocket client

### Additional Features (from documentation)
- ⏰ Auto-timeout (remove patient if no join within 5 min)
- 📧 Late notifications (notify if wait > 10 min)
- ♻️ Reschedule from queue
- 📊 Queue performance metrics
- 🔔 SMS notifications (already have Twilio)

---

## 📄 API Documentation Summary

### Complete API Reference

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/v1/video-queue/join-waiting-room/{id}` | POST | PATIENT | Join waiting room |
| `/api/v1/video-queue/my-position/{id}` | GET | PATIENT | Check queue position |
| `/api/v1/video-queue/join-consultation/{id}` | POST | PATIENT | Join video call |
| `/api/v1/video-queue/my-queue` | GET | DOCTOR | View queue |
| `/api/v1/video-queue/waiting-patients` | GET | DOCTOR | List waiting patients |
| `/api/v1/video-queue/start-consultation/{id}` | POST | DOCTOR | Start consultation |
| `/api/v1/video-queue/end-consultation/{id}` | POST | DOCTOR | End consultation |
| `/api/v1/video-queue/notify-next-patient` | POST | DOCTOR | Notify next patient |
| `/api/v1/video-queue/analytics/doctor-stats` | GET | DOCTOR | Get statistics |

---

## ✅ Implementation Checklist

- [x] Database models created
- [x] Repositories implemented
- [x] Twilio Video service implemented
- [x] Queue management service (FIFO logic)
- [x] Analytics service
- [x] API endpoints created
- [x] Configuration updated
- [x] Security & authorization
- [x] Documentation created
- [ ] WebSocket (optional - can add later)
- [ ] SMS notifications (TODO markers added)
- [ ] Auto-timeout scheduler (TODO)
- [ ] Frontend integration

---

## 🎉 Summary

The backend implementation is **COMPLETE** for:

✅ **FIFO Queue System** - First appointment gets first service
✅ **Secure Video Calls** - Twilio Video with access tokens
✅ **Automatic Queue Management** - Auto-notify next patient
✅ **Analytics Tracking** - Average duration calculation
✅ **Privacy & Security** - Participant validation, 1-on-1 rooms
✅ **Scalable Architecture** - Service-based design

The system ensures **privacy** (1-on-1 secure calls), **fairness** (FIFO ordering), and **efficiency** (automatic queue management).

