import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { Contact, Client, Page } from '../../core/models/models';

@Component({
  selector: 'app-contacts-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Contatos"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhum contato encontrado."
      emptyIcon="contacts"
      emptyActionLabel="Criar Contato"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class ContactsListComponent implements OnInit {
  items: Contact[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'name,asc';
  loading = true;

  clients: Client[] = [];

  contactTypes = [
    { value: 'LAWYER', label: 'Advogado' },
    { value: 'JUDGE', label: 'Juiz' },
    { value: 'CLIENT', label: 'Cliente' },
    { value: 'TECHNICAL_ASSISTANT', label: 'Assistente Técnico' },
    { value: 'OTHER', label: 'Outro' }
  ];

  columns: ColumnDef[] = [
    { key: 'firstName', label: 'Nome' },
    { key: 'lastName', label: 'Sobrenome' },
    { key: 'email', label: 'Email' },
    { key: 'phone', label: 'Telefone' },
    { key: 'jobTitle', label: 'Cargo' },
    { key: 'contactTypeFormatted', label: 'Tipo' },
    { key: 'clientName', label: 'Cliente' },
  ];

  fields: FieldDef[] = [
    { key: 'firstName', label: 'Nome', type: 'text', required: true },
    { key: 'lastName', label: 'Sobrenome', type: 'text' },
    { key: 'email', label: 'Email', type: 'email' },
    { key: 'phone', label: 'Telefone', type: 'text' },
    { key: 'jobTitle', label: 'Cargo', type: 'text' },
    { key: 'department', label: 'Departamento', type: 'text' },
    { key: 'contactType', label: 'Tipo de Contato', type: 'select', required: true, options: this.contactTypes },
    { key: 'clientId', label: 'Cliente Relacionado', type: 'select', required: true, options: [] },
    { key: 'notes', label: 'Anotações', type: 'textarea' }
  ];

  constructor(
    private svc: BaseService<Contact>,
    private clientSvc: BaseService<Client>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void { 
    this.loadClients();
  }

  loadClients(): void {
    this.loading = true;
    this.clientSvc.getPage('clients', 0, 1000, 'name,asc').subscribe({
      next: (p: Page<Client>) => {
        this.clients = p.content;
        const clientField = this.fields.find(f => f.key === 'clientId');
        if (clientField) {
          clientField.options = this.clients.map(c => ({ value: c.id, label: c.name }));
        }
        this.load();
      },
      error: () => {
        this.snackBar.open('Erro ao carregar clientes', 'OK', { duration: 3000 });
        this.load();
      }
    });
  }

  load(): void {
    this.loading = true;
    this.svc.getPage('contacts', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<Contact>) => {
        this.items = p.content.map(contact => ({
          ...contact,
          clientName: contact.client?.name,
          contactTypeFormatted: this.contactTypes.find(t => t.value === contact.contactType)?.label || 'Outro'
        }));
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
    this.sort = e.active && e.direction ? `${e.active},${e.direction}` : 'name,asc';
    this.load();
  }

  openDialog(entity?: any): void {
    const formEntity = entity ? {
      firstName: entity.firstName,
      lastName: entity.lastName,
      email: entity.email,
      phone: entity.phone,
      jobTitle: entity.jobTitle,
      department: entity.department,
      contactType: entity.contactType || 'OTHER',
      clientId: entity.client?.id || entity.clientId,
      notes: entity.notes
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Contato' : 'Novo Contato',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('contacts', entity.id!, result)
          : this.svc.create('contacts', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: Contact): void {
    if (!confirm('Deseja excluir este contato?')) return;
    this.svc.delete('contacts', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
