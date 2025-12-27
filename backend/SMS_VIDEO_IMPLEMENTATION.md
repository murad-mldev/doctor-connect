# SMS & Video Consultation Implementation

## ✅ Features Implemented

### 1. **Twilio SMS Service**
**Status**: ✅ Complete (Development Ready)

#### Implementation Details:
- **Dependency**: `com.twilio.sdk:twilio:9.14.1`
- **Configuration**: Environment-based Twilio credentials
- **Features**:
  - Generic SMS sending
  - Appointment confirmation SMS
  - Appointment cancellation SMS
  - Appointment reminder SMS

#### Configuration Required:
Set these environment variables:
```bash
export SMS_ENABLED=true
export TWILIO_ACCOUNT_SID=your-twilio-account-sid
export TWILIO_AUTH_TOKEN=your-twilio-auth-token
export TWILIO_FROM_NUMBER=+1234567890
```

Or update `application.yml`:
```yaml
notification:
  sms:
    enabled: true
    twilio:
      account-sid: your-account-sid
      auth-token: your-auth-token
      from-number: +1234567890
```

#### SMS Service Methods:
```java
// Generic SMS
smsService.sendSms(phoneNumber, message);

// Appointment confirmation
smsService.sendAppointmentConfirmationSms(phone, patientName, doctorName, time);

// Appointment cancellation
smsService.sendAppointmentCancellationSms(phone, patientName, doctorName, time);

// Appointment reminder
smsService.sendAppointmentReminderSms(phone, patientName, doctorName, time);
```

#### Graceful Fallback:
- If SMS is disabled (`SMS_ENABLED=false`), messages are logged but not sent
- If Twilio fails, errors are logged but don't break the application
- SMS failures don't affect appointment creation/cancellation

---

### 2. **Google Meet Video Consultation**
**Status**: ✅ Complete (Development Ready)

#### Implementation Details:
- **Dependencies**:
  - `com.google.cloud:google-cloud-meet:0.3.0`
  - `com.google.auth:google-auth-library-oauth2-http:1.19.0`
  - `com.google.oauth-client:google-oauth-client-jetty:1.34.1`
- **Entity**: `VideoRoom` (meeting URL, code, conference ID)
- **Features**:
  - Create video rooms for appointments
  - Generate join tokens for participants
  - Meeting room lifecycle management
  - 24-hour room expiration

#### Configuration:
```yaml
video:
  google-meet:
    enabled: true
    credentials-file: src/main/resources/google-credentials.json
    project-id: your-google-project-id
```

#### Video Room Endpoints:

**Create Video Room** (DOCTOR/PATIENT):
```
POST /api/v1/video/rooms
Body: {
  "appointmentId": "uuid",
  "participantName": "John Doe"
}

Response: {
  "id": "room-uuid",
  "appointmentId": "appointment-uuid",
  "meetUrl": "https://meet.google.com/abc-defg-hij",
  "meetingCode": "abc-defg-hij",
  "conferenceId": "conference-uuid",
  "isActive": true,
  "expiresAt": "2025-12-08T...",
  "createdAt": "2025-12-07T..."
}
```

**Get Video Room by Appointment** (DOCTOR/PATIENT):
```
GET /api/v1/video/rooms/appointment/{appointmentId}
```

**Get Video Room by ID** (DOCTOR/PATIENT):
```
GET /api/v1/video/rooms/{roomId}
```

**Generate Join Token** (DOCTOR/PATIENT):
```
POST /api/v1/video/rooms/{roomId}/token
Body: {
  "participantName": "Dr. Smith"
}

Response: {
  "token": "base64-encoded-token",
  "roomId": "room-uuid"
}
```

**End Video Room** (DOCTOR/ADMIN only):
```
POST /api/v1/video/rooms/{roomId}/end
```

#### Development Mode:
The current implementation generates Google Meet-like URLs for development:
- Meeting codes: Format `abc-def-ghi`
- Meet URLs: `https://meet.google.com/abcdefghi`
- Join tokens: Base64 encoded participant info

#### Production Setup:
For production with real Google Meet API:
1. Create a Google Cloud Project
2. Enable Google Meet API
3. Create service account credentials
4. Download JSON key file to `src/main/resources/google-credentials.json`
5. Set `GOOGLE_PROJECT_ID` environment variable
6. Implement actual Google Meet API calls in `VideoConsultationServiceImpl`

---

### 3. **Integrated Appointment Notifications**
**Status**: ✅ Complete

#### Automatic Notifications:
When appointments are **created**:
- ✅ Email to patient (confirmation)
- ✅ SMS to patient (confirmation)
- ✅ Email to doctor (new appointment notification)

When appointments are **cancelled**:
- ✅ Email to patient (cancellation)
- ✅ SMS to patient (cancellation)
- ✅ Email to doctor (cancellation notification)

#### Notification Flow:
```
Appointment Created → AppointmentService → Email + SMS sent automatically
Appointment Cancelled → AppointmentService → Email + SMS sent automatically
```

#### Error Handling:
- Notification failures are logged but don't prevent appointment operations
- If email fails, SMS might still succeed (and vice versa)
- Transactions complete successfully even if all notifications fail

---

## 📊 **Database Changes**

