-- Create cached_patient table if it doesn't exist
CREATE TABLE IF NOT EXISTS cached_patient
(
    id
    UUID
    PRIMARY
    KEY,
    full_name
    TEXT,
    email
    TEXT,
    updated_at
    TIMESTAMP
);

-- Create appointment table if it doesn't exist
CREATE TABLE IF NOT EXISTS appointment
(
    appointment_id
    UUID
    PRIMARY
    KEY,
    request_id
    UUID
    DEFAULT
    NULL,
    patient_id
    UUID
    NOT
    NULL,
    doctor_id
    UUID
    NOT
    NULL,
    doctor_name
    TEXT,
    appointment_date
    DATE,
    start_time
    TIME
    NOT
    NULL,
    end_time
    TIME
    NOT
    NULL,
    reason
    TEXT
    NOT
    NULL,
    slot_id
    UUID,
    saga_id
    UUID,
    amount
    NUMERIC
(
    12,
    2
),
    payment_method TEXT,
    txn_id TEXT,
    appointment_status TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW
(
),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW
(
),
    version BIGINT DEFAULT 0 NOT NULL
    );

-- Ensure newer columns exist if table was created previously by old seed
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS appointment_date DATE;
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS slot_id UUID;
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS saga_id UUID;
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS amount NUMERIC (12,2);
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS payment_method TEXT;
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS txn_id TEXT;
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS appointment_status TEXT;
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();


-- Insert patient into cache if not already present
INSERT INTO cached_patient (id, full_name, email, updated_at)
SELECT '123e4567-e89b-12d3-a456-426614174000',
       'John Doe',
       'john.doe@example.com',
       '2026-05-19 09:00:00' WHERE NOT EXISTS (
    SELECT 1 FROM cached_patient WHERE id = '123e4567-e89b-12d3-a456-426614174000'
);

-- Insert appointment 1 if not already present (doctor_name matches doctor-service full_name for doctor_id)
INSERT INTO appointment (appointment_id, patient_id, doctor_id, doctor_name,
                         appointment_date, start_time, end_time, reason,
                         slot_id, saga_id, amount, payment_method, txn_id, appointment_status,
                         created_at, updated_at, version)
SELECT '11111111-1111-1111-1111-111111111111',
       '123e4567-e89b-12d3-a456-426614174000',
       'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
       'Dr. Laura Nguyen',
       '2026-05-20',
       '10:00:00',
       '10:30:00',
       'Initial Consultation',
       NULL,
       NULL,
       NULL,
       NULL,
       NULL,
       'PENDING',
       NOW(),
       NOW(),
       0 WHERE NOT EXISTS (
    SELECT 1 FROM appointment WHERE appointment_id = '11111111-1111-1111-1111-111111111111'
);

-- Insert appointment 2 if not already present (doctor_name matches doctor-service full_name for doctor_id)
INSERT INTO appointment (appointment_id, patient_id, doctor_id, doctor_name,
                         appointment_date, start_time, end_time, reason,
                         slot_id, saga_id, amount, payment_method, txn_id, appointment_status,
                         created_at, updated_at, version)
SELECT '22222222-2222-2222-2222-222222222222',
       '123e4567-e89b-12d3-a456-426614174000',
       'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
       'Dr. Emily Carter',
       '2026-05-21',
       '14:00:00',
       '14:45:00',
       'Follow-up Appointment',
       NULL,
       NULL,
       NULL,
       NULL,
       NULL,
       'PENDING',
       NOW(),
       NOW(),
       0 WHERE NOT EXISTS (
    SELECT 1 FROM appointment WHERE appointment_id = '22222222-2222-2222-2222-222222222222'
);

-- Drop the constraint if it exists
ALTER TABLE appointment DROP CONSTRAINT IF EXISTS appointment_unique_patient_start;

-- Re-add the constraint
ALTER TABLE appointment
    ADD CONSTRAINT appointment_unique_patient_start UNIQUE (patient_id, appointment_date, start_time);
