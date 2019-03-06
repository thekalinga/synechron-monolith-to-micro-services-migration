import { Injectable } from '@angular/core';

import { environment } from '../../environments/environment';
import { Authentication } from '../model/authentication';
import { SessionStore } from './session-store.service';

@Injectable()
export class GlobalSettings {
  private static readonly authenticationKey = 'authentication';
  private _authentication: Authentication;

  constructor(private sessionStore: SessionStore) {}

  public get authentication(): Authentication {
    // If the user refreshes the page, the authentication will not be available. lets repopulate from session storage
    if (!this._authentication) {
      this._authentication = Authentication.newInstance(
        this.sessionStore.get<Authentication>(GlobalSettings.authenticationKey),
        false
      );
    }
    return this._authentication;
  }

  public set authentication(value: Authentication) {
    if (!(value instanceof Authentication)) {
      throw new Error(
        'passed in object must be an Authentication. Use Authentication.newInstance to build one from json object'
      );
    }
    this._authentication = value;
    this.sessionStore.set<Authentication>(
      GlobalSettings.authenticationKey,
      value
    );
  }

  public resetAuthentication(): void {
    this._authentication = null;
    this.sessionStore.set<Authentication>(
      GlobalSettings.authenticationKey,
      null
    );
  }

  public get authUrl(): string {
    return environment.authenticationServerUrl + '/oauth/token';
  }
}
