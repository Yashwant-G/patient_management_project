CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    saga_id UUID NOT NULL,
    appointment_id UUID NOT NULL,
    patient_name TEXT NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    payment_method TEXT NOT NULL,
    payment_status TEXT NOT NULL,
    txn_id TEXT,
    failure_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO payments (
    saga_id, appointment_id, patient_name, amount, payment_method, payment_status, txn_id, failure_reason, created_at, updated_at
)
SELECT
    '33333333-3333-3333-3333-333333333333',
    '11111111-1111-1111-1111-111111111111',
    'John Doe',
    500.00,
    'UPI',
    'SUCCESS',
    'TXN-DEMO-0001',
    NULL,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM payments WHERE saga_id = '33333333-3333-3333-3333-333333333333'
);

