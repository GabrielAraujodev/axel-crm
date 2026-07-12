import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { Campaign, Page } from '../../core/models/models';

@Component({
  selector: 'app-campaigns-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Campanhas"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhuma campanha encontrada."
      emptyIcon="campaign"
      emptyActionLabel="Criar Campanha"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class CampaignsListComponent implements OnInit {
  items: Campaign[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'id,asc';
  loading = true;

  columns: ColumnDef[] = [
    { key: 'name', label: 'Nome' },
    { key: 'type', label: 'Tipo' },
    { key: 'status', label: 'Status' },
    { key: 'scheduledAt', label: 'Agendado Para' },
    { key: 'sentCount', label: 'Enviados' },
  ];

  fields: FieldDef[] = [
    { key: 'name', label: 'Nome', type: 'text', required: true },
    { key: 'type', label: 'Tipo', type: 'select', required: true, options: [
      { value: 'EMAIL', label: 'Email' },
      { value: 'SOCIAL', label: 'Social' },
      { value: 'PPC', label: 'PPC' },
      { value: 'SEO', label: 'SEO' },
      { value: 'EVENT', label: 'Evento' },
      { value: 'WEBINAR', label: 'Webinar' },
      { value: 'DIRECT_MAIL', label: 'Direct Mail' },
      { value: 'OTHER', label: 'Outro' }
    ]},
    { key: 'status', label: 'Status', type: 'text' },
    { key: 'content', label: 'Conteúdo', type: 'textarea' },
    { key: 'scheduledAt', label: 'Agendamento (ISO Datetime)', type: 'text' }
  ];

  constructor(
    private svc: BaseService<Campaign>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.svc.getPage('campaigns', this.page, this.pageSize, this.sort).subscribe({
      next: (res: any) => {
        if (Array.isArray(res)) {
          this.items = res;
          this.totalElements = res.length;
        } else if (res && res.content) {
          this.items = res.content;
          this.totalElements = res.totalElements;
        } else {
          this.items = [];
          this.totalElements = 0;
        }
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

  openDialog(entity?: Campaign): void {
    const formEntity = entity ? {
      name: entity.name,
      type: entity.type,
      status: entity.status,
      content: entity.content,
      scheduledAt: entity.scheduledAt,
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Campanha' : 'Nova Campanha',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('campaigns', entity.id!, result)
          : this.svc.create('campaigns', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: Campaign): void {
    if (!confirm('Deseja excluir esta campanha?')) return;
    this.svc.delete('campaigns', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluída!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
