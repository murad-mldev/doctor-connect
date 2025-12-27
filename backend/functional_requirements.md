### **Functional Requirements**

#### **Authentication & Authorization**

* Users can register and log in securely.
* Role-based access must be implemented for Admin, Doctor, and Patient.
* Authentication should use Spring Security.
* Admin must verify doctor credentials before activation.

---

#### **Doctor Module**

* Doctors can update their personal profile, including department, specialization, and designation.
* Doctors can set available time slots and define daily patient capacity.
* Doctors can view their assigned patients and each patient’s medical history.
* Doctors can generate or upload prescriptions in an A4 PDF format.
* Doctors can select medicines and tests from a pre-approved and extendable list.
* Doctors can conduct secure one-to-one online consultations via WebRTC or integrated APIs (Zoom/Meet).

---

#### **Patient Module**

* Patients can register, log in, and update their profile information.
* Patients can browse or search for doctors by department, specialization, or name.
* Patients can view doctor schedules and book available appointment slots.
* Patients can upload medical reports (PDFs, images) before consultations.
* Patients can join secure online consultation sessions.
* Patients can view and download their prescriptions and medical history.

---

#### **Admin Module**

* Admins can manage doctors and patients.
* Admins can verify doctor credentials before account activation.
* Admins can manage departments, medicines, and test/report lists.
* Admins can monitor appointments, system activity, and view usage statistics.
* Admins can access audit logs to track key activities such as updates to prescriptions or schedules.

---

#### **Appointment & Scheduling System**

* Appointments should include statuses: PENDING, CONFIRMED, COMPLETED, and CANCELLED.
* Patients can only book appointments if the doctor’s slot capacity has not been exceeded.
* The system should send simulated email or SMS notifications for appointment confirmations, cancellations, and reminders.

---

#### **Prescription & Report Management**

* Doctors and patients can view all previous prescriptions in history.
* Uploaded patient reports must be securely stored and linked to patient records.
* The system automatically generates printable A4 PDF prescriptions containing doctor/patient details, date, medicines, and notes.

---

#### **Payments**

* The system should maintain simple payment records (e.g., Cash or bKash simulation).
* Each doctor can define a consultation fee.

---

#### **Search, Filter & UI Enhancements**

* Patients can filter doctors by specialization, department, or location.
* Pagination and sorting should be implemented for large lists such as doctors or appointments.
* The system should show an availability indicator for doctors (online or next available slot).
* The platform should support English and Bangla language toggle for the user interface.

---