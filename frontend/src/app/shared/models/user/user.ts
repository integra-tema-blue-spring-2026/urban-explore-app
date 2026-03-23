import {UserBio} from './user-bio';
import {UserRole} from './user-role';

export interface User {
  id: string;
  name: string;
  email: string;
  username: string;
  role: UserRole;
  bio: UserBio;
}

