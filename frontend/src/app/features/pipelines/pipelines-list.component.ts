import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { Pipeline, Page } from '../../core/models/models';

@Component({
  selector: 'app-pipelines-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Pipelines"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhum pipeline encontrado."
      emptyIcon="linear_scale"
      emptyActionLabel="Criar Pipeline"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class PipelinesListComponent implements OnInit {
  items: Pipeline[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'id,asc';
  loading = true;

  columns: ColumnDef[] = [
    { key: 'name', label: 'Nome' },
    { key: 'description', label: 'Descrição' },
    { key: 'active', label: 'Ativo' },
  ];

  fields: FieldDef[] = [
    { key: 'name', label: 'Nome', type: 'text', required: true },
    { key: 'description', label: 'Descrição', type: 'textarea' },
    { key: 'active', label: 'Ativo', type: 'checkbox' },
  ];

  constructor(
    private svc: BaseService<Pipeline>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.svc.getPage('pipelines', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<Pipeline>) => {
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
      name: entity.name,
      description: entity.description,
      active: entity.active !== undefined ? entity.active : entity.isActive,
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Pipeline' : 'Novo Pipeline',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('pipelines', entity.id!, result)
          : this.svc.create('pipelines', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: Pipeline): void {
    if (!confirm('Deseja excluir este pipeline?')) return;
    this.svc.delete('pipelines', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
