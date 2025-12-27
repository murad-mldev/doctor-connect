import { HttpInterceptorFn } from '@angular/common/http';

/**
 * HTTP Interceptor that adds credentials (cookies) to all outgoing requests
 * This is required for session-based authentication with cross-origin requests
 */
export const credentialsInterceptor: HttpInterceptorFn = (req, next) => {
  // Clone the request and add withCredentials flag
  const clonedRequest = req.clone({
    withCredentials: true
  });

  return next(clonedRequest);
};
