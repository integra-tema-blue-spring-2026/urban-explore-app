import {HttpInterceptorFn} from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const isAuthEndpoint =
    req.url.endsWith('api/users/auth/login') ||
    req.url.endsWith('api/users/auth/register');

  if (isAuthEndpoint) {
    return next(req);
  }

  const token = localStorage.getItem('token');

  if (typeof token !== 'string' || token.trim().length === 0) {
    return next(req);
  }

  const authorizedRequest = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });

  return next(authorizedRequest);
};

