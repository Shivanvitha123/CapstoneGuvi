export const ROLES = {
  ADMIN: 'ADMIN',
  BUSINESS_OWNER: 'BUSINESS_OWNER',
  UNDERWRITER: 'UNDERWRITER',
  CLAIMS_ADJUSTER: 'CLAIMS_ADJUSTER',
  RISK_ENGINEER: 'RISK_ENGINEER'
} as const;

export type UserRole = typeof ROLES[keyof typeof ROLES];

