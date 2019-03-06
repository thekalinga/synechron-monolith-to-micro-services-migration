import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { environment } from '../../environments/environment';
import { Authentication } from '../model/authentication';
import { GlobalSettings } from './global-settings.service';
import { Todo } from '../model/todo';

@Injectable()
export class TodoService {
  constructor(private http: Http) {}

  public getAll(): Observable<Todo[]> {
    return this.http.get('/api/todos').map(response => response.json() as Todo[]);
  }

  public addTodo(name: string): Observable<Todo> {
    return this.http
      .post('/api/todos', {
        name: name
      })
      .map(response => response.json() as Todo);
  }

  public markAsComplete(todo: Todo): Observable<Todo> {
    return this.http
      .post(`/api/todos/${todo.id}/markAsComplete`, {})
      .map(response => response.json() as Todo);
  }

  public markAsIncomplete(todo: Todo): Observable<Todo> {
    return this.http
      .post(`/api/todos/${todo.id}/markAsIncomplete`, {})
      .map(response => response.json() as Todo);
  }

  public delete(todo: Todo): Observable<Todo> {
    return this.http
      .delete(`/api/todos/${todo.id}`)
      .map(response => response.json() as Todo);
  }
}
