# Public Appointments Booking System - Implementation Guide

## Overview
Implemented a complete public-facing appointment booking system that allows visitors (non-logged-in users) to browse doctors and view available appointments. When they click to book, they are redirected to login/register and then can complete the booking with payment.

## Backend Changes

### 1. New Public Controller (`PublicController.java`)
**Location:** `backend/src/main/java/med/doctor_connect/controller/PublicController.java`

**Public Endpoints:**
- `GET /api/v1/public/doctors` - Get all approved doctors (with pagination and filters)
  - Query params: `departmentId`, `specialization`, `name`, `page`, `limit`
- `GET /api/v1/public/doctors/{doctorId}` - Get single doctor profile by ID

**Features:**
- No authentication required
- Only returns approved and verified doctors
- Supports search and filtering
- Paginated responses

### 2. Updated Schedule Controller (`ScheduleController.java`)
**New Endpoint:**
- `GET /api/v1/doctors/{doctorId}/schedules/available` - Get only available (not fully booked) slots
  - Query param: `date` (optional, defaults to today)

**Features:**
- Public endpoint (no auth required)
- Filters out fully booked slots
- Returns only slots where `bookedCount < capacity`

### 3. New Service Methods

**ScheduleService:**
- `getAvailableSchedules(UUID doctorId, LocalDate date)` - Returns only slots with availability

**Implementation in ScheduleServiceImpl:**
```java
public List<ScheduleSlotDto> getAvailableSchedules(UUID doctorId, LocalDate date) {
    List<ScheduleSlot> slots = scheduleSlotRepository.findByDoctorIdAndDate(doctorId, date);
    return slots.stream()
            .filter(slot -> slot.getBookedCount() < slot.getCapacity())
            .map(scheduleMapper::toDto)
            .collect(Collectors.toList());
}
```

## Frontend Changes

### 1. New Public Service (`public.service.ts`)
**Location:** `frontend/src/app/services/public.service.ts`

**Methods:**
- `getAvailableDoctors()` - Fetch all available doctors with filters
- `getDoctorById(doctorId)` - Get single doctor details

### 2. Updated Schedule Service (`schedule.service.ts`)
**New Method:**
- `getAvailableSchedules(doctorId, date?)` - Fetch available appointment slots

### 3. New Components

#### Browse Doctors Component
**Location:** `frontend/src/app/pages/public/browse-doctors.component.ts`

**Features:**
- Display all approved doctors in a grid
- Search by doctor name
- Filter by specialization
- Pagination
- Click to view doctor details

**Route:** `/browse-doctors`

#### Doctor Details Component
**Location:** `frontend/src/app/pages/public/doctor-details.component.ts`

**Features:**
- Display doctor profile (name, specialization, qualifications, description)
- Show available appointment slots for selected date
- Date navigation (previous/next day, jump to today)
- Click slot to book appointment
- **Authentication Check:** If user is logged in → redirect to booking page, else → redirect to login

**Route:** `/doctors/:id`

### 4. Updated Routes (`app.routes.ts`)
```typescript
// Public routes (no auth required)
{ path: 'browse-doctors', component: BrowseDoctorsComponent },
{ path: 'doctors/:id', component: DoctorDetailsComponent },
```

## User Flow

### For Visitors (Not Logged In)
1. **Browse Doctors:** Navigate to `/browse-doctors`
2. **Search/Filter:** Use search fields to find doctors by name or specialization
3. **View Details:** Click on a doctor card to see their profile and available slots
4. **Select Slot:** Choose an available appointment slot
5. **Redirect to Login:** Click "Book Now" → redirected to `/auth/login` with return URL
6. **Login/Register:** User logs in or registers
7. **Return to Booking:** After login, redirected back to doctor details page
8. **Book Appointment:** User can now book the appointment (payment flow to be implemented)

### For Logged-In Users
1. **Browse Doctors:** Navigate to `/browse-doctors`
2. **View Details:** Click on doctor to see profile and slots
3. **Book Appointment:** Click "Book Now" → proceed to booking/payment flow directly

## Authentication Logic in Doctor Details Component
```typescript
bookAppointment(slot: ScheduleSlot): void {
  this.authService.getCurrentUser().subscribe({
    next: (user) => {
      // User is logged in - proceed to booking
      this.router.navigate(['/book-appointment'], {
        queryParams: {
          doctorId: this.doctor?.id,
          slotId: slot.id,
          date: slot.date,
          time: `${slot.startTime} - ${slot.endTime}`
        }
      });
    },
    error: () => {
      // User not logged in - redirect to login
      const returnUrl = `/doctors/${this.doctor?.id}`;
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl }
      });
    }
  });
}
```

## Next Steps (To Be Implemented)

### 1. Booking Page Component
Create a dedicated booking page (`/book-appointment`) that:
- Shows selected doctor and slot details
- Displays appointment fee
- Allows patient to enter reason for visit
- Initiates payment process

### 2. Payment Integration
- Integrate Stripe payment for appointment booking
- Create payment form component
- Handle payment success/failure
- Create appointment after successful payment

### 3. Login Page Enhancement
Update login component to:
- Accept `returnUrl` query parameter
- Redirect to return URL after successful login
- Maintain booking context

## API Endpoints Summary

### Public Endpoints (No Auth Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/public/doctors` | Get all approved doctors |
| GET | `/api/v1/public/doctors/{id}` | Get doctor by ID |
| GET | `/api/v1/doctors/{id}/schedules` | Get doctor schedules |
| GET | `/api/v1/doctors/{id}/schedules/available` | Get available slots only |

### Protected Endpoints (Auth Required)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/v1/appointments` | Create appointment | PATIENT |
| GET | `/api/v1/appointments/{id}` | Get appointment | Any |
| PATCH | `/api/v1/appointments/{id}/status` | Update status | DOCTOR, ADMIN |

## Testing

### Test the Public Endpoints
1. **Browse Doctors:**
   ```bash
   curl http://localhost:8000/api/v1/public/doctors
   ```

2. **Get Doctor by ID:**
   ```bash
   curl http://localhost:8000/api/v1/public/doctors/{doctorId}
   ```

3. **Get Available Slots:**
   ```bash
   curl http://localhost:8000/api/v1/doctors/{doctorId}/schedules/available?date=2025-12-28
   ```

### Test the Frontend
1. Navigate to `http://localhost:4200/browse-doctors`
2. Search for doctors
3. Click on a doctor to view details
4. Select a date to see available slots
5. Click "Book Now" to test authentication redirect

## Files Created/Modified

### Backend
- ✅ Created: `controller/PublicController.java`
- ✅ Modified: `controller/ScheduleController.java`
- ✅ Modified: `service/ScheduleService.java`
- ✅ Modified: `service/impl/ScheduleServiceImpl.java`

### Frontend
- ✅ Created: `services/public.service.ts`
- ✅ Modified: `services/schedule.service.ts`
- ✅ Modified: `services/index.ts`
- ✅ Created: `pages/public/browse-doctors.component.ts`
- ✅ Created: `pages/public/browse-doctors.component.html`
- ✅ Created: `pages/public/browse-doctors.component.scss`
- ✅ Created: `pages/public/doctor-details.component.ts`
- ✅ Created: `pages/public/doctor-details.component.html`
- ✅ Created: `pages/public/doctor-details.component.scss`
- ✅ Modified: `app.routes.ts`

## Notes
- All public endpoints are accessible without authentication
- Only approved doctors are shown to visitors
- Slot availability is calculated in real-time based on capacity vs booked count
- Payment integration needs to be added for complete booking flow
- Login redirect maintains the return URL for seamless user experience
