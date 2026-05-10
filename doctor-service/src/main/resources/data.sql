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

