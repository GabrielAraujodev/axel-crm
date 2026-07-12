import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TimelineItem } from '../models/models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TimelineService {

  constructor(private http: HttpClient) {}

  private endpoint(path: string): string {
    return `${environment.apiUrl}/${path}`;
  }

  getTimeline(entityType: string, entityId: string): Observable<TimelineItem[]> {
    return this.http.get<TimelineItem[]>(this.endpoint(`${entityType}/${entityId}/timeline`));
  }

  addNote(entityType: string, entityId: string, content: string): Observable<TimelineItem> {
    return this.http.post<TimelineItem>(this.endpoint(`${entityType}/${entityId}/notes`), { content });
  }

  deleteNote(entityType: string, entityId: string, noteId: string): Observable<void> {
    return this.http.delete<void>(this.endpoint(`${entityType}/${entityId}/notes/${noteId}`));
  }
}
