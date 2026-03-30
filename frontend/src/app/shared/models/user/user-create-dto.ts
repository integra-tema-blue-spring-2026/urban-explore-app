import {User} from './user';

export type UserCreateDto =
  Pick<User, 'email' | 'username'> &
  Partial<Pick<User, 'bio'>> &
  {
  password: string;
  };
