import { TestBed } from '@angular/core/testing';

import { UserService } from './user.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { UserRole } from '../../shared/models/user/user-role';
import { User } from '../../shared/models/user/user';

describe('UserService', () => {
  let service: UserService;
  let testHttp: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    testHttp = TestBed.inject(HttpTestingController);
    service = TestBed.inject(UserService);
  });

  afterEach(() => {
    testHttp.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get user by id', () => {
    const mockUserId = '123e4567-e89b-12d3-a456-426614174000';
    const mockUser = {
      id: mockUserId,
      name: 'John Doe',
      email: 'test@gmail.com',
      username: 'johndoe',
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com',
      },
    };

    service.getUser(mockUserId).subscribe({
      next: (user) => {
        expect(user).toBeTruthy();
        expect(user.id).toBe(mockUserId);
        expect(user.email).toBe('test@gmail.com');
        expect(user.username).toBe('johndoe');
        expect(user.role).toBe(UserRole.USER);
        expect(user.bio).toEqual({
          header: 'Hello, I am John!',
          body: 'New York',
          footer: 'https://johndoe.com',
        });
      },
    });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('should get the user followers', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockUserId2 = '12345678-e89b-12d3-a456-426614174000';
    const mockUserId3 = '23456789-e89b-12d3-a456-426614174000';
    const mockUserList = [
      {
        id: mockUserId1,
        name: 'John Doe1',
        email: 'tes1t@gmail.com',
        username: 'johndoe1',
        role: UserRole.USER,
        bio: {
          header: 'Hello, I am John!',
          body: 'New York',
          footer: 'https://johndoe.com',
        },
      },
      {
        id: mockUserId2,
        name: 'John Doe2',
        email: 'test2@gmail.com',
        username: 'johndoe2',
        role: UserRole.ADMIN,
        bio: {
          header: 'Hello, I am John!',
          body: 'New York',
          footer: 'https://johndoe.com',
        },
      },
    ];

    service.getUserFollowers(mockUserId3).subscribe({
      next: (users) => {
        expect(users).toBeTruthy();
        expect(users.length).toBe(2);
        expect(users[0].id).toBe(mockUserId1);
        expect(users[1].id).toBe(mockUserId2);
      },
    });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId3}/followers`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUserList);
  });

  it('should get the user following', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockUserId2 = '12345678-e89b-12d3-a456-426614174000';
    const mockUserId3 = '23456789-e89b-12d3-a456-426614174000';
    const mockUserList = [
      {
        id: mockUserId1,
        name: 'John Doe1',
        email: 'tes1t@gmail.com',
        username: 'johndoe1',
        role: UserRole.USER,
        bio: {
          header: 'Hello, I am John!',
          body: 'New York',
          footer: 'https://johndoe.com',
        },
      },
      {
        id: mockUserId2,
        name: 'John Doe2',
        email: 'test2@gmail.com',
        username: 'johndoe2',
        role: UserRole.ADMIN,
        bio: {
          header: 'Hello, I am John!',
          body: 'New York',
          footer: 'https://johndoe.com',
        },
      },
    ];

    service.getUserFollowing(mockUserId3).subscribe({
      next: (users) => {
        expect(users).toBeTruthy();
        expect(users.length).toBe(2);
        expect(users[0].id).toBe(mockUserId1);
        expect(users[1].id).toBe(mockUserId2);
      },
    });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId3}/following`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUserList);
  });

  it('should update the user', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';

    const updatedData: Partial<User> = {
      username: 'John Updated',
    };

    const mockUpdatedUser = {
      id: mockUserId1,
      email: 'email@gmail.com',
      username: updatedData.username,
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com',
      },
    };

    service.updateUser(mockUserId1, updatedData).subscribe({
      next: (user) => {
        expect(user).toBeTruthy();
        expect(user.id).toBe(mockUserId1);
        expect(user.username).toBe(updatedData.username);
      },
    });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId1}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedData);
    req.flush(mockUpdatedUser);
  });

  it('should follow the user', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockFollowerId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockUser = {
      id: mockUserId1,
      name: 'John Doe1',
      email: 'tes1t@gmail.com',
      username: 'johndoe1',
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com',
      },
    };

    service.followUser(mockUserId1, mockFollowerId1).subscribe({
      next: (user) => {
        expect(user).toBeTruthy();
        expect(user.id).toBe(mockUserId1);
      },
    });

    const req = testHttp.expectOne((request) => {
      return (
        request.url === `${service.USER_API_URL}/follow/${mockUserId1}` &&
        request.params.get('followerId') === mockFollowerId1
      );
    });
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({});
    req.flush(mockUser);
  });

  it('should unfollow the user', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockFollowerId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockUser = {
      id: mockUserId1,
      name: 'John Doe1',
      email: 'tes1t@gmail.com',
      username: 'johndoe1',
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com',
      },
    };

    service.unfollowUser(mockUserId1, mockFollowerId1).subscribe({
      next: (user) => {
        expect(user).toBeTruthy();
        expect(user.id).toBe(mockUserId1);
      },
    });

    const req = testHttp.expectOne((request) => {
      return (
        request.url === `${service.USER_API_URL}/unfollow/${mockUserId1}` &&
        request.params.get('followerId') === mockFollowerId1
      );
    });
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({});
    req.flush(mockUser);
  });

  it('should register the user', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockRegisteredUser = {
      email: 'tes1t@gmail.com',
      username: 'johndoe1',
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com',
      },
      password: 'password',
    };

    const mockUser = {
      id: mockUserId1,
      email: 'tes1t@gmail.com',
      username: 'johndoe1',
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com',
      },
    };

    service.registerUser(mockRegisteredUser).subscribe({
      next: (user) => {
        expect(user).toBeTruthy();
        expect(user.id).toBe(mockUserId1);
        expect(user.role).toBe(UserRole.USER);
      },
    });

    const req = testHttp.expectOne(`${service.USER_API_URL}/auth/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRegisteredUser);
    req.flush(mockUser);
  });

  it('should login the user', () => {
    const mockCredentials = {
      username: 'johndoe1',
      password: 'password',
    };
    const mockTokenResponse = {
      token: 'mock-jwt-token',
    };

    service.loginUser(mockCredentials).subscribe({
      next: (response) => {
        expect(response).toBeTruthy();
        expect(response.token).toBe(mockTokenResponse.token);
      },
    });

    const req = testHttp.expectOne(`${service.USER_API_URL}/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockCredentials);
    req.flush(mockTokenResponse);
  });

  it('should delete the user', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockUser = null;

    service.deleteUser(mockUserId1).subscribe({
      next: (response) => {
        expect(response).toBeNull();
      },
    });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId1}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(mockUser);
  });
});
