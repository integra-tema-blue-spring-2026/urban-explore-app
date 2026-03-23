import { TestBed } from '@angular/core/testing';

import { UserService } from './user.service';
import { provideHttpClient} from '@angular/common/http';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {UserRole} from '../../shared/models/user/user-role';
import {User} from '../../shared/models/user/user';


describe('UserService', () => {

  let service: UserService;
  let testHttp : HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers : [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
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
        footer: 'https://johndoe.com'
      }
    };

    service.getUser(mockUserId).subscribe(
      {next : (user) => {
        expect(user).toBeTruthy();
        expect(user.id).toBe(mockUserId);
        expect(user.name).toBe('John Doe');
        expect(user.email).toBe('test@gmail.com');
        expect(user.username).toBe('johndoe');
        expect(user.role).toBe(UserRole.USER);
        expect(user.bio).toEqual({
          header: 'Hello, I am John!',
          body: 'New York',
          footer: 'https://johndoe.com'
        });
        }}
    );

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('should get the user followers', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockUserId2 = '12345678-e89b-12d3-a456-426614174000';
    const mockUserId3 = '23456789-e89b-12d3-a456-426614174000';
    const mockUserList =[{
      id: mockUserId1,
      name: 'John Doe1',
      email: 'tes1t@gmail.com',
      username: 'johndoe1',
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com'
      }
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
          footer: 'https://johndoe.com'
        }
      }] ;

    service.getUserFollowers(mockUserId3).subscribe(
      {
        next: (users) => {
          expect(users).toBeTruthy();
          expect(users.length).toBe(2);
          expect(users[0].id).toBe(mockUserId1);
          expect(users[1].id).toBe(mockUserId2);
        }
      });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId3}/followers`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUserList);
  });

  it('should get the user following', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockUserId2 = '12345678-e89b-12d3-a456-426614174000';
    const mockUserId3 = '23456789-e89b-12d3-a456-426614174000';
    const mockUserList =[{
      id: mockUserId1,
      name: 'John Doe1',
      email: 'tes1t@gmail.com',
      username: 'johndoe1',
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com'
      }
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
          footer: 'https://johndoe.com'
        }
      }] ;

    service.getUserFollowing(mockUserId3).subscribe(
      {
        next: (users) => {
          expect(users).toBeTruthy();
          expect(users.length).toBe(2);
          expect(users[0].id).toBe(mockUserId1);
          expect(users[1].id).toBe(mockUserId2);
        }
      });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId3}/following`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUserList);
  });

  it('should update the user', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';


    const updatedData : Partial<User> = {
      name: 'John Doe Updated',
      email: 'email@gmail.com',
    }

    const mockUpdatedUser = {
      id: mockUserId1,
      name: updatedData.name,
      email: updatedData.email,
      username: 'johndoe1',
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com'
      }
    }

    service.updateUser(mockUserId1,updatedData ).subscribe(
      {
        next: (user) => {
          expect(user).toBeTruthy();
          expect(user.id).toBe(mockUserId1);
          expect(user.name).toBe(updatedData.name);
          expect(user.email).toBe(updatedData.email);
        }
      });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId1}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedData)
    req.flush(mockUpdatedUser);
  });

  it('should follow the user', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockFollowerId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockUser ={
      id: mockUserId1,
      name: 'John Doe1',
      email: 'tes1t@gmail.com',
      username: 'johndoe1',
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com'
      }
    };

    service.followUser(mockUserId1,mockFollowerId1 ).subscribe(
      {
        next: (user) => {
          expect(user).toBeTruthy();
          expect(user.id).toBe(mockUserId1);
        }
      });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId1}/follow`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({
      followerId: mockFollowerId1
    })
    req.flush(mockUser);
  });

  it('should unfollow the user', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockFollowerId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockUser ={
      id: mockUserId1,
      name: 'John Doe1',
      email: 'tes1t@gmail.com',
      username: 'johndoe1',
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com'
      }
    };

    service.unfollowUser(mockUserId1,mockFollowerId1 ).subscribe(
      {
        next: (user) => {
          expect(user).toBeTruthy();
          expect(user.id).toBe(mockUserId1);
        }
      });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId1}/unfollow`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({
      followerId: mockFollowerId1
    })
    req.flush(mockUser);
  });

  it('should create the user', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockCreateUser ={
      name: 'John Doe1',
      email: 'tes1t@gmail.com',
      username: 'johndoe1',
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com'
      }
    };

    const mockUser ={
      id: mockUserId1,
      name: 'John Doe1',
      email: 'tes1t@gmail.com',
      username: 'johndoe1',
      role: UserRole.USER,
      bio: {
        header: 'Hello, I am John!',
        body: 'New York',
        footer: 'https://johndoe.com'
      }
    };

    service.createUser(mockCreateUser, "password" ).subscribe(
      {
        next: (user) => {
          expect(user).toBeTruthy();
          expect(user.id).toBe(mockUserId1);
          expect(user.role).toBe(UserRole.USER);
        }
      });

    const req = testHttp.expectOne(`${service.USER_API_URL}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      ...mockCreateUser,
      password: "password"
    })
    req.flush(mockUser);
  });

  it('should delete the user', () => {
    const mockUserId1 = '123e4567-e89b-12d3-a456-426614174000';
    const mockUser = null;

    service.deleteUser(mockUserId1 ).subscribe(
      {
        next: (response) => {
          expect(response).toBeNull();
        }
      });

    const req = testHttp.expectOne(`${service.USER_API_URL}/${mockUserId1}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(mockUser);
  });

});
