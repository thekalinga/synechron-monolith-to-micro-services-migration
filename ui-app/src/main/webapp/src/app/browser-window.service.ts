import { WindowService } from './window.service';
import { Injectable } from '@angular/core';

@Injectable()
export class BrowserWindowService implements WindowService {
  getComputedStyle(nativeElement: Element): CSSStyleDeclaration {
    return window.getComputedStyle(nativeElement);
  }
}
