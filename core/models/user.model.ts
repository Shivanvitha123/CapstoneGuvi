import { UserRole } from '../constants/roles';

export interface User {
  userId: number;
  name: string;
  email: string;
  role: UserRole;
}
