import {AfterViewChecked, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {Todo} from '../model/todo';
import {TodoService} from '../service/todo.service';
import {TodoComponent} from '../todo/todo.component';
import {MatSnackBar} from '@angular/material';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit, AfterViewChecked {
  fg: FormGroup;

  public todos: Todo[] | null;
  public fetchInProgress = false;
  public fetchSuccess = false;
  public addInProgress = false;
  public addFailed = false;

  private focusResetOnFirstLoad = false;

  @ViewChildren(TodoComponent) todoChildComponents: QueryList<TodoComponent>;
  @ViewChild('newTodo') inputNativeElement: ElementRef;

  constructor(
    public service: TodoService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.fg = this.fb.group({
      newTodo: ['']
    });
    this.fetchInProgress = true;
    this.service.getAll().subscribe(
      todos => {
        this.todos = todos;
      },
      e => {
        this.fetchInProgress = false;
        this.fetchSuccess = false;
      },
      () => {
        this.fetchInProgress = false;
        this.fetchSuccess = true;
      }
    );
  }

  ngAfterViewChecked(): void {
    if (!this.focusResetOnFirstLoad && this.fetchSuccess) {
      setTimeout(() => {
        this.inputNativeElement.nativeElement.focus();
        this.focusResetOnFirstLoad = true;
      }, 0);
    }
  }

  public onRetry(): void {
    this.ngOnInit();
  }

  public onAddTodo(): void {
    this.addInProgress = true;
    this.fg.controls.newTodo.disable();
    this.service.addTodo(this.fg.controls.newTodo.value).subscribe(
      todo => {
        this.todos.unshift(todo);
      },
      e => {
        this.addFailed = true;
        this.addInProgress = false;
        this.fg.controls.newTodo.enable();
        this.inputNativeElement.nativeElement.focus();
        this.snackBar.open('Failed to add Todo. Please retry', null, {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: 'action-failure'
        });
      },
      () => {
        this.fg.controls.newTodo.setValue('');
        this.addFailed = false;
        this.addInProgress = false;
        this.fg.controls.newTodo.enable();
        this.inputNativeElement.nativeElement.focus();
        this.snackBar.open('Todo added successfully', null, {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: 'action-success'
        });
      }
    );
  }

  public onTodoDeleteComplete(index: number): void {
    this.todos.splice(index, 1);
    this.inputNativeElement.nativeElement.focus();
  }

  public todoIdentity(index: number, todo: Todo): number {
    return todo.id;
  }

  public get disableAdd(): boolean {
    return this.fg.controls.newTodo.value === '' || this.addInProgress;
  }
}
