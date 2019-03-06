import { Component, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';

import { GlobalSettings } from '../service/global-settings.service';
import { LoginService } from '../service/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class LoginComponent implements OnInit, OnDestroy {
  fg: FormGroup;
  loginInProgress = false;

  private paramMapSubscription: Subscription;
  private loginSubscription: Subscription;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private service: LoginService,
    private settings: GlobalSettings,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.fg = this.fb.group({
      username: [null, Validators.required],
      password: [null, Validators.required]
    });
    // if we are already logged in, lets just redirect to home page
    if (
      this.settings.authentication !== null &&
      this.settings.authentication.looksValid()
    ) {
      this.router.navigate(['']);
    }
  }

  onLogin(): void {
    this.loginInProgress = false;
    this.loginSubscription = this.service
      .login(this.fg.controls.username.value, this.fg.controls.password.value)
      .subscribe(
        _ => {},
        e => {
          this.snackBar.open(
            'Error while logging in. Check login details & retry',
            null,
            {
              duration: 3000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: 'action-failure'
            }
          );
          this.loginInProgress = false;
        },
        () => {
          this.loginInProgress = false;
          this.snackBar.open('Login successful. Taking you to Home page', null, {
            duration: 3000,
            verticalPosition: 'top',
            horizontalPosition: 'center',
            panelClass: 'action-success'
          });
          this.router.navigate(['']);
        }
      );
  }

  ngOnDestroy(): void {
    if (this.loginSubscription != null) {
      this.loginSubscription.unsubscribe();
    }
  }
}
