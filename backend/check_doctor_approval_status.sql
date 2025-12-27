-- Check all doctor profiles and their approval status
SELECT
    dp.id as doctor_profile_id,
    u.full_name,
    u.email,
    dp.license_number,
    dp.specialization,
    dp.approved,
    dp.created_at
FROM doctor_profile dp
INNER JOIN users u ON dp.user_id = u.id
ORDER BY dp.created_at DESC;

-- Count doctors by approval status
SELECT
    approved,
    COUNT(*) as count
FROM doctor_profile
GROUP BY approved;

-- If you need to reset all doctors to unapproved for testing:
-- UPDATE doctor_profile SET approved = false WHERE approved = true;
