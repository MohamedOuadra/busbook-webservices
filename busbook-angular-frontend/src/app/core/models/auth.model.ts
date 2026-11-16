export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  phoneNumber?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  email: string;
  fullName: string;
  role: 'USER' | 'ADMIN';
}

export interface UserProfile {
  id: number;
  email: string;
  fullName: string;
  phoneNumber?: string;
  role: 'USER' | 'ADMIN';
  createdAt?: string;
}
