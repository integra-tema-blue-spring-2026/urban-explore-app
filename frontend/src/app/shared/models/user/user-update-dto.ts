import {User} from './user';

export type UserUpdateDto = Partial<Pick<User, 'username' | 'bio' | 'avatarUrl'>>
