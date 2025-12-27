-- First, check which doctor users don't have profiles
SELECT
    u.id as user_id,
    u.full_name,
    u.email,
    CASE
        WHEN dp.id IS NULL THEN 'NO PROFILE - NEEDS CREATION'
        ELSE 'HAS PROFILE'
    END as profile_status
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id
LEFT JOIN doctor_profile dp ON u.id = dp.user_id
WHERE r.name IN ('ROLE_DOCTOR', 'DOCTOR');

-- Create doctor profiles for users who don't have one
-- Replace 'YOUR-USER-ID-HERE' with the actual user ID from the query above
INSERT INTO doctor_profile (id, user_id, license_number, approved, created_at, updated_at)
SELECT
    gen_random_uuid() as id,
    u.id as user_id,
    'LEGACY-LICENSE' as license_number,  -- Placeholder license number
    false as approved,                    -- Requires verification
    NOW() as created_at,
    NOW() as updated_at
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id
LEFT JOIN doctor_profile dp ON u.id = dp.user_id
WHERE r.name IN ('ROLE_DOCTOR', 'DOCTOR')
  AND dp.id IS NULL;  -- Only create for users without profiles

-- Verify the profiles were created
SELECT
    u.id as user_id,
    u.full_name,
    u.email,
    dp.license_number,
    dp.approved,
    dp.created_at
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id
JOIN doctor_profile dp ON u.id = dp.user_id
WHERE r.name IN ('ROLE_DOCTOR', 'DOCTOR')
ORDER BY dp.created_at DESC;
