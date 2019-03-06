import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output,
  HostBinding
} from '@angular/core';
import { MatCheckboxChange, MatSnackBar } from '@angular/material';

import { Todo } from '../model/todo';
import { TodoService } from '../service/todo.service';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-todo',
  templateUrl: './todo.component.html',
  styleUrls: ['./todo.component.scss']
})
export class TodoComponent {
  public hoverInProgress: boolean;
  public updateInProgress: boolean;
  private _todo: Todo;

  @HostBinding('style.backgroundColor') backgroundColor = 'inherit';
  @Output() deleted: EventEmitter<void> = new EventEmitter<void>();

  constructor(private service: TodoService, private snackBar: MatSnackBar) {}

  public onCompleteToggle(): void {
    this.updateInProgress = true;
    let request$: Observable<Todo>;
    if (this._todo.completed) {
      request$ = this.service.markAsComplete(this._todo);
    } else {
      request$ = this.service.markAsIncomplete(this._todo);
    }
    request$.subscribe(
      _ => {},
      e => {
        this._todo.completed = !this._todo.completed;
        this.updateInProgress = false;
        this.snackBar.open(
          `Failed to mark todo as ${this._todo.completed ? 'incomplete' : 'complete'}. Please retry`,
          null,
          {
            duration: 3000,
            verticalPosition: 'top',
            horizontalPosition: 'center',
            panelClass: 'action-failure'
          }
        );
        this.updateBackgroundColor();
      },
      () => {
        this.updateInProgress = false;
        this.snackBar.open(`Todo is marked as ${this._todo.completed ? 'complete' : 'incomplete'}`, null, {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: 'action-success'
        });
        this.updateBackgroundColor();
      }
    );
  }

  public onTodoDelete(): void {
    this.updateInProgress = true;
    this.service.delete(this._todo).subscribe(
      _ => {},
      e => {
        this.updateInProgress = false;
        this.snackBar.open('Failed to delete todo. Please retry', null, {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: 'action-failure'
        });
        this.updateBackgroundColor();
      },
      () => {
        this.deleted.emit();
        this.updateInProgress = false;
        this.snackBar.open('Todo delete successfully', null, {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: 'action-success'
        });
        this.updateBackgroundColor();
      }
    );
  }

  public get checkboxTooltip(): string {
    return this._todo.completed
      ? 'Uncheck to mark as incomplete'
      : 'Check to mark as complete';
  }

  @HostListener('click')
  onClick() {
    if (!this.updateInProgress) {
      this._todo.completed = !this._todo.completed;
      this.onCompleteToggle();
    }
  }

  @HostListener('mouseenter')
  onMouseEnter() {
    this.hoverInProgress = true;
    this.updateBackgroundColor();
  }

  @HostListener('mouseleave')
  onMouseLeave() {
    this.hoverInProgress = false;
    this.updateBackgroundColor();
  }

  private updateBackgroundColor(): void {
    if (this.hoverInProgress || this.updateInProgress) {
      this.backgroundColor = 'rgba(0, 0, 0, .1)';
    } else {
      this.backgroundColor = 'inherit';
    }
  }

  @Input()
  public set todo(todo: Todo) {
    this._todo = todo;
  }

  public get todo(): Todo {
    return this._todo;
  }
}
