import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { User, Page } from '../../core/models/models';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Usuários"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhum usuário encontrado."
      emptyIcon="person"
      emptyActionLabel="Criar Usuário"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class UsersListComponent implements OnInit {
  items: User[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'id,asc';
  loading = true;

  columns: ColumnDef[] = [
    { key: 'fullName', label: 'Nome' },
    { key: 'email', label: 'Email' },
    { key: 'role', label: 'Perfil' },
    { key: 'active', label: 'Ativo' },
  ];

  fields: FieldDef[] = [
    { key: 'fullName', label: 'Nome', type: 'text', required: true },
    { key: 'email', label: 'Email', type: 'email', required: true },
    { key: 'password', label: 'Senha (Deixe em branco para manter a mesma ao editar)', type: 'password' },
    { key: 'role', label: 'Perfil', type: 'select', options: [
      { value: 'ADMIN', label: 'Admin' },
      { value: 'MANAGER', label: 'Gerente' },
      { value: 'USER', label: 'Usuário' }
    ]},
    { key: 'active', label: 'Ativo', type: 'checkbox' }
  ];

  constructor(
    private svc: BaseService<User>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.svc.getPage('users', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<User>) => {
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

  openDialog(entity?: any): void {
    const formEntity = entity ? {
      fullName: entity.fullName,
      email: entity.email,
      role: entity.role,
      active: entity.active,
      password: '',
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Usuário' : 'Novo Usuário',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const body = { ...result };
        if (!body.password) {
          delete body.password;
        }
        const op = entity
          ? this.svc.update('users', entity.id!, body)
          : this.svc.create('users', body);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: User): void {
    if (!confirm('Deseja excluir este usuário?')) return;
    this.svc.delete('users', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
