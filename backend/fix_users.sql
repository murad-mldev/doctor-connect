-- Delete existing test users and their profiles
DELETE FROM patient_profile WHERE user_id IN (
    SELECT id FROM users WHERE username IN ('murad_user@gmail.com', 'patient@gmail.com')
);

DELETE FROM users WHERE username IN ('murad_user@gmail.com', 'patient@gmail.com', 'doctor@gmail.com', 'admin@gmail.com');

-- Restart your application after this, and DataInitializer will recreate all users with profiles