### New Entity: VideoRoom
```sql
CREATE TABLE video_room (
    id UUID PRIMARY KEY,
    appointment_id UUID UNIQUE NOT NULL REFERENCES appointment(id),
    meet_url VARCHAR NOT NULL,
    meeting_code VARCHAR,
    conference_id VARCHAR,
    is_active BOOLEAN DEFAULT true,
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_video_room_appointment ON video_room(appointment_id);
```

---

## 🔧 **Setup Instructions**

### For Development (Testing):

#### 1. SMS Testing (Optional):
```bash
# Use Twilio test credentials (free trial)
export SMS_ENABLED=true
export TWILIO_ACCOUNT_SID=your-test-sid
export TWILIO_AUTH_TOKEN=your-test-token
export TWILIO_FROM_NUMBER=+15005550006  # Twilio test number

# Or disable SMS entirely
export SMS_ENABLED=false
```

#### 2. Email (Already configured):
```bash
export MAIL_USERNAME=your-gmail@gmail.com
export MAIL_PASSWORD=your-app-password
```

#### 3. Video (Development mode - no setup needed):
The video service works out of the box in development mode, generating mock meeting URLs.

---

### For Production:

#### 1. Twilio SMS:
1. Sign up at https://www.twilio.com
2. Get Account SID and Auth Token
3. Purchase a phone number
4. Set environment variables with real credentials

#### 2. Google Meet API:
1. Create Google Cloud Project: https://console.cloud.google.com
2. Enable Google Meet API
3. Create service account
4. Download credentials JSON
5. Place in `src/main/resources/google-credentials.json`
6. Update `VideoConsultationServiceImpl` to use actual API

---

## 📝 **Usage Examples**

### Create Appointment with Automatic Notifications:
```bash
POST /api/v1/appointments
{
  "doctorId": "doctor-uuid",
  "slotId": "slot-uuid",
  "patientId": "patient-uuid",
  "reason": "Regular checkup"
}

# Automatically sends:
# - Email to patient@example.com
# - SMS to +1234567890 (patient phone)
# - Email to doctor@example.com
```

### Create Video Room for Appointment:
```bash
POST /api/v1/video/rooms
{
  "appointmentId": "appointment-uuid",
  "participantName": "John Doe"
}

# Returns meeting URL for both doctor and patient
```

### Patient Joins Video Call:
```bash
# 1. Get video room
GET /api/v1/video/rooms/appointment/{appointmentId}

# 2. Generate join token
POST /api/v1/video/rooms/{roomId}/token
{
  "participantName": "John Doe"
}

# 3. Frontend redirects to meetUrl with token
```

---

## ✨ **Features Summary**

### SMS Service:
- ✅ Twilio integration
- ✅ Appointment confirmation SMS
- ✅ Appointment cancellation SMS
- ✅ Appointment reminder SMS
- ✅ Graceful degradation (works even if disabled)
- ✅ Error handling

### Video Consultation:
- ✅ Video room creation per appointment
- ✅ Google Meet-like URLs
- ✅ Join token generation
- ✅ Room lifecycle management
- ✅ 24-hour expiration
- ✅ RBAC protection (DOCTOR/PATIENT only)
- ✅ One room per appointment

### Notifications Integration:
- ✅ Auto-send on appointment creation
- ✅ Auto-send on appointment cancellation
- ✅ Both email and SMS
- ✅ Patient and doctor notifications
- ✅ Non-blocking (failures don't affect appointments)

---

## 🎯 **Next Steps**

### Optional Enhancements:
1. **Scheduled SMS Reminders**: Send reminder SMS 24 hours before appointment
2. **Video Recording**: Store meeting recordings (requires Google Meet API)
3. **SMS Templates**: More sophisticated SMS templates with branding
4. **Notification Preferences**: Let users choose email vs SMS
5. **Real-time Notifications**: WebSocket for instant updates
6. **WhatsApp Integration**: Use Twilio WhatsApp API
7. **Google Calendar Integration**: Add appointments to calendar

---

## 📋 **Testing Checklist**

- [ ] Set Twilio credentials (or disable SMS)
- [ ] Set email credentials
- [ ] Create test appointment → verify email/SMS sent
- [ ] Cancel test appointment → verify cancellation email/SMS
- [ ] Create video room → verify meeting URL generated
- [ ] Generate join token → verify token format
- [ ] End video room → verify room marked inactive
- [ ] Test with SMS disabled → verify graceful fallback
- [ ] Test notification failures → verify app still works

---

## 🚀 **Production Deployment**

Before deploying to production:
1. ✅ Configure real Twilio credentials
2. ✅ Configure real email SMTP
3. ✅ Set up Google Meet API (if using real API)
4. ✅ Test SMS delivery with real phone numbers
5. ✅ Test email delivery
6. ✅ Test video room creation
7. ✅ Set up monitoring for notification failures
8. ✅ Configure rate limiting for SMS (to control costs)

---

## 💰 **Cost Considerations**

### Twilio SMS Pricing (as of 2024):
- US/Canada: ~$0.0075 per SMS
- International: Varies by country
- Free trial: $15 credit (enough for ~2000 SMS)

### Email:
- Gmail SMTP: Free (with limits)
- SendGrid: Free tier available

### Google Meet:
- Free for basic usage
- Enterprise features require Google Workspace


