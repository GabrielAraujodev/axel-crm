import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { Lead, LeadNote, TimelineItem, Task, User, Page } from '../models/models';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LeadDetailService extends BaseService<Lead> {

  constructor(http: HttpClient) {
    super(http);
  }

  getTimeline(leadId: string): Observable<TimelineItem[]> {
    return this.http.get<TimelineItem[]>(this.endpoint(`leads/${leadId}/timeline`));
  }

  getNotes(leadId: string): Observable<LeadNote[]> {
    return this.http.get<LeadNote[]>(this.endpoint(`leads/${leadId}/notes`));
  }

  createNote(leadId: string, note: Partial<LeadNote>): Observable<LeadNote> {
    return this.http.post<LeadNote>(this.endpoint(`leads/${leadId}/notes`), note);
  }

  deleteNote(leadId: string, noteId: string): Observable<void> {
    return this.http.delete<void>(this.endpoint(`leads/${leadId}/notes/${noteId}`));
  }

  convertLead(leadId: string): Observable<any> {
    return this.http.post<any>(this.endpoint(`leads/${leadId}/convert`), {});
  }

  getUsers(): Observable<Page<User>> {
    return this.http.get<Page<User>>(this.endpoint('users?size=1000'));
  }

  getTasks(): Observable<Page<Task>> {
    return this.http.get<Page<Task>>(this.endpoint('tasks?size=1000'));
  }

  createTask(task: Partial<Task>): Observable<Task> {
    return this.http.post<Task>(this.endpoint('tasks'), task);
  }
}
