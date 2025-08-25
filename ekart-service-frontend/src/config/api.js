import axios from 'axios';

// API Base URLs
const API_BASE_URL = 'http://localhost:8080'; // API Gateway URL
const KEYCLOAK_URL = 'http://localhost:8090/realms/ekart';

// Service URLs (through API Gateway)
export const API_ENDPOINTS = {
  // User Service
  USERS: `${API_BASE_URL}/api/users`,
  USER_PROFILE: `${API_BASE_URL}/api/users/profile`,
  USER_REGISTER: `${API_BASE_URL}/api/auth/register`,
  USER_LOGIN: `${API_BASE_URL}/api/auth/login`,
  
  // Product Service
  PRODUCTS: `${API_BASE_URL}/api/products`,
  PRODUCT_CATEGORIES: `${API_BASE_URL}/api/products/categories`,
  PRODUCT_SEARCH: `${API_BASE_URL}/api/products/search`,
  
  // Order Service
  ORDERS: `${API_BASE_URL}/api/orders`,
  ORDER_HISTORY: `${API_BASE_URL}/api/orders/history`,
  
  // Payment Service
  PAYMENTS: `${API_BASE_URL}/api/payments`,
  PAYMENT_PROCESS: `${API_BASE_URL}/api/payments/process`,
  
  // Notification Service
  NOTIFICATIONS: `${API_BASE_URL}/api/notifications`,
};

// Axios instance with interceptors
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Enable sending cookies with requests
});

// Request interceptor to add auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Add X-Requested-With header to help with CORS and CSRF
    config.headers['X-Requested-With'] = 'XMLHttpRequest';
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;

// Separate client for authentication requests that bypasses CSRF for now
export const authApiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'X-Requested-With': 'XMLHttpRequest',
  },
  withCredentials: true,
});

// Auth client doesn't need token interceptor as it's used for login/register
authApiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('Auth API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);
