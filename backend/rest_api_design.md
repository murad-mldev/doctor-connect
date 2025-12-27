# REST API Design

## Auth & Session

* `POST /api/v1/auth/register`
  Create account (patient or doctor registration request). (Public)

    * Body: `{ name, email, password, role, ... }`
    * Response: `201` created with user summary (not tokens).
* `POST /api/v1/auth/login`
  Exchange credentials → access + refresh tokens. (Public)

    * Body: `{ email, password }`
    * Response: `200` `{ accessToken, refreshToken, expiresIn, user }`
* `POST /api/v1/auth/refresh`
  Refresh access token using refresh token. (Auth)
* `POST /api/v1/auth/logout`
  Revoke tokens / server-side logout. (Auth)

---

## Users / Profiles

* `GET /api/v1/users/me` — get current user profile. (Auth)
* `PATCH /api/v1/users/me` — update profile (partial update). (Auth)
* `GET /api/v1/users/{id}` — get public user profile (doctor profile mostly). (Auth/Some public fields)
* `POST /api/v1/users/{id}/credentials` — upload doctor credentials (multipart). (Auth: DOCTOR)
* `GET /api/v1/doctors` — list/search doctors with filters (department, specialization, name, availability). (Public or Auth)

    * Query: `?department=cardiology&specialization=pediatrics&limit=20&page=1&available=true`
* `GET /api/v1/doctors/{id}` — doctor detailed view (availability summary, ratings, verified flag).

---

## Scheduling & Availability

* `GET /api/v1/doctors/{doctorId}/schedules?date=YYYY-MM-DD` — show day slots and capacities. (Public/Auth)
* `POST /api/v1/doctors/{doctorId}/schedules` — create slot(s). (Auth: DOCTOR)
* `PUT /api/v1/doctors/{doctorId}/schedules/{slotId}` — update slot (Auth: DOCTOR)
* `DELETE /api/v1/doctors/{doctorId}/schedules/{slotId}` — remove slot. (Auth: DOCTOR)
* `GET /api/v1/doctors/{doctorId}/availability` — boolean quick check. (Public/Auth)

---

## Appointments (booking flow)

* `POST /api/v1/appointments` — book an appointment. (Auth: PATIENT)

    * Body: `{ doctorId, slotId, patientId(optional if booking for self), idempotencyKey }`
    * Returns: `201` appointment summary (`status: PENDING/CONFIRMED`)
    * Server enforces capacity & idempotency.
* `GET /api/v1/appointments/{id}` — appointment details. (Auth: Doctor/Patient/Admin as authorized)
* `GET /api/v1/users/{userId}/appointments?status=...&from=...&to=...` — list for user with pagination.
* `PATCH /api/v1/appointments/{id}/status` — change status (confirm/cancel/complete). (Auth: Doctor/Admin)

    * Body: `{ status: "CONFIRMED" }`
* `POST /api/v1/appointments/{id}/cancel` — patient cancellation reason.
* `POST /api/v1/appointments/{id}/reschedule` — suggest new slot. (Auth)

Notes:

* Return clear conflict (`409`) if slot full.
* Publish domain event internally on creation `appointment.created` for Notification module.

---

## Prescriptions

* `POST /api/v1/appointments/{id}/prescriptions` — create prescription for an appointment. (Auth: DOCTOR)

    * Body contains medicines list, tests, notes, follow-up. Include `idempotencyKey`.
    * Response `201` includes `prescriptionId` and PDF generation status/link.
* `GET /api/v1/prescriptions/{id}` — fetch prescription metadata & PDF link. (Auth: Doctor/Patient/Admin)
* `GET /api/v1/patients/{patientId}/prescriptions` — list prescriptions with pagination.

Notes:

* Prescription creation is asynchronous if PDF generation is done in background: return record with `pdfStatus: pending/ready`.

---

## Reports & Files

* `POST /api/v1/patients/{patientId}/reports` — upload report (multipart). (Auth: Patient/Doctor uploading on behalf)

    * Returns `201` with `reportId` and secure access link metadata.
* `GET /api/v1/reports/{id}` — download report (Auth with ownership checks).
* `GET /api/v1/patients/{patientId}/reports` — list uploaded reports.

Notes:

* Enforce max file size & types. Use pre-signed URLs or streaming endpoints for downloads.

---

## Video / Real-time (signaling & session)

* `POST /api/v1/video/rooms` — create room for appointment (Auth: Doctor/Patient).

    * Body: `{ appointmentId }` → returns `roomId`, `joinToken` (short-lived), `iceServers` list.
* `POST /api/v1/video/rooms/{roomId}/token` — issue join token for a participant. (Auth)
* WebSocket endpoint for signaling (separate from REST): `/ws/video`

    * Messages: `join`, `offer`, `answer`, `iceCandidate`, `leave`. Auth via token query param or Authorization header.

Notes:

* Only participants authorized for appointment may create/join room. Room lifecycle tied to appointment.

---

## Notifications & Webhooks

* `POST /api/v1/notifications/test` — trigger sample notification (Auth: Admin).
* Internal events trigger Notification module (no public API required), but optionally expose:
* `POST /api/v1/webhooks/notifications` — (optional) allow external systems to receive events (signed payloads).

---

## Admin

* `GET /api/v1/admin/doctors/pending` — list pending verifications. (Auth: ADMIN)
* `POST /api/v1/admin/doctors/{id}/verify` — verify doctor credentials. (Auth: ADMIN)
* `GET /api/v1/admin/audit-logs?resource=...` — paginated audit logs.
* `GET /api/v1/admin/stats` — system metrics summary (appointments per day, active doctors, etc.).

Notes:

* Admin endpoints restricted by RBAC. Return paginated large lists.

---

## Payments (Optional)

* `POST /api/v1/payments` — create payment intent / record (Auth: Patient)
* `GET /api/v1/payments/{id}` — payment status
* `POST /api/v1/payments/{id}/confirm` — webhook callback from payment gateway to update status.

Notes:

* Keep payment flow idempotent; verify webhooks with signatures.

---

## Search & Misc

* `GET /api/v1/search` — single endpoint for cross-resource search (doctors, specialties). Query: `?q=diabetes&type=doctor&limit=20`.
* `GET /api/v1/meta/departments` — return static/reference data used for filters.

---

## Error & Response model

Standardize responses:

* Success wrapper (optional): `{ data: ..., meta: {...} }`
* Error: `{ error: { code: "APPT_SLOT_FULL", message: "Slot capacity reached", details: [...] } }`

Include `X-Request-Id` header in responses for tracing.

---

## Internal interactions between modules (via REST + events)

* Most modules are internal service calls within monolith (direct method calls). At HLD level, model these as:

    * UI → Appointment API → Appointment Module (validates with User Module and Schedule data)
    * Appointment Module emits event `appointment.created` → Notification Module consumes and sends emails/SMS.
    * Appointment Module requests Video Module to create a room at appointment time → Video Module returns `roomId` and tokens.
    * Prescription Module stores prescription and requests File Module to persist PDF; once ready, notifies Patient via Notification Module.
---