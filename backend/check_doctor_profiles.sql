-- Check all doctor profiles
SELECT
    dp.id as profile_id,
    dp.user_id,
    u.full_name,
    u.email,
    u.phone,
    dp.license_number,
    dp.specialization,
    dp.approved,
    dp.created_at
FROM doctor_profile dp
LEFT JOIN users u ON dp.user_id = u.id
ORDER BY dp.created_at DESC;

-- Check users with DOCTOR role
SELECT
    u.id as user_id,
    u.full_name,
    u.email,
    u.is_verified,
    r.name as role_name
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id
WHERE r.name = 'ROLE_DOCTOR' OR r.name = 'DOCTOR'
ORDER BY u.created_at DESC;

-- Check if doctor users have profiles
SELECT
    u.id as user_id,
    u.full_name,
    u.email,
    CASE
        WHEN dp.id IS NULL THEN 'NO PROFILE'
        ELSE 'HAS PROFILE'
    END as profile_status,
    dp.approved
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id
LEFT JOIN doctor_profile dp ON u.id = dp.user_id
WHERE r.name IN ('ROLE_DOCTOR', 'DOCTOR')
ORDER BY u.created_at DESC;
