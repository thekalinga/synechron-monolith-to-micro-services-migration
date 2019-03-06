import { Injectable } from '@angular/core';
import {
  Headers,
  RequestOptions,
  RequestMethod,
  RequestOptionsArgs
} from '@angular/http';
import {
  Interceptor,
  InterceptorRequest,
  InterceptorRequestBuilder
} from 'x-ng4-http-interceptor-dontuse';

import { GlobalSettings } from '../service/global-settings.service';

@Injectable()
export class CommonHeaderAugmentingInterceptor implements Interceptor {
  constructor(private settings: GlobalSettings) {}

  beforeRequest(
    request: InterceptorRequest,
    interceptorStep: number,
    requestNum: number
  ): InterceptorRequest | void {
    let options = request.options;
    if (!options) {
      options = new RequestOptions();
    }
    const requestMethod = this.methodFromRequestOptions(options);
    // Add these headers only if the request is not OPTIONS/HEAD,
    // which might be sent by browser as a preflight request,
    // if the auth server is on a different host when compared to the host where this app is hosted
    if (
      requestMethod !== RequestMethod.Options &&
      requestMethod !== RequestMethod.Head
    ) {
      const headers = options.headers || new Headers();
      const postEnum = RequestMethod.Post;
      if (
        !headers.has('Content-Type') &&
        (requestMethod === RequestMethod.Post ||
          requestMethod === RequestMethod.Put)
      ) {
        headers.append('Content-Type', 'application/json;charset=UTF-8');
      }
      if (!headers.has('Accept')) {
        headers.append('Accept', 'application/json;charset=UTF-8');
      }
      if (!headers.has('Authorization')) {
        headers.append(
          'Authorization',
          'Bearer ' + this.settings.authentication.accessToken
        );
      }
      options.headers = headers;
      return InterceptorRequestBuilder.new(request)
        .options(options)
        .build();
    }
  }

  private methodFromRequestOptions(
    options?: RequestOptionsArgs
  ): RequestMethod {
    if (!options) {
      return RequestMethod.Get;
    }
    if (typeof options.method === 'string') {
      switch (options.method) {
        case 'post':
          return RequestMethod.Get;
        case 'put':
          return RequestMethod.Put;
        case 'delete':
          return RequestMethod.Delete;
        case 'head':
          return RequestMethod.Head;
        case 'patch':
          return RequestMethod.Patch;
        case 'get':
        default:
          return RequestMethod.Get;
      }
    }
    return options.method;
  }
}
