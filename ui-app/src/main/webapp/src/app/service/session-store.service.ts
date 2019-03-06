import { Injectable } from '@angular/core';
import { SessionStorageService } from 'ngx-webstorage';
// import { Locker } from 'angular-safeguard';
//
@Injectable()
export class SessionStore {
  // private locker: Locker;
  //
  // constructor(locker: Locker) {
  //   this.locker = locker.useDriver(Locker.DRIVERS.SESSION);
  // }
  //
  // set<T>(key: string, value: T) {
  //   this.locker.set(key, value);
  // }
  //
  // get<T>(key: string): T {
  //   return this.locker.get(key) as T;
  // }

  constructor(private storage: SessionStorageService) {}

  set<T>(key: string, value: T) {
    this.storage.store(key, value);
  }

  get<T>(key: string): T {
    return this.storage.retrieve(key);
  }

  remove(key: string): void {
    return this.storage.clear(key);
  }
}
