import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { Product, Page } from '../../core/models/models';

@Component({
  selector: 'app-products-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Catálogo de Produtos"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhum produto encontrado."
      emptyIcon="inventory_2"
      emptyActionLabel="Criar Produto"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class ProductsListComponent implements OnInit {
  items: Product[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'name,asc';
  loading = true;

  columns: ColumnDef[] = [
    { key: 'name', label: 'Nome' },
    { key: 'category', label: 'Categoria' },
    { key: 'unitPrice', label: 'Preço Unit.' },
    { key: 'unit', label: 'Unidade' },
    { key: 'sku', label: 'SKU' },
    { key: 'isActive', label: 'Ativo' },
  ];

  fields: FieldDef[] = [
    { key: 'name', label: 'Nome', type: 'text', required: true },
    { key: 'description', label: 'Descrição', type: 'textarea' },
    { key: 'sku', label: 'SKU / Código', type: 'text' },
    {
      key: 'category', label: 'Categoria', type: 'select',
      options: [
        { value: 'SERVICE', label: 'Serviço' },
        { value: 'PRODUCT', label: 'Produto' },
        { value: 'SOFTWARE', label: 'Software' },
        { value: 'CONSULTING', label: 'Consultoria' },
      ]
    },
    { key: 'unitPrice', label: 'Preço Unitário', type: 'number' },
    { key: 'costPrice', label: 'Preço de Custo', type: 'number' },
    {
      key: 'unit', label: 'Unidade', type: 'select',
      options: [
        { value: 'hour', label: 'Hora' },
        { value: 'day', label: 'Dia' },
        { value: 'month', label: 'Mês' },
        { value: 'unit', label: 'Unidade' },
        { value: 'project', label: 'Projeto' },
      ]
    },
    { key: 'isActive', label: 'Ativo', type: 'checkbox' },
    { key: 'notes', label: 'Observações', type: 'textarea' },
  ];

  constructor(
    private svc: BaseService<Product>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.svc.getPage('products', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<Product>) => {
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
      sku: entity.sku,
      category: entity.category,
      unitPrice: entity.unitPrice,
      costPrice: entity.costPrice,
      unit: entity.unit,
      isActive: entity.isActive,
      notes: entity.notes,
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Produto' : 'Novo Produto',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '600px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('products', entity.id!, result)
          : this.svc.create('products', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: Product): void {
    if (!confirm('Deseja excluir este produto?')) return;
    this.svc.delete('products', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
