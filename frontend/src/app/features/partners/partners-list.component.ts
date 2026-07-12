import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ListPageComponent, ColumnDef } from '../../shared/list-page/list-page.component';
import { FormDialogComponent, FieldDef, FormDialogData } from '../../shared/form-dialog/form-dialog.component';
import { BaseService } from '../../core/services/base.service';
import { Partner, Page } from '../../core/models/models';

@Component({
  selector: 'app-partners-list',
  standalone: true,
  imports: [ListPageComponent],
  template: `
    <app-list-page
      title="Parceiros / Indicadores"
      [columns]="columns"
      [data]="items"
      [totalElements]="totalElements"
      [pageSize]="pageSize"
      [loading]="loading"
      emptyMessage="Nenhum parceiro encontrado."
      emptyIcon="handshake"
      emptyActionLabel="Criar Parceiro"
      (pageChange)="onPage($event)"
      (sortChange)="onSort($event)"
      (add)="openDialog()"
      (edit)="openDialog($event)"
      (remove)="onDelete($event)"
    ></app-list-page>
  `,
})
export class PartnersListComponent implements OnInit {
  items: Partner[] = [];
  totalElements = 0;
  pageSize = 10;
  page = 0;
  sort = 'name,asc';
  loading = true;

  columns: ColumnDef[] = [
    { key: 'name', label: 'Nome' },
    { key: 'company', label: 'Empresa' },
    { key: 'totalReferrals', label: 'Indicações' },
    { key: 'proposalsSent', label: 'Propostas' },
    { key: 'conversionRateFormatted', label: 'Conversão' },
    { key: 'commissionPercentageFormatted', label: 'Comissão Padrão' }
  ];

  fields: FieldDef[] = [
    { key: 'name', label: 'Nome', type: 'text', required: true },
    { key: 'email', label: 'Email', type: 'email' },
    { key: 'phone', label: 'Telefone', type: 'text' },
    { key: 'company', label: 'Empresa', type: 'text' },
    { key: 'commissionPercentage', label: 'Comissão (%)', type: 'number' },
    { key: 'bankDetails', label: 'Dados Bancários (Banco, Ag, CC, Pix)', type: 'textarea' }
  ];

  constructor(
    private svc: BaseService<Partner>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void { 
    this.load(); 
  }

  load(): void {
    this.loading = true;
    this.svc.getPage('partners', this.page, this.pageSize, this.sort).subscribe({
      next: (p: Page<Partner>) => {
        this.items = p.content.map(partner => ({
          ...partner,
          conversionRateFormatted: partner.conversionRate ? `${partner.conversionRate}%` : '0%',
          commissionPercentageFormatted: partner.commissionPercentage ? `${partner.commissionPercentage}%` : '0%'
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
      name: entity.name,
      email: entity.email,
      phone: entity.phone,
      company: entity.company,
      commissionPercentage: entity.commissionPercentage,
      bankDetails: entity.bankDetails
    } : undefined;

    const data: FormDialogData = {
      title: entity ? 'Editar Parceiro' : 'Novo Parceiro',
      fields: this.fields,
      entity: formEntity,
    };

    this.dialog.open(FormDialogComponent, { data, width: '520px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        const op = entity
          ? this.svc.update('partners', entity.id!, result)
          : this.svc.create('partners', result);
        op.subscribe({
          next: () => {
            this.snackBar.open('Salvo com sucesso!', 'OK', { duration: 3000 });
            this.load();
          },
          error: () => this.snackBar.open('Erro ao salvar', 'OK', { duration: 3000 }),
        });
      });
  }

  onDelete(entity: Partner): void {
    if (!confirm('Deseja excluir este parceiro?')) return;
    this.svc.delete('partners', entity.id!).subscribe({
      next: () => {
        this.snackBar.open('Excluído!', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Erro ao excluir', 'OK', { duration: 3000 }),
    });
  }
}
