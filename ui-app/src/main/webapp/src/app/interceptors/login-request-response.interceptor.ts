import { Injectable } from '@angular/core';
import { Headers, Request, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';

import {
  HttpDirect,
  Interceptor,
  InterceptorRequest,
  InterceptorResponseWrapper,
  InterceptorResponseWrapperBuilder,
  InterceptorService,
  RealResponseObservableTransformer
} from 'x-ng4-http-interceptor-dontuse';

import { environment } from '../../environments/environment';
import { Authentication } from '../model/authentication';
import { GlobalSettings } from '../service/global-settings.service';

@Injectable()
export class LoginAwareRequestResponseInterceptor
  implements RealResponseObservableTransformer, Interceptor {
  constructor(private settings: GlobalSettings, private router: Router) {}

  transform(
    response$: Observable<Response>,
    request: InterceptorRequest,
    http: HttpDirect,
    interceptorService: InterceptorService
  ): Observable<Response> {
    return response$.catch(response => {
      if (response.status === 401) {
        // Don't intercept 401 for the login page to avoid infinite loop
        const url: string =
          request.url instanceof Request
            ? (request.url as Request).url
            : (request.url as string);
        if (this.settings.authUrl !== url) {
          const authentication = this.settings.authentication;
          if (authentication && authentication.refreshToken) {
            const body = `refresh_token=${
              authentication.refreshToken
            }&grant_type=refresh_token`;
            const headers = new Headers({
              'Content-Type':
                'application/x-www-form-urlencoded; charset=utf-8',
              Authorization:
                'Basic ' +
                btoa(
                  `${environment.authenticationClientName}:${
                    environment.authenticationClientPassword
                  }`
                )
            });
            const options = new RequestOptions({ headers: headers });
            return http
              .post(this.settings.authUrl, body, options)
              .flatMap(loginResponse => {
                const newAuthentication = Authentication.newInstance(
                  loginResponse.json(),
                  true
                );
                if (newAuthentication && newAuthentication.accessToken) {
                  this.settings.authentication = newAuthentication;
                  const originalHeaders = request.options.headers;
                  originalHeaders.delete('Authorization');
                  originalHeaders.append(
                    'Authorization',
                    'Bearer ' + this.settings.authentication.accessToken
                  );
                  return http.request(request.url, request.options);
                } else {
                  return Observable.create(response);
                }
              })
              .catch(err => {
                console.log('Failed to get new access token. Reason:', err);
                if (err.status === 400) {
                  // Means refresh token is no more valid
                  this.settings.resetAuthentication();
                  // redirect to login page with current navigated url
                  this.router.navigate(['/login']);
                  return Observable.of(
                    InterceptorResponseWrapperBuilder.new(null)
                      .err(err)
                      .forceRequestCompletion(true)
                      .build()
                  );
                }
                return Observable.throw(err);
              });
          }
        }
      }
      return Observable.throw(response);
    });
  }

  onErr(
    responseWrapper: InterceptorResponseWrapper,
    interceptorStep: number
  ): InterceptorResponseWrapper | void {
    if (responseWrapper.err instanceof Response) {
      const response: Response = responseWrapper.err;
      // Don't intercept 401 for the login page to avoid infinite loop
      if (
        response.status === 401 &&
        this.settings.authUrl !== responseWrapper.urlAsStr
      ) {
        // redirect to login page with current navigated url
        this.router.navigate(['/login']);
        // Complete the flow normally as we have redirected the user to login screen
        return InterceptorResponseWrapperBuilder.new(responseWrapper)
          .forceRequestCompletion(true)
          .build();
      }
    }
  }
}
