-- Create cached_patient table if it doesn't exist
CREATE TABLE IF NOT EXISTS cached_patient (
    id UUID PRIMARY KEY,
    full_name TEXT,
    email TEXT,
    updated_at TIMESTAMP
);

-- Create doctor table if it doesn't exist
CREATE TABLE IF NOT EXISTS doctor (
    doctor_id UUID PRIMARY KEY,
    full_name TEXT NOT NULL,
    specialization TEXT NOT NULL,
    email TEXT UNIQUE
);

-- Create full-text search index for doctor_full_name
CREATE INDEX IF NOT EXISTS doctor_full_name_fts_idx
    ON doctor
    USING GIN (to_tsvector('english', full_name));

-- Create appointment table if it doesn't exist
CREATE TABLE IF NOT EXISTS appointment (
    appointment_id UUID PRIMARY KEY,
    patient_id UUID,
    doctor_id UUID REFERENCES doctor(doctor_id),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    reason TEXT,
    version BIGINT DEFAULT 0 NOT NULL
);

-- Add doctor_id column to appointment if it doesn't exist (for existing tables)
ALTER TABLE appointment ADD COLUMN IF NOT EXISTS doctor_id UUID REFERENCES doctor(doctor_id);

-- Insert sample doctors if not already present
INSERT INTO doctor (doctor_id, full_name, specialization, email)
SELECT 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Dr. Emily Carter', 'Cardiology', 'emily.carter@clinic.com'
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE doctor_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

INSERT INTO doctor (doctor_id, full_name, specialization, email)
SELECT 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Dr. James Wilson', 'Neurology', 'james.wilson@clinic.com'
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE doctor_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

INSERT INTO doctor (doctor_id, full_name, specialization, email)
SELECT 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Dr. Sarah Patel', 'Orthopedics', 'sarah.patel@clinic.com'
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE doctor_id = 'cccccccc-cccc-cccc-cccc-cccccccccccc');

INSERT INTO doctor (doctor_id, full_name, specialization, email)
SELECT 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Dr. Michael Chen', 'Dermatology', 'michael.chen@clinic.com'
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE doctor_id = 'dddddddd-dddd-dddd-dddd-dddddddddddd');

INSERT INTO doctor (doctor_id, full_name, specialization, email)
SELECT 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Dr. Laura Nguyen', 'General Practice', 'laura.nguyen@clinic.com'
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE doctor_id = 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee');

-- Insert patient into cache if not already present
INSERT INTO cached_patient (id, full_name, email, updated_at)
SELECT
    '123e4567-e89b-12d3-a456-426614174000',
    'John Doe',
    'john.doe@example.com',
    '2026-05-19 09:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM cached_patient WHERE id = '123e4567-e89b-12d3-a456-426614174000'
);

-- Insert appointment 1 if not already present
INSERT INTO appointment (appointment_id, patient_id, doctor_id, start_time, end_time, reason, version)
SELECT
    '11111111-1111-1111-1111-111111111111',
    '123e4567-e89b-12d3-a456-426614174000',
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    '2026-05-20 10:00:00',
    '2026-05-20 10:30:00',
    'Initial Consultation',
    0
WHERE NOT EXISTS (
    SELECT 1 FROM appointment WHERE appointment_id = '11111111-1111-1111-1111-111111111111'
);

-- Insert appointment 2 if not already present
INSERT INTO appointment (appointment_id, patient_id, doctor_id, start_time, end_time, reason, version)
SELECT
    '22222222-2222-2222-2222-222222222222',
    '123e4567-e89b-12d3-a456-426614174000',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '2026-05-21 14:00:00',
    '2026-05-21 14:45:00',
    'Follow-up Appointment',
    0
WHERE NOT EXISTS (
    SELECT 1 FROM appointment WHERE appointment_id = '22222222-2222-2222-2222-222222222222'
);

-- Drop the constraint if it exists
ALTER TABLE appointment DROP CONSTRAINT IF EXISTS appointment_unique_patient_start;

-- Re-add the constraint
ALTER TABLE appointment ADD CONSTRAINT appointment_unique_patient_start UNIQUE (patient_id, start_time);
