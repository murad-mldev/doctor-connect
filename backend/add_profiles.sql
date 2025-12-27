-- Add patient profiles for USER and PATIENT role users who don't have one
INSERT INTO patient_profile (id, user_id, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    u.id,
    NOW(),
    NOW()
FROM users u
LEFT JOIN patient_profile pp ON pp.user_id = u.id
WHERE pp.id IS NULL 
  AND u.username IN ('murad_user@gmail.com', 'patient@gmail.com');
