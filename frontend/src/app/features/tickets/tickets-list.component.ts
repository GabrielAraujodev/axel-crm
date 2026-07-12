import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { SupportTicket, Page } from '../../core/models/models';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-tickets-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Tickets de Suporte"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhum ticket encontrado."
      emptyIcon="support_agent"
      emptyActionLabel="Criar Ticket"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class TicketsListComponent implements OnInit {
  items: SupportTicket[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'id,asc';
  loading = true;

  clients: any[] = [];
  users: any[] = [];

  columns: ColumnDef[] = [
    { key: 'subject', label: 'Assunto' },
    { key: 'clientName', label: 'Cliente' },
    { key: 'assignedToName', label: 'Responsável' },
    { key: 'status', label: 'Status' },
    { key: 'priority', label: 'Prioridade' },
  ];

  fields: FieldDef[] = [
    { key: 'subject', label: 'Assunto', type: 'text', required: true },
    { key: 'description', label: 'Descrição', type: 'textarea' },
    { key: 'clientId', label: 'Cliente', type: 'select', options: [] },
    { key: 'assignedToId', label: 'Responsável', type: 'select', options: [] },
  ];

  constructor(
    private svc: BaseService<SupportTicket>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadRelations();
  }

  loadRelations(): void {
    this.loading = true;
    forkJoin({
      clientsPage: this.svc.getPage('clients', 0, 1000, 'name,asc'),
      usersPage: this.svc.getPage('users', 0, 1000, 'name,asc')
    }).subscribe({
      next: (res: any) => {
        this.clients = res.clientsPage.content;
        this.users = res.usersPage.content;

        const clientField = this.fields.find(f => f.key === 'clientId');
        if (clientField) clientField.options = [
          { value: '', label: 'Nenhum' },
          ...this.clients.map(c => ({ value: c.id, label: c.name }))
        ];

        const userField = this.fields.find(f => f.key === 'assignedToId');
        if (userField) userField.options = [
          { value: '', label: 'Nenhum' },
          ...this.users.map(u => ({ value: u.id, label: u.fullName || u.name }))
        ];

        this.load();
      },
      error: () => this.load()
    });
  }

  load(): void {
    this.loading = true;
    this.svc.getPage('support-tickets', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<SupportTicket>) => {
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

  openDialog(entity?: SupportTicket): void {
    const formEntity = entity ? {
      subject: entity.subject,
      description: entity.description,
      clientId: entity.clientId || '',
      assignedToId: entity.assignedToUserId || '',
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Ticket' : 'Novo Ticket',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        if (result.clientId === '') result.clientId = null;
        if (result.assignedToId === '') result.assignedToId = null;

        const op = entity
          ? this.svc.update('support-tickets', entity.id!, result)
          : this.svc.create('support-tickets', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: SupportTicket): void {
    if (!confirm('Deseja excluir este ticket?')) return;
    this.svc.delete('support-tickets', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
