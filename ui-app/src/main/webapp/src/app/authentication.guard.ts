import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  RouterStateSnapshot,
  Router
} from '@angular/router';
import { GlobalSettings } from './service/global-settings.service';

@Injectable()
export class AuthenticationGuard implements CanActivate {
  constructor(private settings: GlobalSettings, private router: Router) {}

  public canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (
      this.settings.authentication !== null &&
      this.settings.authentication.looksValid()
    ) {
      return true;
    }
    this.router.navigate(['/login']);
    return false;
  }
}
