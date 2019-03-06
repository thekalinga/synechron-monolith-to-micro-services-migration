import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Http, HttpModule, RequestOptions, XHRBackend } from '@angular/http';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgProgress, NgProgressModule } from 'ngx-progressbar';
import { Ng2Webstorage } from 'ngx-webstorage';
import { InterceptorService } from 'x-ng4-http-interceptor-dontuse';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthenticationGuard } from './authentication.guard';
import { BrowserWindowService } from './browser-window.service';
import { HomeComponent } from './home/home.component';
import { CommonHeaderAugmentingInterceptor } from './interceptors/common-header-augmenting.interceptor';
import { LoadingBarHttpInterceptor } from './interceptors/loading-bar-http.interceptor';
import { LoginAwareRequestResponseInterceptor } from './interceptors/login-request-response.interceptor';
import { LoginComponent } from './login/login.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { GlobalSettings } from './service/global-settings.service';
import { LoginService } from './service/login.service';
import { TodoService } from './service/todo.service';
import { ProgressBarService } from './service/progress-bar.service';
import { SessionStore } from './service/session-store.service';
import { TodoComponent } from './todo/todo.component';
import { MaterialModule } from './vendor/material.module';
import { WINDOW_SERVICE } from './window.service';
import { RequestDelayingInterceptor } from './interceptors/request-delaying.interceptor';

export function interceptorFactory(
  xhrBackend: XHRBackend,
  requestOptions: RequestOptions,
  loadingBarHttpInterceptor: LoadingBarHttpInterceptor,
  commonHeaderAugmentingInterceptor: CommonHeaderAugmentingInterceptor,
  requestDelayingInterceptor: RequestDelayingInterceptor,
  loginAwareRequestResponseInterceptor: LoginAwareRequestResponseInterceptor
) {
  const service = new InterceptorService(xhrBackend, requestOptions);
  service.addInterceptor(loadingBarHttpInterceptor);
  service.addInterceptor(commonHeaderAugmentingInterceptor);
  service.addInterceptor(requestDelayingInterceptor);
  service.addInterceptor(loginAwareRequestResponseInterceptor);
  service.realResponseObservableTransformer = loginAwareRequestResponseInterceptor;
  return service;
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    NotFoundComponent,
    TodoComponent
  ],
  imports: [
    Ng2Webstorage.forRoot({
      prefix: 'app',
      separator: '.',
      caseSensitive: true
    }),
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    NgProgressModule,
    MaterialModule,
    FlexLayoutModule,
    AppRoutingModule
  ],
  providers: [
    ProgressBarService,
    {
      provide: NgProgress,
      useExisting: ProgressBarService
    },
    LoadingBarHttpInterceptor,
    CommonHeaderAugmentingInterceptor,
    RequestDelayingInterceptor,
    LoginAwareRequestResponseInterceptor,
    {
      provide: InterceptorService,
      useFactory: interceptorFactory,
      deps: [
        XHRBackend,
        RequestOptions,
        LoadingBarHttpInterceptor,
        CommonHeaderAugmentingInterceptor,
        RequestDelayingInterceptor,
        LoginAwareRequestResponseInterceptor
      ]
    },
    {
      provide: Http,
      useExisting: InterceptorService
    },
    SessionStore,
    GlobalSettings,
    AuthenticationGuard,
    {
      provide: WINDOW_SERVICE,
      useClass: BrowserWindowService
    },
    LoginService,
    TodoService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
