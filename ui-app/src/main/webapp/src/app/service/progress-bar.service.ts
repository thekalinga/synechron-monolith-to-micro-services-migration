import { NgProgress } from 'ngx-progressbar';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { Injectable, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import 'rxjs/add/operator/debounceTime';

@Injectable()
export class ProgressBarService extends NgProgress {
  private inProgressRequestNumbers: number[] = [];
  private concurrentlySharedBy = 0;
  /**
   * Since progressbar is shared between routing navigation & actual http requests,
   * just throttling dismiss & start requests so that if navigation gets completed quickly followed by start,
   * we will just do start
   * @type {Subject<boolean>}
   */
  private finalAction$: Subject<boolean> = new Subject<boolean>();
  private finalActionSubscription: Subscription;

  constructor() {
    super();
    this.finalActionSubscription = this.finalAction$
      .debounceTime(1000)
      .subscribe(action => {
        console.log('Should start progressbar?', action);
        if (action) {
          if (!super.isStarted()) {
            console.log('Starting progressbar');
            super.start();
          }
        } else {
          if (super.isStarted()) {
            console.log('Stopping loading completely');
            super.done();
          }
        }
      });
    console.log('Subscribed from progresbar events');
  }

  // looks like lifycycle is not applicable to injectable classes
  // ngOnDestroy(): void {
  //   console.log('Unsubscribed from progresbar events');
  //   if (this.finalActionSubscription) {
  //     this.finalActionSubscription.unsubscribe();
  //   }
  // }

  start(requestNum?: number): void {
    if (this.concurrentlySharedBy === 0) {
      this.finalAction$.next(true);
    }
    if (requestNum) {
      this.inProgressRequestNumbers.push(requestNum);
    }
    console.log('Total requests in progress', this.concurrentlySharedBy + 1);
    this.concurrentlySharedBy++;
  }

  done(requestNum?: number): void {
    console.log('Number of concurrent requests', this.concurrentlySharedBy);
    if (this.concurrentlySharedBy > 0) {
      if (requestNum) {
        console.log(
          `requestNum`,
          requestNum,
          'inProgressRequestNumbers',
          this.inProgressRequestNumbers
        );
        const index = this.inProgressRequestNumbers.indexOf(requestNum);
        if (index !== -1) {
          console.log(
            `Not tracking any more stop requests for request#${requestNum}`
          );
          this.inProgressRequestNumbers.splice(index, 1);
          this.concurrentlySharedBy--;
        }
      } else {
        this.concurrentlySharedBy--;
      }

      if (this.concurrentlySharedBy === 0) {
        this.finalAction$.next(false);
      }
    }
  }
}
