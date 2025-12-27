-- Check if user exists
SELECT id, username, full_name, active, verified FROM users WHERE username = 'murad_user@gmail.com';

-- Check if patient profile exists for this user
SELECT pp.id, pp.user_id FROM patient_profile pp
JOIN users u ON pp.user_id = u.id
WHERE u.username = 'murad_user@gmail.com';
