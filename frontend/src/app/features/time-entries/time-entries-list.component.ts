import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { TimeEntry, Page } from '../../core/models/models';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-time-entries-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Registro de Horas"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhum registro de horas encontrado."
      emptyIcon="schedule"
      emptyActionLabel="Criar Registro"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class TimeEntriesListComponent implements OnInit {
  items: TimeEntry[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'id,asc';
  loading = true;

  users: any[] = [];
  projects: any[] = [];
  tasks: any[] = [];

  columns: ColumnDef[] = [
    { key: 'userName', label: 'Usuário' },
    { key: 'projectName', label: 'Projeto' },
    { key: 'taskTitle', label: 'Tarefa' },
    { key: 'startTime', label: 'Hora Início' },
    { key: 'endTime', label: 'Hora Fim' },
    { key: 'durationMinutes', label: 'Duração (Min)' },
    { key: 'description', label: 'Descrição' },
  ];

  fields: FieldDef[] = [
    { key: 'userId', label: 'Usuário', type: 'select', required: true, options: [] },
    { key: 'projectId', label: 'Projeto', type: 'select', required: true, options: [] },
    { key: 'taskId', label: 'Tarefa', type: 'select', options: [] },
    { key: 'startTime', label: 'Hora Início (AAAA-MM-DDTHH:MM:SS)', type: 'text', required: true },
    { key: 'endTime', label: 'Hora Fim (AAAA-MM-DDTHH:MM:SS)', type: 'text' },
    { key: 'durationMinutes', label: 'Duração (Minutos)', type: 'number' },
    { key: 'description', label: 'Descrição', type: 'textarea' },
    { key: 'hourlyRate', label: 'Taxa Horária', type: 'number' },
  ];

  constructor(
    private svc: BaseService<TimeEntry>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadRelations();
  }

  loadRelations(): void {
    this.loading = true;
    forkJoin({
      usersPage: this.svc.getPage('users', 0, 1000, 'name,asc'),
      projectsPage: this.svc.getPage('projects', 0, 1000, 'name,asc'),
      tasksPage: this.svc.getPage('tasks', 0, 1000, 'title,asc')
    }).subscribe({
      next: (res: any) => {
        this.users = res.usersPage.content;
        this.projects = res.projectsPage.content;
        this.tasks = res.tasksPage.content;

        const userField = this.fields.find(f => f.key === 'userId');
        if (userField) userField.options = this.users.map(u => ({ value: u.id, label: u.fullName || u.name }));

        const projectField = this.fields.find(f => f.key === 'projectId');
        if (projectField) projectField.options = this.projects.map(p => ({ value: p.id, label: p.name }));

        const taskField = this.fields.find(f => f.key === 'taskId');
        if (taskField) taskField.options = [
          { value: '', label: 'Nenhuma' },
          ...this.tasks.map(t => ({ value: t.id, label: t.title }))
        ];

        this.load();
      },
      error: () => this.load()
    });
  }

  load(): void {
    this.loading = true;
    this.svc.getPage('time-entries', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<TimeEntry>) => {
        this.items = p.content;
        this.totalElements = p.totalElements;
        this.loading = false;
      },
      error: () => { this.loading = false; },
    });
  }

  onPage(e: PageEvent): void {
    this.page = e.pageIndex;
    this.pageSize = e.pageSize;
    this.load();
  }

  onSort(e: Sort): void {
    this.sort = e.active && e.direction ? `${e.active},${e.direction}` : 'id,asc';
    this.load();
  }

  openDialog(entity?: TimeEntry): void {
    const formEntity = entity ? {
      userId: entity.userId,
      projectId: entity.projectId,
      taskId: entity.taskId || '',
      startTime: entity.startTime,
      endTime: entity.endTime,
      durationMinutes: entity.durationMinutes,
      description: entity.description,
      hourlyRate: entity.hourlyRate,
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Registro de Tempo' : 'Novo Registro de Tempo',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        if (result.taskId === '') result.taskId = null;

        const op = entity
          ? this.svc.update('time-entries', entity.id!, result)
          : this.svc.create('time-entries', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: TimeEntry): void {
    if (!confirm('Deseja excluir este registro de tempo?')) return;
    this.svc.delete('time-entries', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
