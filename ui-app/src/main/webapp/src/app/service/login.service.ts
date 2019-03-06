import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';

import { environment } from '../../environments/environment';
import { Authentication } from '../model/authentication';
import { GlobalSettings } from './global-settings.service';

@Injectable()
export class LoginService {
  constructor(private http: Http, private settings: GlobalSettings) {}

  login(username: string, password: string): Observable<Response> {
    const body = `username=${username}&password=${password}&grant_type=password`;
    const headers = new Headers({
      'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8',
      Authorization:
        'Basic ' +
        btoa(
          `${environment.authenticationClientName}:${
            environment.authenticationClientPassword
          }`
        )
    });
    const options = new RequestOptions({ headers: headers });
    return this.http
      .post(this.settings.authUrl, body, options)
      .do(loginResponse => {
        const newAuthentication = Authentication.newInstance(
          loginResponse.json(),
          true
        );
        this.settings.authentication = newAuthentication;
      });
  }
}
