import { User } from './user';
import { UserRole } from './user-role';

export type UserRegisterDto = Pick<User, 'email' | 'username'> &
  Partial<Pick<User, 'bio'>> & {
    password: string;
    role: UserRole;
  };
