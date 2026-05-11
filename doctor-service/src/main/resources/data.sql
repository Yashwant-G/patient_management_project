-- Create doctor table if it doesn't exist
CREATE TABLE IF NOT EXISTS doctor (
    doctor_id UUID PRIMARY KEY,
    full_name TEXT NOT NULL,
    specialization TEXT NOT NULL,
    email TEXT UNIQUE,
    fees NUMERIC(12,2) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- Create full-text search index for doctor_full_name
CREATE INDEX IF NOT EXISTS doctor_full_name_fts_idx
    ON doctor
    USING GIN (to_tsvector('english', full_name));

-- Insert sample doctors if not already present
INSERT INTO doctor (doctor_id, full_name, specialization, email, fees, start_time, end_time, is_active)
SELECT 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Dr. Emily Carter', 'Cardiology', 'emily.carter@clinic.com', 800.00, '09:00:00', '17:00:00', TRUE
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE doctor_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

INSERT INTO doctor (doctor_id, full_name, specialization, email, fees, start_time, end_time, is_active)
SELECT 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Dr. James Wilson', 'Neurology', 'james.wilson@clinic.com', 1200.00, '10:00:00', '18:00:00', TRUE
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE doctor_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

INSERT INTO doctor (doctor_id, full_name, specialization, email, fees, start_time, end_time, is_active)
SELECT 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Dr. Sarah Patel', 'Orthopedics', 'sarah.patel@clinic.com', 950.00, '09:30:00', '16:30:00', TRUE
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE doctor_id = 'cccccccc-cccc-cccc-cccc-cccccccccccc');

INSERT INTO doctor (doctor_id, full_name, specialization, email, fees, start_time, end_time, is_active)
SELECT 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Dr. Michael Chen', 'Dermatology', 'michael.chen@clinic.com', 600.00, '11:00:00', '19:00:00', TRUE
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE doctor_id = 'dddddddd-dddd-dddd-dddd-dddddddddddd');

INSERT INTO doctor (doctor_id, full_name, specialization, email, fees, start_time, end_time, is_active)
SELECT 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Dr. Laura Nguyen', 'General Practice', 'laura.nguyen@clinic.com', 500.00, '08:30:00', '15:30:00', TRUE
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE doctor_id = 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee');

-- Create slots table (owned by doctor-service)
CREATE TABLE IF NOT EXISTS slots (
    slot_id UUID PRIMARY KEY,
    appointment_id UUID,
    doctor_id UUID NOT NULL,
    slot_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status TEXT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    expires_at TIMESTAMP,
    CONSTRAINT fk_slots_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id),
    CONSTRAINT chk_slot_time CHECK (start_time < end_time)
);

-- Required constraint (as requested)
ALTER TABLE slots DROP CONSTRAINT IF EXISTS uq_doctor_slot_time;
ALTER TABLE slots
    ADD CONSTRAINT uq_doctor_slot_time UNIQUE (doctor_id, slot_date, start_time);

-- Seed a few available slots (future-dated)
INSERT INTO slots (slot_id, appointment_id, doctor_id, slot_date, start_time, end_time, status, version, expires_at)
SELECT
    '99999999-0000-0000-0000-000000000001',
    NULL,
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    '2026-05-20',
    '10:00:00',
    '10:30:00',
    'AVAILABLE',
    0,
    NOW() + INTERVAL '30 minutes'
WHERE NOT EXISTS (SELECT 1 FROM slots WHERE slot_id = '99999999-0000-0000-0000-000000000001');

INSERT INTO slots (slot_id, appointment_id, doctor_id, slot_date, start_time, end_time, status, version, expires_at)
SELECT
    '99999999-0000-0000-0000-000000000002',
    NULL,
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '2026-05-21',
    '14:00:00',
    '14:45:00',
    'AVAILABLE',
    0,
    NOW() + INTERVAL '30 minutes'
WHERE NOT EXISTS (SELECT 1 FROM slots WHERE slot_id = '99999999-0000-0000-0000-000000000002');

