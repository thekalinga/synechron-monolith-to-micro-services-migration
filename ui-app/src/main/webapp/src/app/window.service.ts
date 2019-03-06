import {InjectionToken} from '@angular/core';

export const WINDOW_SERVICE: InjectionToken<WindowService> = new InjectionToken<WindowService>('window-service');

export interface WindowService {
  getComputedStyle(nativeElement: Element): CSSStyleDeclaration;
}
