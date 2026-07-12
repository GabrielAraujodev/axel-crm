import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { Document, Page } from '../../core/models/models';

@Component({
  selector: 'app-documents-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Documentos"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhum documento encontrado."
      emptyIcon="folder_open"
      emptyActionLabel="Criar Documento"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class DocumentsListComponent implements OnInit {
  items: Document[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'name,asc';
  loading = true;

  columns: ColumnDef[] = [
    { key: 'name', label: 'Nome' },
    { key: 'category', label: 'Categoria' },
    { key: 'clientName', label: 'Cliente' },
    { key: 'documentDate', label: 'Data' },
    { key: 'archived', label: 'Arquivado' },
  ];

  fields: FieldDef[] = [
    { key: 'name', label: 'Nome', type: 'text', required: true },
    { key: 'description', label: 'Descrição', type: 'textarea' },
    {
      key: 'category', label: 'Categoria', type: 'select',
      options: [
        { value: 'CONTRACT', label: 'Contrato' },
        { value: 'PROPOSAL', label: 'Proposta' },
        { value: 'REPORT', label: 'Relatório' },
        { value: 'INVOICE', label: 'Fatura' },
        { value: 'NOTE', label: 'Anotação' },
        { value: 'LEGAL', label: 'Jurídico' },
        { value: 'OTHER', label: 'Outro' },
      ]
    },
    { key: 'tags', label: 'Tags', type: 'text' },
    { key: 'fileName', label: 'Nome do Arquivo', type: 'text' },
    { key: 'fileType', label: 'Tipo de Arquivo', type: 'text' },
    { key: 'fileSize', label: 'Tamanho (bytes)', type: 'number' },
    { key: 'fileUrl', label: 'URL do Arquivo', type: 'text' },
    { key: 'clientId', label: 'Cliente', type: 'select', options: [] },
    { key: 'dealId', label: 'Negócio', type: 'select', options: [] },
    { key: 'contractId', label: 'Contrato', type: 'select', options: [] },
    { key: 'projectId', label: 'Projeto', type: 'select', options: [] },
    { key: 'documentDate', label: 'Data do Documento', type: 'date' },
    { key: 'expiryDate', label: 'Data de Validade', type: 'date' },
    { key: 'archived', label: 'Arquivado', type: 'checkbox' },
  ];

  clients: any[] = [];
  deals: any[] = [];
  contracts: any[] = [];
  projects: any[] = [];

  constructor(
    private svc: BaseService<Document>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadRelations();
  }

  loadRelations(): void {
    this.loading = true;
    this.svc.getPage('clients', 0, 1000, 'name,asc').subscribe({
      next: (cPage: any) => {
        this.clients = cPage.content;
        const cf = this.fields.find(f => f.key === 'clientId');
        if (cf) cf.options = this.clients.map(c => ({ value: c.id, label: c.name }));
        this.svc.getPage('deals', 0, 1000, 'title,asc').subscribe({
          next: (dPage: any) => {
            this.deals = dPage.content;
            const df = this.fields.find(f => f.key === 'dealId');
            if (df) df.options = [{ value: null, label: 'Nenhum' }, ...this.deals.map(d => ({ value: d.id, label: d.title }))];
            this.svc.getPage('contracts', 0, 1000, 'title,asc').subscribe({
              next: (kPage: any) => {
                this.contracts = kPage.content;
                const kf = this.fields.find(f => f.key === 'contractId');
                if (kf) kf.options = [{ value: null, label: 'Nenhum' }, ...this.contracts.map(k => ({ value: k.id, label: k.title }))];
                this.svc.getPage('projects', 0, 1000, 'name,asc').subscribe({
                  next: (pPage: any) => {
                    this.projects = pPage.content;
                    const pf = this.fields.find(f => f.key === 'projectId');
                    if (pf) pf.options = [{ value: null, label: 'Nenhum' }, ...this.projects.map(p => ({ value: p.id, label: p.name }))];
                    this.load();
                  },
                  error: () => this.load(),
                });
              },
              error: () => this.load(),
            });
          },
          error: () => this.load(),
        });
      },
      error: () => this.load(),
    });
  }

  load(): void {
    this.loading = true;
    this.svc.getPage('documents', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<Document>) => {
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
    this.sort = e.active && e.direction ? `${e.active},${e.direction}` : 'name,asc';
    this.load();
  }

  openDialog(entity?: any): void {
    const formEntity = entity ? {
      name: entity.name,
      description: entity.description,
      category: entity.category,
      tags: entity.tags,
      fileName: entity.fileName,
      fileType: entity.fileType,
      fileSize: entity.fileSize,
      fileUrl: entity.fileUrl,
      clientId: entity.clientId,
      dealId: entity.dealId,
      contractId: entity.contractId,
      projectId: entity.projectId,
      documentDate: entity.documentDate,
      expiryDate: entity.expiryDate,
      archived: entity.archived,
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Documento' : 'Novo Documento',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '600px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('documents', entity.id!, result)
          : this.svc.create('documents', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.loadRelations();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: Document): void {
    if (!confirm('Deseja excluir este documento?')) return;
    this.svc.delete('documents', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
