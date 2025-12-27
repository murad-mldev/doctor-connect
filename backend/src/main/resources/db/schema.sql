-- Enable extensions
CREATE EXTENSION IF NOT EXISTS pgcrypto;  -- for gen_random_uuid()

/*
  ENUM types
*/
CREATE TYPE appointment_status AS ENUM ('PENDING','CONFIRMED','COMPLETED','CANCELLED');
CREATE TYPE payment_method AS ENUM ('CASH','BKASH','CARD','OTHER');
CREATE TYPE payment_status AS ENUM ('INITIATED','COMPLETED','FAILED');
CREATE TYPE notification_type AS ENUM ('EMAIL','SMS','SYSTEM');
CREATE TYPE file_type_enum AS ENUM ('REPORT','PRESCRIPTION','CREDENTIAL','OTHER');

-- Core user table (authentication)
CREATE TABLE users (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          email TEXT NOT NULL UNIQUE,
                          password_hash TEXT NOT NULL,
                          role user_role NOT NULL DEFAULT 'PATIENT',
                          full_name TEXT,
                          phone TEXT,
                          is_active BOOLEAN NOT NULL DEFAULT TRUE,
                          is_verified BOOLEAN NOT NULL DEFAULT FALSE,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_email ON users (email);

-- Doctor profile (one-to-one to user when role=DOCTOR)
CREATE TABLE doctor_profile (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                                department_id UUID NULL,
                                specialization TEXT,
                                designation TEXT,
                                qualifications TEXT,
                                license_number TEXT,
                                approved BOOLEAN DEFAULT FALSE,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Patient profile (one-to-one to app_user when role=PATIENT)
CREATE TABLE patient_profile (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                                 date_of_birth DATE,
                                 gender TEXT,
                                 blood_group TEXT,
                                 address TEXT,
                                 emergency_contact TEXT,
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                 updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Department table
CREATE TABLE department (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            name TEXT NOT NULL UNIQUE,
                            description TEXT,
                            created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Medicines / tests catalogs
CREATE TABLE medicine (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name TEXT NOT NULL,
                          dosage_form TEXT,
                          manufacturer TEXT,
                          description TEXT,
                          is_active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_medicine_name ON medicine(lower(name));

CREATE TABLE lab_test (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name TEXT NOT NULL,
                          description TEXT,
                          is_active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_lab_test_name ON lab_test(lower(name));

-- Schedule slots (doctor defines availability)
CREATE TABLE schedule_slot (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               doctor_id UUID NOT NULL REFERENCES doctor_profile(id) ON DELETE CASCADE,
                               date DATE NOT NULL,
                               start_time TIME NOT NULL,
                               end_time TIME NOT NULL,
                               capacity INTEGER NOT NULL DEFAULT 1 CHECK (capacity > 0),
                               booked_count INTEGER NOT NULL DEFAULT 0 CHECK (booked_count >= 0),
                               created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                               updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                               CONSTRAINT uq_doctor_date_time UNIQUE (doctor_id, date, start_time, end_time)
);

CREATE INDEX idx_schedule_doctor_date ON schedule_slot (doctor_id, date);

-- Appointment table (created when patient books a slot)
CREATE TABLE appointment (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             slot_id UUID NOT NULL REFERENCES schedule_slot(id) ON DELETE RESTRICT,
                             doctor_id UUID NOT NULL REFERENCES doctor_profile(id) ON DELETE CASCADE,
                             patient_id UUID NOT NULL REFERENCES patient_profile(id) ON DELETE CASCADE,
                             status appointment_status NOT NULL DEFAULT 'PENDING',
                             appointment_time TIMESTAMPTZ NOT NULL, -- exact scheduled time (derived from slot)
                             reason TEXT,
                             fee NUMERIC(12,2) DEFAULT 0,
                             created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                             updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                             UNIQUE (slot_id, patient_id)  -- avoid duplicate bookings by same patient for same slot
);

CREATE INDEX idx_appointment_doctor_date ON appointment (doctor_id, appointment_time);
CREATE INDEX idx_appointment_patient ON appointment (patient_id);

-- Payment record
CREATE TABLE payment_record (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                appointment_id UUID NOT NULL UNIQUE REFERENCES appointment(id) ON DELETE CASCADE,
                                amount NUMERIC(12,2) NOT NULL,
                                method payment_method,
                                status payment_status DEFAULT 'INITIATED',
                                provider_reference TEXT,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- File storage / reports / prescription pdfs / doctor credentials
CREATE TABLE file_store (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            owner_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
                            related_entity_type TEXT,   -- e.g., 'report','prescription','credential'
                            related_entity_id UUID,     -- optional
                            file_key TEXT NOT NULL,     -- storage key/path (S3 key or local path)
                            file_name TEXT,
                            file_type file_type_enum NOT NULL DEFAULT 'OTHER',
                            size_bytes BIGINT,
                            checksum TEXT,
                            uploaded_by UUID REFERENCES users(id) ON DELETE SET NULL,
                            uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_file_store_owner ON file_store (owner_user_id);
CREATE INDEX idx_file_store_entity ON file_store (related_entity_type, related_entity_id);

-- Prescription and details
CREATE TABLE prescription (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              appointment_id UUID NOT NULL UNIQUE REFERENCES appointment(id) ON DELETE CASCADE,
                              doctor_id UUID NOT NULL REFERENCES doctor_profile(id) ON DELETE CASCADE,
                              patient_id UUID NOT NULL REFERENCES patient_profile(id) ON DELETE CASCADE,
                              notes TEXT,
                              pdf_file_id UUID REFERENCES file_store(id) ON DELETE SET NULL,
                              created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                              updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE prescription_medicine (
                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       prescription_id UUID NOT NULL REFERENCES prescription(id) ON DELETE CASCADE,
                                       medicine_id UUID REFERENCES medicine(id) ON DELETE SET NULL,
                                       name TEXT NOT NULL,          -- denormalized name copy
                                       dosage TEXT,
                                       duration TEXT,
                                       instructions TEXT
);
CREATE INDEX idx_pres_med_prescription ON prescription_medicine (prescription_id);

CREATE TABLE prescription_test (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   prescription_id UUID NOT NULL REFERENCES prescription(id) ON DELETE CASCADE,
                                   test_id UUID REFERENCES lab_test(id) ON DELETE SET NULL,
                                   name TEXT NOT NULL,
                                   instructions TEXT
);
CREATE INDEX idx_pres_test_prescription ON prescription_test (prescription_id);

-- Medical record (EHR-like)
CREATE TABLE medical_record (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                patient_id UUID NOT NULL REFERENCES patient_profile(id) ON DELETE CASCADE,
                                doctor_id UUID REFERENCES doctor_profile(id) ON DELETE SET NULL,
                                title TEXT,
                                diagnosis TEXT,
                                treatment TEXT,
                                attachments JSONB, -- list of file_store ids or metadata
                                record_date DATE NOT NULL DEFAULT now(),
                                created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_medical_record_patient ON medical_record (patient_id);

-- Notifications
CREATE TABLE notification (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                              type notification_type NOT NULL,
                              title TEXT,
                              body TEXT,
                              metadata JSONB,
                              is_read BOOLEAN DEFAULT FALSE,
                              created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_notification_user ON notification (user_id);

-- Audit log
CREATE TABLE audit_log (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           actor_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
                           action TEXT NOT NULL,
                           entity_type TEXT,
                           entity_id UUID,
                           change_summary JSONB,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_audit_actor ON audit_log (actor_user_id);

-- Refresh tokens (if you handle refresh tokens server-side)
CREATE TABLE refresh_token (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               token_hash TEXT NOT NULL, -- hashed refresh token
                               expires_at TIMESTAMPTZ NOT NULL,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                               revoked BOOLEAN DEFAULT FALSE
);
CREATE INDEX idx_refresh_user ON refresh_token (user_id);

-- Optional: doctor credential uploads (references file_store)
CREATE TABLE doctor_credential (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   doctor_id UUID NOT NULL REFERENCES doctor_profile(id) ON DELETE CASCADE,
                                   file_id UUID NOT NULL REFERENCES file_store(id) ON DELETE CASCADE,
                                   uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                   verified BOOLEAN DEFAULT FALSE,
                                   verified_at TIMESTAMPTZ
);
CREATE INDEX idx_doctor_cred_doctor ON doctor_credential (doctor_id);

------------------------------------------------------
-- Trigger + function to enforce capacity and update booked_count
-- Behavior:
--  - Before INSERT on appointment: check slot capacity.
--  - On INSERT: increment schedule_slot.booked_count.
--  - On DELETE: decrement schedule_slot.booked_count.
--  - On status change to CANCELLED: decrement booked_count (if previously counted).
------------------------------------------------------

-- We'll implement a conservative approach: require booking process to use SELECT ... FOR UPDATE on the slot row
-- but also have DB trigger as last-line safety.

CREATE OR REPLACE FUNCTION fn_appointment_before_insert()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
slot_capacity INTEGER;
    slot_booked INTEGER;
BEGIN
    -- lock schedule_slot row to avoid race (FOR UPDATE)
    PERFORM 1 FROM schedule_slot WHERE id = NEW.slot_id FOR UPDATE;

SELECT capacity, booked_count INTO slot_capacity, slot_booked
FROM schedule_slot WHERE id = NEW.slot_id;

IF slot_capacity IS NULL THEN
        RAISE EXCEPTION 'Schedule slot not found';
END IF;

    IF slot_booked >= slot_capacity THEN
        RAISE EXCEPTION 'Slot capacity reached';
END IF;

    -- increment booked_count
UPDATE schedule_slot
SET booked_count = booked_count + 1, updated_at = now()
WHERE id = NEW.slot_id;

RETURN NEW;
END;
$$;

CREATE TRIGGER trg_appointment_before_insert
    BEFORE INSERT ON appointment
    FOR EACH ROW
    EXECUTE FUNCTION fn_appointment_before_insert();

-- On appointment delete, decrement booked_count
CREATE OR REPLACE FUNCTION fn_appointment_after_delete()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
UPDATE schedule_slot
SET booked_count = GREATEST(booked_count - 1, 0), updated_at = now()
WHERE id = OLD.slot_id;
RETURN OLD;
END;
$$;

CREATE TRIGGER trg_appointment_after_delete
    AFTER DELETE ON appointment
    FOR EACH ROW
    EXECUTE FUNCTION fn_appointment_after_delete();

-- On status update to CANCELLED from non-CANCELLED, decrement booked_count
CREATE OR REPLACE FUNCTION fn_appointment_before_update()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF TG_OP = 'UPDATE' THEN
        IF NEW.status = 'CANCELLED' AND OLD.status <> 'CANCELLED' THEN
UPDATE schedule_slot
SET booked_count = GREATEST(booked_count - 1, 0), updated_at = now()
WHERE id = NEW.slot_id;
ELSIF OLD.status = 'CANCELLED' AND NEW.status <> 'CANCELLED' THEN
            -- Re-booking a previously cancelled appointment (rare) -> increment
UPDATE schedule_slot
SET booked_count = booked_count + 1, updated_at = now()
WHERE id = NEW.slot_id;
END IF;
END IF;
RETURN NEW;
END;
$$;

CREATE TRIGGER trg_appointment_before_update
    BEFORE UPDATE ON appointment
    FOR EACH ROW
    EXECUTE FUNCTION fn_appointment_before_update();

------------------------------------------------------
-- Useful views and helper queries
------------------------------------------------------

-- View: doctor schedules with remaining seats
CREATE OR REPLACE VIEW v_schedule_availability AS
SELECT
    s.*,
    (s.capacity - s.booked_count) AS remaining_seats
FROM schedule_slot s;

-- Example: quick lookup to check if patient already booked a slot
CREATE INDEX idx_appointment_slot_patient ON appointment (slot_id, patient_id);

------------------------------------------------------
-- Example constraints / checks
------------------------------------------------------
-- Ensure appointment's doctor matches slot's doctor (defensive)
CREATE OR REPLACE FUNCTION fn_appointment_doctor_slot_check()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
slot_doc UUID;
BEGIN
SELECT doctor_id INTO slot_doc FROM schedule_slot WHERE id = NEW.slot_id;
IF slot_doc IS NULL THEN
        RAISE EXCEPTION 'Invalid slot';
END IF;
    IF slot_doc <> NEW.doctor_id THEN
        RAISE EXCEPTION 'Appointment doctor_id must match slot doctor_id';
END IF;
RETURN NEW;
END;
$$;

CREATE TRIGGER trg_appointment_doc_slot_check
    BEFORE INSERT OR UPDATE ON appointment
                         FOR EACH ROW
                         EXECUTE FUNCTION fn_appointment_doctor_slot_check();

-- Keep updated_at columns in sync via triggers (optional)
CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at = now();
RETURN NEW;
END;
$$;

-- Attach updated_at trigger to tables that have updated_at
CREATE TRIGGER trg_updated_at_users
    BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_updated_at_doctor_profile
    BEFORE UPDATE ON doctor_profile FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_updated_at_patient_profile
    BEFORE UPDATE ON patient_profile FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_updated_at_schedule_slot
    BEFORE UPDATE ON schedule_slot FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_updated_at_appointment
    BEFORE UPDATE ON appointment FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_updated_at_payment
    BEFORE UPDATE ON payment_record FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

-- End of schema
