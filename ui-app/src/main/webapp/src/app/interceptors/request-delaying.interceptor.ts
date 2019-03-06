import { Injectable } from '@angular/core';
import { Request, RequestOptionsArgs } from '@angular/http';
import {
  Interceptor,
  InterceptorRequest,
  InterceptorRequestOptionsArgs,
  InterceptorResponseWrapper
} from 'x-ng4-http-interceptor-dontuse';
import { ProgressBarService } from '../service/progress-bar.service';
import { Observable } from 'rxjs/Observable';
import { delay } from 'rxjs/operators';

@Injectable()
export class RequestDelayingInterceptor implements Interceptor {
  constructor(private progressBarService: ProgressBarService) {}

  beforeRequest(
    request: InterceptorRequest,
    interceptorStep: number,
    requestNum: number
  ): Observable<InterceptorRequest> {
    return Observable.of(request).pipe(delay(2000));
  }
}
