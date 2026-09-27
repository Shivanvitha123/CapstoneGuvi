export const API = {
  AUTH: {
    LOGIN: '/api/auth/login',
    REGISTER: '/api/auth/register',
    ME: '/api/auth/me'
  },
  BUSINESS: '/api/business',
  POLICIES: '/api/policies',
  RISK: '/api/risk',
  CLAIMS: '/api/claims',
  USERS: '/api/auth/users'
} as const;
