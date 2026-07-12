import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page } from '../models/models';

@Injectable({ providedIn: 'root' })
export class BaseService<T> {
  protected baseUrl = environment.apiUrl;

  constructor(protected http: HttpClient) {}

  protected endpoint(path: string): string {
    return `${this.baseUrl}/${path}`;
  }

  getPage(path: string, page = 0, size = 10, sort = 'id,asc'): Observable<Page<T>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);
    return this.http.get<Page<T>>(this.endpoint(path), { params });
  }

  getById(path: string, id: string): Observable<T> {
    return this.http.get<T>(`${this.endpoint(path)}/${id}`);
  }

  create(path: string, body: Partial<T>): Observable<T> {
    return this.http.post<T>(this.endpoint(path), body);
  }

  update(path: string, id: string, body: Partial<T>): Observable<T> {
    return this.http.put<T>(`${this.endpoint(path)}/${id}`, body);
  }

  delete(path: string, id: string): Observable<void> {
    return this.http.delete<void>(`${this.endpoint(path)}/${id}`);
  }
}
