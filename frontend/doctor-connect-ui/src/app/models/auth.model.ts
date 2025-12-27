// Auth request for login (matching backend AuthRequest)
export interface AuthRequest {
  emailOrPhoneNumber: string;
  password: string;
}

// Alias for LoginRequest (same as AuthRequest)
export interface LoginRequest {
  emailOrPhoneNumber: string;
  password: string;
}

// Register request (matching backend UserDto structure for registration)
export interface RegisterRequest {
  emailOrPhoneNumber: string;
  password: string;
  fullName?: string;
  phone?: string;
  email?: string;
  isActive?: boolean;
  isVerified?: boolean;
  roles?: any[];
  licenseNumber?: string; // For doctor registration
}

// Auth response (matching backend AuthResponse - no token, session-based)
export interface AuthResponse {
  message: string;
  success: boolean;
}

// Admin login request (matching backend AdminLoginRequest)
export interface AdminLoginRequest {
  username: string;
  password: string;
}
