import {User} from './user';

export type UserRegisterDto =
  Pick<User, 'email' | 'username'> &
  Partial<Pick<User, 'bio'>> &
  {
  password: string;
  };
