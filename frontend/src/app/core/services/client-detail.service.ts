import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from './base.service';
import { Client, ClientNote, ClientAttachment, TimelineItem, Deal, Task, Proposal, Project, Page } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ClientDetailService extends BaseService<Client> {
  constructor(http: HttpClient) {
    super(http);
  }

  getTimeline(clientId: string): Observable<TimelineItem[]> {
    return this.http.get<TimelineItem[]>(this.endpoint(`clients/${clientId}/timeline`));
  }

  addNote(clientId: string, content: string): Observable<ClientNote> {
    return this.http.post<ClientNote>(this.endpoint(`clients/${clientId}/notes`), { content });
  }

  deleteNote(clientId: string, noteId: string): Observable<void> {
    return this.http.delete<void>(this.endpoint(`clients/${clientId}/notes/${noteId}`));
  }

  getAttachments(clientId: string): Observable<ClientAttachment[]> {
    return this.http.get<ClientAttachment[]>(this.endpoint(`clients/${clientId}/attachments`));
  }

  uploadAttachment(clientId: string, file: File): Observable<ClientAttachment> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ClientAttachment>(this.endpoint(`clients/${clientId}/attachments`), formData);
  }

  downloadAttachment(clientId: string, attachmentId: string): Observable<Blob> {
    return this.http.get(this.endpoint(`clients/${clientId}/attachments/${attachmentId}/download`), {
      responseType: 'blob'
    });
  }

  deleteAttachment(clientId: string, attachmentId: string): Observable<void> {
    return this.http.delete<void>(this.endpoint(`clients/${clientId}/attachments/${attachmentId}`));
  }

  getDeals(): Observable<Page<Deal>> {
    return this.http.get<Page<Deal>>(this.endpoint('deals?size=1000'));
  }

  getTasks(): Observable<Page<Task>> {
    return this.http.get<Page<Task>>(this.endpoint('tasks?size=1000'));
  }

  createDeal(deal: Partial<Deal>): Observable<Deal> {
    return this.http.post<Deal>(this.endpoint('deals'), deal);
  }

  createTask(task: Partial<Task>): Observable<Task> {
    return this.http.post<Task>(this.endpoint('tasks'), task);
  }

  getPipelines(): Observable<Page<any>> {
    return this.http.get<Page<any>>(this.endpoint('pipelines?size=1000'));
  }

  getPipelineStages(pipelineId: string): Observable<any[]> {
    return this.http.get<any[]>(this.endpoint(`pipelines/${pipelineId}/stages`));
  }

  getUsers(): Observable<Page<any>> {
    return this.http.get<Page<any>>(this.endpoint('users?size=1000'));
  }

  getProposals(): Observable<Page<Proposal>> {
    return this.http.get<Page<Proposal>>(this.endpoint('proposals?size=1000'));
  }

  getProjects(): Observable<Page<Project>> {
    return this.http.get<Page<Project>>(this.endpoint('projects?size=1000'));
  }

  createProposal(proposal: Partial<Proposal>): Observable<Proposal> {
    return this.http.post<Proposal>(this.endpoint('proposals'), proposal);
  }

  createProject(project: Partial<Project>): Observable<Project> {
    return this.http.post<Project>(this.endpoint('projects'), project);
  }
}
