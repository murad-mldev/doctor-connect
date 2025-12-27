# Doctor Features Analysis

## Backend API Endpoints

### 1. DoctorController (`/api/v1/doctors`)

- ✅ `GET /api/v1/doctors` - Search doctors (IMPLEMENTED in doctor.service.ts)
- ✅ `GET /api/v1/doctors/{id}` - Get doctor by ID (IMPLEMENTED in doctor.service.ts)
- ✅ `GET /api/v1/doctors/{id}/availability` - Check availability (IMPLEMENTED in doctor.service.ts)

### 2. DoctorProfileController (`/api/v1/doctor-profile`)

- ❌ `GET /api/v1/doctor-profile/me` - Get current doctor profile (MISSING)
- ❌ `GET /api/v1/doctor-profile/{userId}` - Get doctor profile by user ID (MISSING)
- ❌ `PATCH /api/v1/doctor-profile/me` - Update current doctor profile (MISSING)
- ❌ `PATCH /api/v1/doctor-profile/{userId}` - Update doctor profile (MISSING)

**Stripe Connect Endpoints** (MISSING):

- ❌ `POST /api/v1/doctor-profile/stripe/create-account` - Create Stripe account
- ❌ `POST /api/v1/doctor-profile/stripe/onboarding-link` - Get onboarding link
- ❌ `GET /api/v1/doctor-profile/stripe/dashboard-link` - Get dashboard link
- ❌ `GET /api/v1/doctor-profile/stripe/account-status` - Get account status
- ❌ `POST /api/v1/doctor-profile/stripe/refresh-status` - Refresh account status

### 3. ScheduleController (`/api/v1/doctors/{doctorId}/schedules`)

- ✅ `GET /api/v1/doctors/{doctorId}/schedules` - Get schedules (IMPLEMENTED)
- ✅ `POST /api/v1/doctors/{doctorId}/schedules` - Create schedule (IMPLEMENTED)
- ✅ `PUT /api/v1/doctors/{doctorId}/schedules/{slotId}` - Update schedule (IMPLEMENTED)
- ✅ `DELETE /api/v1/doctors/{doctorId}/schedules/{slotId}` - Delete schedule (IMPLEMENTED)

### 4. AppointmentController (`/api/v1/appointments`, `/api/v1/users/{userId}/appointments`)

- ✅ `POST /api/v1/appointments` - Create appointment (IMPLEMENTED)
- ✅ `GET /api/v1/appointments/{id}` - Get appointment by ID (IMPLEMENTED)
- ✅ `GET /api/v1/users/{userId}/appointments` - Get user appointments (IMPLEMENTED)
- ✅ `PATCH /api/v1/appointments/{id}/status` - Update status (IMPLEMENTED)
- ✅ `POST /api/v1/appointments/{id}/cancel` - Cancel appointment (IMPLEMENTED)
- ✅ `POST /api/v1/appointments/{id}/reschedule` - Reschedule (IMPLEMENTED)

### 5. PrescriptionController (`/api/v1/appointments/{id}/prescriptions`, `/api/v1/prescriptions`)

- ✅ `POST /api/v1/appointments/{id}/prescriptions` - Create prescription (IMPLEMENTED)
- ✅ `GET /api/v1/prescriptions/{id}` - Get prescription by ID (IMPLEMENTED)
- ✅ `GET /api/v1/patients/{patientId}/prescriptions` - Get patient prescriptions (IMPLEMENTED)

## Missing Implementations

### Services to Create:

1. **DoctorProfileService** - Handle doctor profile and Stripe Connect operations

### Components to Implement:

1. **DoctorAppointmentsComponent** - List and manage appointments
2. **DoctorScheduleComponent** - Manage availability schedule
3. **DoctorProfileComponent** - Edit profile and Stripe Connect setup

