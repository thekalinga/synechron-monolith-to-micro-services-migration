import { Injectable } from '@angular/core';
import { Request, RequestOptionsArgs } from '@angular/http';
import {
  Interceptor,
  InterceptorRequest,
  InterceptorRequestOptionsArgs,
  InterceptorResponseWrapper
} from 'x-ng4-http-interceptor-dontuse';
import { ProgressBarService } from '../service/progress-bar.service';

@Injectable()
export class LoadingBarHttpInterceptor implements Interceptor {
  constructor(private progressBarService: ProgressBarService) {}

  beforeRequest(
    request: InterceptorRequest,
    interceptorStep: number,
    requestNum: number
  ): void {
    console.log(`Requesting start loading for ${requestNum}`);
    this.progressBarService.start(requestNum);
  }

  onResponse(
    responseWrapper: InterceptorResponseWrapper,
    interceptorStep: number,
    requestNum: number
  ): void {
    console.log(`Requesting stop loading on onResponse for ${requestNum}`);
    console.log(
      `Response received from server for request ${this.extractUrlFromResponseWrapper(
        responseWrapper
      )}`
    );
    this.progressBarService.done(requestNum);
  }

  onShortCircuit(
    responseWrapper: InterceptorResponseWrapper,
    interceptorStep: number,
    requestNum: number
  ): void {
    console.log(`Requesting stop loading on onShortCircuit for ${requestNum}`);
    console.log(
      `Short circuit requested by one of the interceptor for request ${this.extractUrlFromResponseWrapper(
        responseWrapper
      )}`
    );
    this.progressBarService.done(requestNum);
  }

  onErr(
    responseWrapper: InterceptorResponseWrapper,
    interceptorStep: number,
    requestNum: number
  ): void {
    console.log(`Requesting stop loading on onErr for ${requestNum}`);
    console.log(
      `Error occurred while processing the request for url ${this.extractUrlFromResponseWrapper(
        responseWrapper
      )}; Error:`,
      responseWrapper.err
    );
    this.progressBarService.done(requestNum);
  }

  onForceCompleteOrForceReturn(
    responseWrapper: InterceptorResponseWrapper,
    interceptorStep: number,
    requestNum: number
  ): void {
    console.log(
      `Requesting stop loading on onForceCompleteOrForceReturn for ${requestNum}`
    );
    console.log(
      `One of the interceptor requested for force completion for request ${this.extractUrlFromResponseWrapper(
        responseWrapper
      )}`
    );
    this.progressBarService.done(requestNum);
  }

  onUnsubscribe(
    interceptorStep: number,
    url: string | Request,
    options: RequestOptionsArgs | InterceptorRequestOptionsArgs,
    requestNum: number
  ) {
    console.log(`Requesting stop loading on onUnsubscribe for ${requestNum}`);
    console.log('Requesting to stop progress bar on unsubscribe');
    this.progressBarService.done(requestNum);
  }

  private extractUrlFromResponseWrapper(
    responseWrapper: InterceptorResponseWrapper
  ): string {
    return responseWrapper.url instanceof Request
      ? (<Request>responseWrapper.url).url
      : responseWrapper.url;
  }
}
